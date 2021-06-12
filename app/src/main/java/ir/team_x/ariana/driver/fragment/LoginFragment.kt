package ir.team_x.ariana.driver.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.activity.MainActivity
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentLoginBinding
import ir.team_x.ariana.driver.databinding.FragmentNewsDetailsBinding
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)

        binding.btnLogin.setOnClickListener {
//            if (binding.edtUserName.text.isEmpty()) {
//                MyApplication.showSnackBar("please enter your name.", it)
//                binding.edtUserName.requestFocus()
//            } else if (binding.edtPassword.text.isEmpty()) {
//                MyApplication.showSnackBar("please enter your password.", it)
//                binding.edtPassword.requestFocus()
//            } else {
            startActivity(Intent(MyApplication.currentActivity, MainActivity::class.java))
            MyApplication.currentActivity.finish()
//                login()
//            }
        }

        return binding.root
    }

}