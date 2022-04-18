package ir.transport_x.taxi.fragment.financial

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentAtmBinding
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.room.CardNumber
import ir.transport_x.taxi.room.MyDB
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import org.json.JSONObject

class ATMFragment(private var ATMObj: JSONObject, var price: String) : Fragment() {

    lateinit var binding: FragmentAtmBinding
    var dataBase: MyDB = MyDB.getDataBase(MyApplication.context)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAtmBinding.inflate(inflater, container, false)
        TypeFaceUtilJava.overrideFonts(binding.root)
        TypeFaceUtilJava.overrideFonts(binding.txtTitle, MyApplication.iranSansMediumTF)

        fillCards()
        setCursorEnd(binding.root)

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.txtReport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, PaymentReportFragment())
                .replace()
        }

        binding.txtAttention.text = StringHelper.toPersianDigits(ATMObj.getString("warningTxt"))
        binding.txtWarning.text = StringHelper.toPersianDigits(ATMObj.getString("warningTxt2"))
        binding.justifiedTextViewImportance.text =
            StringHelper.toPersianDigits(ATMObj.getString("attentionTxt"))
        binding.txtPrice.text = StringHelper.toPersianDigits(price)

        if (ATMObj.getString("warningTxt") == "") {
            binding.llAttention.visibility = View.GONE
        }
        if (ATMObj.getString("warningTxt2") == "") {
            binding.llWarning.visibility = View.GONE
        }
        if (ATMObj.getString("attentionTxt") == "") {
            binding.llJustifiedTextViewImportance.visibility = View.GONE
        }

        StringHelper.setCharAfterOnTime(binding.edtCardNumber, " - ", 4)

        binding.btnSubmit.setOnClickListener {
            val cardNumber = binding.edtCardNumber.text.trim().toString()
            val bankName = binding.edtBankName.text.trim().toString()
            val tracking = binding.edtTrackingCode.text.trim().toString()
            val price = binding.txtPrice.text.trim().toString()
            val desc = binding.edtDesc.text.trim().toString()

            if (cardNumber.isEmpty()) {
                MyApplication.Toast("شماره کارت را وارد کنید", Toast.LENGTH_SHORT)
                binding.edtCardNumber.requestFocus()
                return@setOnClickListener
            }

            if (cardNumber.length < 16) {
                MyApplication.Toast("شماره کارت را اصلاح کنید", Toast.LENGTH_SHORT)
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
                binding.txtPrice.requestFocus()
                return@setOnClickListener
            }

            val p = StringHelper.extractTheNumber(price).toInt()
            if (p < 10000) {
                binding.txtPrice.text = StringHelper.setComma("10000")
                binding.txtPrice.requestFocus()
                MyApplication.Toast("مبلغ وارد شده کمتر حد مجاز میباشد.", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }

            GeneralDialog().message("از ثبت واریزی اطمینان دارید؟")
                .firstButton("بله") {
                    dataBase.cardNumberDao()
                        .insertCardNo(CardNumber(cardNo = cardNumber, bankName = bankName))
                    atmPayment(cardNumber, bankName, tracking, price, desc)
                }
                .secondButton("خیر") {}
                .show()
        }

        return binding.root
    }

    private fun setCursorEnd(v: View?) {
        try {
            if (v is ViewGroup) {
                for (i in 0 until v.childCount) {
                    val child = v.getChildAt(i)
                    setCursorEnd(child)
                }
            } else if (v is EditText) {
                v.onFocusChangeListener = View.OnFocusChangeListener { view: View?, b: Boolean ->
                    if (b) MyApplication.handler.postDelayed(
                        { v.setSelection(v.text.length) },
                        200
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "ATMFragment class, setCursorEnd method")
            // ignore
        }
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

    @SuppressLint("ClickableViewAccessibility")
    private fun fillCards() {
        val restaurants: List<String> = dataBase.cardNumberDao().getCardNo()
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            MyApplication.currentActivity,
            R.layout.item_bank_card,
            restaurants
        )
        binding.edtCardNumber.threshold = 1
        binding.edtCardNumber.setAdapter(adapter)

        binding.edtCardNumber.setOnTouchListener { v, event ->
            binding.edtCardNumber.showDropDown()
            false
        }
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

    private fun clearPage() {
        binding.edtCardNumber.setText("")
        binding.edtBankName.setText("")
        binding.edtTrackingCode.setText("")
        binding.edtDesc.setText("")

    }

}