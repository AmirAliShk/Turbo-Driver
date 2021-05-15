package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.databinding.FragmentFreeLoadsBinding
import ir.team_x.ariana.driver.databinding.FragmentManageServicesBinding
import ir.team_x.ariana.driver.databinding.FragmentOnlinePaymentBinding
import ir.team_x.ariana.driver.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
 private lateinit var binding : FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentProfileBinding.inflate(inflater, container, false)



        return binding.root
    }

    companion object {

    }
}