package ir.transport_x.taxi.fragment.financial

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.adapter.ValueAdapter
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentOnlinePaymentBinding
import ir.transport_x.taxi.model.ValueModel
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import org.json.JSONArray

class OnlinePaymentFragment(private val onlineObj: String) : Fragment() {
    private lateinit var binding: FragmentOnlinePaymentBinding
    var price = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlinePaymentBinding.inflate(inflater, container, false)
        TypeFaceUtilJava.overrideFonts(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        setCursorEnd(binding.root)

        val priceArr = JSONArray(onlineObj)
        val values: ArrayList<ValueModel> = ArrayList()
        for (i in 0 until priceArr.length()) {
            val item: String = priceArr.getString(i)
            val model = ValueModel(item, false)
            values.add(model)
        }
        val valueAdapter = ValueAdapter(values, object : ValueAdapter.SelectedValue {
            override fun getSelected(s: String) {
                price = s
                binding.edtValueCredit.setText(price)
            }
        })
        binding.gridView.adapter = valueAdapter

        StringHelper.setCommaOnTime(binding.edtValueCredit)

        binding.btnSubmit.setOnClickListener {

            if (binding.edtValueCredit.text.toString() == "") {
                binding.edtValueCredit.error = "مبلغ وارد نشده است."
                return@setOnClickListener
            }

            val price = StringHelper.extractTheNumber(binding.edtValueCredit.text.toString())
            if (price.toInt() < 10000) {
                binding.edtValueCredit.error = "حداقل مبلغ ورودی 10,000 تومان میباشد."
                binding.edtValueCredit.setText(StringHelper.setComma("5000"))
                return@setOnClickListener
            }

            if (price.toInt() > 100000) {
                binding.edtValueCredit.error = "حداکثر مبلغ ورودی 100,000 تومان میباشد."
                binding.edtValueCredit.setText(StringHelper.setComma("100000"))
                return@setOnClickListener
            }
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        MyApplication.prefManager.onlineUrl + StringHelper.toEnglishDigits(
                            price
                        ) + "/" + MyApplication.prefManager.getDriverId()
                    )
                )
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

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
            AvaCrashReporter.send(e, "$TAG class, setCursorEnd method")
            // ignore
        }
    }

    companion object {
        @JvmField
        val TAG = OnlinePaymentFragment::class.java.simpleName
    }

}