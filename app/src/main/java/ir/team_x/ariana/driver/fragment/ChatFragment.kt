package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentChatBinding
import ir.team_x.ariana.driver.databinding.FragmentProfileBinding
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }



        return binding.root
    }

}