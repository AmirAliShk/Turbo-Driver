package ir.transport_x.taxi.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentProfileBinding
import ir.transport_x.taxi.utils.TypeFaceUtil

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtName, MyApplication.iranSansMediumTF)
//        TypeFaceUtil.overrideFont(binding.txtTitle,MyApplication.iranSansMediumTF)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity?.window!!
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.pageBackground)
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightStatusBars = true
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).isAppearanceLightNavigationBars = true
        }


        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

//        binding.llAboutUs.setOnClickListener {
//            FragmentHelper.toFragment(MyApplication.currentActivity,AboutUsFragment()).replace()
//        }

        binding.txtName.text = MyApplication.prefManager.getUserName()
        binding.txtIben.text = MyApplication.prefManager.getIban()
        binding.txtNationalCode.text = MyApplication.prefManager.getNational()
        binding.txtDriverCode.text = MyApplication.prefManager.getDriverId().toString()

        return binding.root
    }

}