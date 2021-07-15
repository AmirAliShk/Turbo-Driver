package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentAboutUsBinding
import ir.team_x.ariana.driver.databinding.FragmentProfileBinding
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class AboutUsFragment : Fragment() {
    private lateinit var binding: FragmentAboutUsBinding
    val aboutUs =
        "سرعت، اطمینان و لذت یک سفر درون شهری را با ما تجربه کنید. در توربو با چند کلیک نزدیکترین تاکسی به خود را انتخاب و مسیر و قیمت سفر را آنلاین مشاهده نمایید. با این روش علاوه بر سادگی و سهولت گرفتن تاکسی از خدمات و تخفیف های ویژه ما بهرمند شوید. \n" +
                "اپلیکیشن توربو به عنوان پلی میان ما و شما می باشد با ارسال نظرات خویش ما را در ارائه خدمات بهتر یاری رسانید. "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.txtAboutUs.text = "$aboutUs $aboutUs $aboutUs $aboutUs $aboutUs $aboutUs" // TODO change the passage

        return binding.root
    }

}