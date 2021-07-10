package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentProfileBinding
import ir.team_x.ariana.driver.databinding.FragmentSupportBinding
import ir.team_x.ariana.driver.utils.CallHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil


class SupportFragment : Fragment() {
    private lateinit var binding: FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSupportBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed()}

        binding.llCall.setOnClickListener {
            CallHelper.make("05131832")
        }

        return binding.root
    }

}