package ir.team_x.cloud_transport.taxi_driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.FragmentAboutUsBinding
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil

class AboutUsFragment : Fragment() {
    private lateinit var binding: FragmentAboutUsBinding
    val aboutUs = "با توجه به سند ملی سیستم های حمل و نقل هوشمند در ایران و نیز نقش و اهمیت فناوری اطلاعات در کسب و کارها، این مجموعه بر آن است تا از طریق توسعه و راه اندازی نرم افزارهای تخصصی تحت وب و مبتنی بر تلفن همراه به سهم خود گامی در جهت تحقق شعار شهر هوشمند و شهروند الکترونیک در حوزه ی حمل و نقل درون شهری و برون شهری بردارد.\n" +
            "پیک موتوری آریانا با بیش از ۱۴ سال سابقه و ۱۵۰ نفر نیرو یکی از قدیمی\u200C\u200Cترین مجموعه\u200Cهای حمل و نقل درون شهری در شهر مقدّس مشهد می \u200Cباشد."


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle,MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.txtAboutUs.text = "$aboutUs"

        return binding.root
    }

}