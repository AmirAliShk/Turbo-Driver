package ir.transport_x.taxi.fragment.financial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.R
import ir.transport_x.taxi.adapter.ValueAdapter
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentFinancialAttentionBinding
import ir.transport_x.taxi.model.ValueModel
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONObject
import java.util.*

class FinancialAttentionFragment(
    var cardNum: String,
    var cardOwner: String,
    var ATMObj: JSONObject
) : Fragment() {

    lateinit var binding: FragmentFinancialAttentionBinding
    lateinit var price: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinancialAttentionBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)
        TAG = FinancialAttentionFragment::class.java.simpleName

        val ATMPRice = ATMObj.getJSONArray("ATMPrice")
        val values = ArrayList<ValueModel>()
        for (i in 0 until ATMPRice.length()) {
            val item = ATMPRice.getString(i)
            val model = ValueModel(item, false)
            values.add(model)
        }
        val valueAdapter = ValueAdapter(values, object : ValueAdapter.SelectedValue {
            override fun getSelected(s: String) {

                price = s
                if (s == "0") {
                    binding.cvCardDetails.visibility = View.GONE
                    binding.btnRegRecord.visibility = View.GONE
                } else {
                    binding.cvCardDetails.visibility = View.VISIBLE
                    binding.btnRegRecord.visibility = View.VISIBLE
                }
            }
        })
        binding.gridView.adapter = valueAdapter

        binding.txtCardNumber.typeface = MyApplication.iranSansMediumTF
        binding.txtCardName.typeface = MyApplication.iranSansMediumTF

        binding.txtCardNumber.text =
            StringHelper.toPersianDigits(StringHelper.setCharAfter(cardNum, "-", 4))
        binding.txtCardName.text = cardOwner

        binding.btnRegRecord.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                ATMFragment(ATMObj, price)
            )
                .setFrame(R.id.frame_container)
                .replace()
        }

        binding.btnFollowRecord.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, PaymentReportFragment())
                .setFrame(R.id.frame_container)
                .replace()
        }

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        return binding.root
    }

    companion object {
        lateinit var TAG: String
    }
}