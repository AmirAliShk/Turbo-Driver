package ir.team_x.cloud_transport.driver.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.team_x.cloud_transport.driver.app.EndPoint
import ir.team_x.cloud_transport.driver.app.MyApplication
import ir.team_x.cloud_transport.driver.databinding.FragmentVerificationBinding
import ir.team_x.cloud_transport.driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.driver.okHttp.RequestHelper
import ir.team_x.cloud_transport.driver.push.AvaCrashReporter
import ir.team_x.cloud_transport.driver.utils.FragmentHelper
import ir.team_x.cloud_transport.driver.utils.KeyBoardHelper
import ir.team_x.cloud_transport.driver.utils.PhoneNumberValidation
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil
import org.json.JSONObject


class VerificationFragment : Fragment() {
    lateinit var binding: FragmentVerificationBinding
    lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)

        binding.btnLogin.setOnClickListener {
            mobileNumber = binding.edtMobilNo.text.toString()

            if (mobileNumber.isEmpty()) {
                binding.edtMobilNo.error = "شماره موبایل را وارد کنید."
                return@setOnClickListener
            }

            if (!PhoneNumberValidation.isValid(mobileNumber)) {
                binding.edtMobilNo.error = "شماره موبایل نامعتبر میباشد."
                return@setOnClickListener
            }

            mobileNumber = if (mobileNumber.startsWith("0")) mobileNumber else "0$mobileNumber"

            KeyBoardHelper.hideKeyboard()
            verification(mobileNumber)
        }

        return binding.root
    }

    private fun verification(phoneNumber: String) {
        binding.vfSubmit.displayedChild = 1
        RequestHelper.builder(EndPoint.VERIFICATION)
            .addParam("phoneNumber", phoneNumber)
            .listener(onVerificationCallBack)
            .post()
    }

    private val onVerificationCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable, vararg args: Any) {
            MyApplication.handler.post {
                try {
                    binding.vfSubmit.displayedChild = 0
//                    {"success":true,"message":"با موفقیت ارسال شد","data":{"repetitionTime":120}}
                    val resObj = JSONObject(args[0].toString())
                    val success = resObj.getBoolean("success")
                    val message = resObj.getString("message")
                    if (success) {
                        val objData = resObj.getJSONObject("data")
                        val repetitionTime = objData.getInt("repetitionTime")
                        MyApplication.prefManager.setRepetitionTime(repetitionTime)
                        val bundle = Bundle()
                        bundle.putString("mobileNumber", mobileNumber)
                        FragmentHelper
                            .toFragment(MyApplication.currentActivity, CheckVerificationFragment())
                            .setArguments(bundle)
                            .setAddToBackStack(false)
                            .replace()
                        MyApplication.Toast(message, Toast.LENGTH_SHORT)
                    } else {
                        GeneralDialog().message(message).secondButton("باشه") {}.cancelable(false)
                            .show()
                    }

                } catch (e: Exception) {
                    binding.vfSubmit.displayedChild = 0
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
                binding.vfSubmit.displayedChild = 0
            }
        }
    }


}