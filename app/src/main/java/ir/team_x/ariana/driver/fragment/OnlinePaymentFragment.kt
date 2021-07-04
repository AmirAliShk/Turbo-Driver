package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentOnlinePaymentBinding
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class OnlinePaymentFragment : Fragment() {
 private lateinit var binding : FragmentOnlinePaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentOnlinePaymentBinding.inflate(inflater, container, false)
        TypeFaceUtilJava.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.priceGroup.check(R.id.tenTH)
        binding.priceGroup.setOnItemClickListener { selectedId ->
            var price = "30000"
            when (selectedId) {
                R.id.tenTH -> price = "10000"
                R.id.twentyTH -> price = "20000"
                R.id.thirtyTH -> price = "30000"
                R.id.fourTH -> price = "40000"
                R.id.fiveTH -> price = "50000"
                R.id.sexTH -> price = "60000"
            }
            binding.edtValueCredit.setText(StringHelper.setComma(price))
        }

        return binding.root
    }



}