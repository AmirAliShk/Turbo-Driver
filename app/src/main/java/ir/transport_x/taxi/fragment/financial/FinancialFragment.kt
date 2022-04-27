package ir.transport_x.taxi.fragment.financial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentFinancialBinding
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import ir.transport_x.taxi.webServices.UpdateCharge
import org.json.JSONObject

class FinancialFragment : Fragment() {
    private lateinit var binding: FragmentFinancialBinding
    var ATMObj: String = ""
    var onlineObj: String = ""
    var cardNum = ""
    var cardOwner = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinancialBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)
        TypeFaceUtil.overrideFont(binding.txtCharge, MyApplication.iranSansMediumTF)

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        getCharge()

        if (MyApplication.prefManager.onlineUrl!!.isEmpty()) {
            binding.llOnlinePayment.visibility = View.GONE
        } else {
            binding.llOnlinePayment.visibility = View.VISIBLE
        }

        binding.llOnlinePayment.setOnClickListener {
            if (onlineObj == "") {
                getCharge()
                return@setOnClickListener
            }
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                OnlinePaymentFragment(JSONObject(onlineObj).getJSONArray("onlinPrice").toString())
            ).replace()
        }

        binding.llCardToCard.setOnClickListener {
            if (ATMObj == "") {
                getCharge()
                return@setOnClickListener
            }
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                FinancialAttentionFragment (cardNum, cardOwner, JSONObject(ATMObj))
            )
            .setFrame(R.id.frame_container)
            .replace()
        }

        binding.llReport.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                AccountReportFragment()
            ).replace()
        }

        return binding.root
    }

    private fun getCharge() {
        UpdateCharge().update(object : UpdateCharge.ChargeListener {
            override fun getCharge(charge: String, response: String) {
                if (charge.isNotEmpty()) {
                    binding.vfCharge.displayedChild = 1
                    binding.txtCharge.text = StringHelper.toPersianDigits(
                        StringHelper.setComma(charge)
                    )
                    val dataObj = JSONObject(response)
                    cardNum = MyApplication.prefManager.cardNumber.toString()
                    cardOwner = MyApplication.prefManager.cardName.toString()
                    ATMObj = JSONObject(dataObj.getString("ATMObj")).toString()
                    onlineObj = JSONObject(dataObj.getString("onlineObj")).toString()
                    binding.txtNote1.text = dataObj.getString("guidTxt")
                    binding.txtNote2.text = JSONObject(onlineObj).getString("attentionTxt")
                    if (JSONObject(ATMObj).getString("attentionTxt") == "") {
                        binding.llNote2.visibility = View.GONE
                    }
                    if (dataObj.getString("guidTxt") == "") {
                        binding.txtNote1.visibility = View.GONE
                    }
                } else {
                    binding.vfCharge.displayedChild = 0
                }
            }
        })
    }

}