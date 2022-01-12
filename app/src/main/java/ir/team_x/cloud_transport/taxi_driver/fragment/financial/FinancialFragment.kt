package ir.team_x.cloud_transport.taxi_driver.fragment.financial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.FragmentFinancialBinding
import ir.team_x.cloud_transport.taxi_driver.utils.FragmentHelper
import ir.team_x.cloud_transport.taxi_driver.utils.StringHelper
import ir.team_x.cloud_transport.taxi_driver.webServices.UpdateCharge
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil

class FinancialFragment : Fragment() {
    private lateinit var binding: FragmentFinancialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinancialBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle,MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.txtCardNumber.text=StringHelper.setCharAfter(MyApplication.prefManager.cardNumber,"-",4 )
        binding.txtCardName.text=MyApplication.prefManager.cardName

        UpdateCharge().update(object: UpdateCharge.ChargeListener{
            override fun getCharge(charge: String) {
                if(charge.isNotEmpty()){
                    binding.vfCharge.displayedChild=1
                    binding.txtCharge.text = StringHelper.toPersianDigits(StringHelper.setComma(charge))
                }else{
                    binding.vfCharge.displayedChild=0
                }
            }
        })

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