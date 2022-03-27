package ir.transport_x.taxi.fragment.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.app.AppKeys
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentCheckVerificationBinding
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.KeyBoardHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import ir.transport_x.taxi.webServices.GetAppInfo
import org.json.JSONObject
import java.util.*

class CheckVerificationFragment : Fragment() {

    lateinit var binding: FragmentCheckVerificationBinding
    lateinit var phoneNumber: String
    private lateinit var countDownTimer: CountDownTimer
    lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckVerificationBinding.inflate(layoutInflater)
        TypeFaceUtil.overrideFont(binding.root)

        binding.txtRules.paintFlags = (Paint.UNDERLINE_TEXT_FLAG)

        val bundle = arguments
        if (bundle != null) {
            phoneNumber = bundle.getString("mobileNumber")!!
            binding.txtPhoneNumber.text = phoneNumber
        }

        startWaitingTime()

        binding.pin.setOnEditorActionListener { v, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_DONE) {
                code = binding.pin.text.toString()
                when {
                    code.isEmpty() -> {
                        MyApplication.Toast("کد را وارد کنید", Toast.LENGTH_SHORT)
                    }
                    !binding.cbRules.isChecked -> {
                        MyApplication.Toast(
                            "لطفا قوانین و مقررات را قبول نمایید",
                            Toast.LENGTH_SHORT
                        )
                    }
                    else -> {
                        checkVerification()
                    }
                }
                KeyBoardHelper.hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.llResendCode.setOnClickListener {
            verification(phoneNumber)
        }

        binding.llChangeNumber.setOnClickListener {
            if (countDownTimer != null) countDownTimer.cancel()
            FragmentHelper
                .toFragment(MyApplication.currentActivity, VerificationFragment())
                .setAddToBackStack(false)
                .setFrame(android.R.id.content)
                .replace()
        }

        binding.llRules.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(EndPoint.RULL)
            MyApplication.currentActivity.startActivity(i)
        }

        binding.btnLogin.setOnClickListener {
            code = binding.pin.text.toString()

            if (code.isEmpty()) {
                MyApplication.Toast("کد را وارد کنید", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            if (!binding.cbRules.isChecked) {
                MyApplication.Toast("لطفا قوانین و مقررات را قبول نمایید", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            checkVerification()
        }

        return binding.root
    }

    private fun verification(phoneNumber: String) {
        RequestHelper.builder(EndPoint.VERIFICATION)
            .addParam("phoneNumber", phoneNumber)
            .listener(onVerificationCallBack)
            .post()
    }

    private val onVerificationCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable, vararg args: Any) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"با موفقیت ارسال شد","data":{"repetitionTime":120}}
                    val resObj = JSONObject(args[0].toString())
                    val success = resObj.getBoolean("success")
                    val message = resObj.getString("message")
                    if (success) {
                        binding.vfTime.displayedChild= 0
                        startWaitingTime()
                        MyApplication.Toast(message, Toast.LENGTH_SHORT)
                        val objData = resObj.getJSONObject("data")
                        val repetitionTime = objData.getInt("repetitionTime")
                        MyApplication.prefManager.setRepetitionTime(repetitionTime)
                    } else {
                        GeneralDialog().message(message).secondButton("باشه") {}
                            .show()
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                    AvaCrashReporter.send(
                        e,
                        "VerificationFragment class, onVerificationCallBack onResponse method"
                    )
                }
            }
        }

        override fun onFailure(reCall: Runnable, e: Exception) {
            MyApplication.handler.post {

            }
        }
    }

    private fun checkVerification() {
        binding.vfSubmit.displayedChild = 1
        RequestHelper.builder(EndPoint.CHECK)
            .addParam("phoneNumber", phoneNumber)
            .addParam("scope", AppKeys.SCOPE)
            .addParam("code", code)
            .addParam("domain", MyApplication.context.packageName)
            .listener(onCheckVerificationCallBack)
            .post()
    }

    private val onCheckVerificationCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable, vararg args: Any) {
                MyApplication.handler.post {
                    try {
                        binding.vfSubmit.displayedChild = 0
//                  {"success":true,"message":"با موفقیت وارد شدید","data":{"id_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzLCJ1c2VybmFtZSI6IjEyMzQiLCJpYXQiOjE2MDkzMjg2NTYsImV4cCI6MTYwOTMyODk1Nn0.u_twFCxWzu73CMkPtb73Q0WdgzozgWKbgZYSmzlIgHg","access_token":"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3R1cmJvdGF4aS5pciIsImF1ZCI6IlVzZXJzIiwiZXhwIjoxNjA5MzI4OTU2LCJzY29wZSI6Im9wZXJhdG9yIiwic3ViIjoidHVyYm90YXhpIiwianRpIjoiNkQ0OTc3ODI3NzFGN0ZEMSIsImFsZyI6IkhTMjU2IiwiaWF0IjoxNjA5MzI4NjU2fQ.8Ssz4-AhK10cy8ma1635iIgquj9gtHHB4S1ETyioRN4","refresh_token":"kTDDNxxc4tQVrN1qQhQFBXZE5qFu3mbelgEbExnsnUElmZv0fFUDpOilLVeOegN5nDCX92mlahXHxP7hWjN52AoOZnZbDG7nz7mcqjowrpxiAgjWsHw5DeOW0RBvadgnRXGEYYS9YByTrYwTL3C4VZEY0DzeTzVyfZsRG2D8LX1jeE87yDx7Afe8D0em4htKfM1KvMWlptdMQbrZrE6yZRuvofubZAFgHgazoi8EDfiWtanu5jNiW86KuPJgbC0r"}}
                        val resObj = JSONObject(args[0].toString())
                        val success = resObj.getBoolean("success")
                        val message = resObj.getString("message")
                        if (success) {
                            val data = resObj.getJSONObject("data")
                            MyApplication.prefManager.setIdToken(data.getString("id_token"))
                            MyApplication.prefManager.setAuthorization(data.getString("access_token"))
                            MyApplication.prefManager.setRefreshToken(data.getString("refresh_token"))
                            GetAppInfo().callAppInfoAPI()
                        } else {
                            //                        {"success":false,"message":".اطلاعات صحیح نمی باشد","data":{}}
                            GeneralDialog().message(message).secondButton("باشه") {}.show()
                        }
                    } catch (e: java.lang.Exception) {
                        binding.vfSubmit.displayedChild = 0
                        e.printStackTrace()
                        AvaCrashReporter.send(
                            e,
                            "CheckVerificationFragment class, CheckVerificationFragment onResponse method"
                        )
                    }
                }
            }

            override fun onFailure(reCall: Runnable, e: java.lang.Exception) {
                MyApplication.handler.post {
                    binding.vfSubmit.displayedChild = 0
                }
            }
        }

    private fun startWaitingTime() {
        if (MyApplication.prefManager.getActivationRemainingTime() < Calendar.getInstance()
                .timeInMillis
        ) MyApplication.prefManager.setActivationRemainingTime(
            Calendar.getInstance()
                .timeInMillis + MyApplication.prefManager.getRepetitionTime() * 1000
        )
        countDownTimer()
    }

    private fun countDownTimer() {
        val remainingTime: Long =
            MyApplication.prefManager.getActivationRemainingTime() - Calendar.getInstance()
                .timeInMillis
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.vfTime.displayedChild = 0
                binding.txtResendCode.text = millisUntilFinished.div(1000).toString()
            }

            override fun onFinish() {
                binding.vfTime.displayedChild = 1

            }
        }.start()
    }

}