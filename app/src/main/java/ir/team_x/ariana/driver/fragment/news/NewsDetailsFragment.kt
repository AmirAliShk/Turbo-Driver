package ir.team_x.ariana.driver.fragment.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentNewsDetailsBinding
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class NewsDetailsFragment(title: String, text: String) : Fragment() {

    private lateinit var binding: FragmentNewsDetailsBinding

    val newsTitle = title
    val newsText = text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        binding.txtTitle.text = StringHelper.toPersianDigits(newsTitle)
        binding.txtText.text = StringHelper.toPersianDigits(newsText)

        return binding.root
    }

}