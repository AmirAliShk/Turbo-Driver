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

class FinancialFragment : Fragment() {
    private lateinit var binding: FragmentFinancialBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinancialBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)
        TypeFaceUtil.overrideFont(binding.txtCharge, MyApplication.iranSansMediumTF)
        TypeFaceUtil.overrideFont(binding.txtCardName, MyApplication.iranSansMediumTF)

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.txtCardNumber.text =
            StringHelper.setCharAfter(MyApplication.prefManager.cardNumber, "-", 4)
        binding.txtCardName.text = MyApplication.prefManager.cardName

        UpdateCharge().update(object : UpdateCharge.ChargeListener {
            override fun getCharge(charge: String) {
                if (charge.isNotEmpty()) {
                    binding.vfCharge.displayedChild = 1
                    binding.txtCharge.text = StringHelper.toPersianDigits(
                        StringHelper.setComma(charge)
                    )
                } else {
                    binding.vfCharge.displayedChild = 0
                }
            }
        })

        binding.txtNote1.text = StringHelper.toPersianDigits(
            getString(
                R.string.txt_increase_charge
            )
        )
        binding.txtNote2.text = StringHelper.toPersianDigits(getString(R.string.txt_financial_desc))
        binding.txtNote3.text = StringHelper.toPersianDigits(getString(R.string.txt_importance))

        if (MyApplication.prefManager.onlineUrl!!.isEmpty()) {
            binding.llOnlinePayment.visibility = View.GONE
        } else {
            binding.llOnlinePayment.visibility = View.VISIBLE
        }

        binding.llOnlinePayment.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                OnlinePaymentFragment()
            ).replace()
        }

        binding.llCardToCard.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                ATMFragment()
            ).replace()
        }

        binding.llReport.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                AccountReportFragment()
            ).replace()
        }

        return binding.root
    }


}