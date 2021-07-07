package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentAtmBinding
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import org.json.JSONObject

class ATMFragment : Fragment() {

    private lateinit var binding: FragmentAtmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAtmBinding.inflate(inflater, container, false)
        TypeFaceUtilJava.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.txtReport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, PaymentReportFragment())
                .replace()
        }

        binding.priceGroup.check(R.id.ten)
        binding.priceGroup.setOnItemClickListener { selectedId ->
            var price = "30000"
            when (selectedId) {
                R.id.ten -> price = "10000"
                R.id.fifteen -> price = "15000"
                R.id.twenty -> price = "20000"
                R.id.twentyFive -> price = "25000"
                R.id.thirty -> price = "30000"
                R.id.thirtyFive -> price = "35000"
            }
            binding.edtValueCredit.setText(StringHelper.setComma(price))
        }

        StringHelper.setCharAfterOnTime(binding.edtCardNumber, "-", 4)

        binding.btnSubmit.setOnClickListener {
            val cardNumber = binding.edtCardNumber.text.trim().toString()
            val bankName = binding.edtBankName.text.trim().toString()
            val tracking = binding.edtTrackingCode.text.trim().toString()
            val price = binding.edtValueCredit.text.trim().toString()
            val desc = binding.edtDesc.text.trim().toString()

            if (cardNumber.isEmpty()) {
                MyApplication.Toast("شماره کارت را وارد کنید", Toast.LENGTH_SHORT)
                binding.edtCardNumber.requestFocus()
                return@setOnClickListener
            }

            if (bankName.isEmpty()) {
                MyApplication.Toast("نام بانک را وارد کنید", Toast.LENGTH_SHORT)
                binding.edtBankName.requestFocus()
                return@setOnClickListener
            }

            if (tracking.isEmpty()) {
                MyApplication.Toast("کد پیگیری فیش را وارد کنید", Toast.LENGTH_SHORT)
                binding.edtTrackingCode.requestFocus()
                return@setOnClickListener
            }

            if (price.isEmpty()) {
                MyApplication.Toast("مبلغ را وارد کنید", Toast.LENGTH_SHORT)
                binding.edtValueCredit.requestFocus()
                return@setOnClickListener
            }

            val p = StringHelper.extractTheNumber(price).toInt()
            if (p < 2000) {
                binding.edtValueCredit.setText(StringHelper.setComma("2000"))
                MyApplication.Toast("مبلغ وارد شده کمتر حد مجاز میباشد", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            atmPayment(cardNumber, bankName, tracking, price, desc)
        }

        return binding.root
    }

    private fun atmPayment(
        cardNumber: String,
        bankName: String,
        tracking: String,
        price: String,
        dest: String
    ) {
        binding.vfSubmit.displayedChild = 1
        RequestHelper.builder(EndPoint.ATM)
            .listener(ATMCallBack)
            .addParam("cardNumber", StringHelper.extractTheNumber(cardNumber))
            .addParam("bankName", bankName)
            .addParam("trackingCode", tracking)
            .addParam("price", StringHelper.extractTheNumber(price))
            .addParam("description", dest)
            .post()
    }

    private val ATMCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfSubmit.displayedChild = 0
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val backStatus = dataObj.getInt("backStatus")
                        val msg = dataObj.getString("message")
                        if (backStatus == 1) {
                            clearPage()
                            GeneralDialog().message(msg).firstButton("باشه") {}.show()
                        } else {
                            GeneralDialog().message(msg).secondButton("باشه") {}.show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfSubmit.displayedChild = 0
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfSubmit.displayedChild = 0
            }
        }
    }

private fun clearPage(){
    binding.edtCardNumber.setText("")
    binding.edtBankName.setText("")
    binding.edtTrackingCode.setText("")
    binding.edtDesc.setText("")

}

}