package ir.team_x.cloud_transport.taxi_driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.FragmentSupportBinding
import ir.team_x.cloud_transport.taxi_driver.utils.CallHelper
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil


class SupportFragment : Fragment() {
    private lateinit var binding: FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSupportBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle,MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed()}

        binding.llCall.setOnClickListener {
            CallHelper.make("05131832")
        }

        return binding.root
    }

}