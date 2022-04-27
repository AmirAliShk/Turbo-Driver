package ir.transport_x.taxi.fragment.login

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentVerificationBinding
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.KeyBoardHelper
import ir.transport_x.taxi.utils.PhoneNumberValidation
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONObject

class VerificationFragment : Fragment() {
    lateinit var binding: FragmentVerificationBinding
    lateinit var mobileNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity?.window!!
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window.navigationBarColor = resources.getColor(R.color.grayLighter)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightStatusBars = false
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightNavigationBars = true
        }
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
            .addParam("domain", MyApplication.context.packageName)
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
                            .setFrame(android.R.id.content)
                            .replace()
                        MyApplication.Toast(message, Toast.LENGTH_SHORT)
                    } else {
                        GeneralDialog().message(message).secondButton("باشه") {}
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