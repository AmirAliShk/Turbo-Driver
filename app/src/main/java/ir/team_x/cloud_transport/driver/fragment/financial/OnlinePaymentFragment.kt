package ir.team_x.cloud_transport.driver.fragment.financial

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import ir.team_x.cloud_transport.driver.R
import ir.team_x.cloud_transport.driver.app.EndPoint
import ir.team_x.cloud_transport.driver.app.MyApplication
import ir.team_x.cloud_transport.driver.databinding.FragmentOnlinePaymentBinding
import ir.team_x.cloud_transport.driver.push.AvaCrashReporter
import ir.team_x.cloud_transport.driver.utils.StringHelper
import ir.team_x.cloud_transport.driver.utils.TypeFaceUtilJava
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil


class OnlinePaymentFragment : Fragment() {
    private lateinit var binding: FragmentOnlinePaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlinePaymentBinding.inflate(inflater, container, false)
        TypeFaceUtilJava.overrideFonts(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle,MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        setCursorEnd(binding.root)
        StringHelper.setCommaOnTime(binding.edtValueCredit)
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

        binding.btnSubmit.setOnClickListener {

            if (binding.edtValueCredit.text.toString() == "") {
                binding.edtValueCredit.error = "مبلغ وارد نشده است"
                return@setOnClickListener
            }

            val price = StringHelper.extractTheNumber(binding.edtValueCredit.text.toString())
            if (price.toInt() < 10000) {
                binding.edtValueCredit.error = "حداقل مبلغ ورودی 10,000 تومان میباشد"
                binding.edtValueCredit.setText(StringHelper.setComma("5000"))
                return@setOnClickListener
            }

            if (price.toInt() > 100000) {
                binding.edtValueCredit.error = "حداکثر مبلغ ورودی 100,000 تومان میباشد"
                binding.edtValueCredit.setText(StringHelper.setComma("100000"))
                return@setOnClickListener
            }
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(EndPoint.PAYMENT + StringHelper.toEnglishDigits(price) + "/" + MyApplication.prefManager.getDriverId())
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
            AvaCrashReporter.send(e, "OnlinePaymentFragment class, setCursorEnd method")
            // ignore
        }
    }


}