package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.databinding.FragmentFinancialBinding
import ir.team_x.ariana.driver.databinding.FragmentPaymentReportBinding
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class PaymentReportFragment : Fragment() {


    private lateinit var binding: FragmentPaymentReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPaymentReportBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)


        return binding.root

    }

}