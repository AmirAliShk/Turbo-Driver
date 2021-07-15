package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentProfileBinding
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.llAboutUs.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity,AboutUsFragment()).replace()
        }

        binding.txtName.text=MyApplication.prefManager.getUserName()
        binding.txtIben.text=MyApplication.prefManager.getIban()
        binding.txtNationalCode.text=MyApplication.prefManager.getNational()

        return binding.root
    }

}