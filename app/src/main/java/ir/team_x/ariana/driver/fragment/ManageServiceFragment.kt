package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentFreeLoadsBinding
import ir.team_x.ariana.driver.databinding.FragmentManageServicesBinding

class ManageServiceFragment : Fragment() {
 private lateinit var binding : FragmentManageServicesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentManageServicesBinding.inflate(inflater, container, false)

        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        return binding.root
    }

    companion object {

    }
}