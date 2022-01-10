package ir.team_x.cloud_transport.taxi_driver.fragment.news

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.FragmentNewsDetailsBinding
import ir.team_x.cloud_transport.taxi_driver.utils.StringHelper
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil

import android.net.Uri

import android.content.Intent
import java.lang.Exception

class NewsDetailsFragment(link: String, title: String, text: String) : Fragment() {

    private lateinit var binding: FragmentNewsDetailsBinding

    private val newsTitle = title
    private val newsText = text
    private var link = link

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtPageTitle, MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        binding.txtTitle.text = StringHelper.toPersianDigits(newsTitle)
        binding.txtText.text = StringHelper.toPersianDigits(newsText)

        if (link.trim().isEmpty()) {
            binding.imgLink.visibility = View.GONE
        }

        binding.imgLink.setOnClickListener {
            try {
                if (!link.startsWith("http://") && !link.startsWith("https://"))
                    link = "http://$link"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return binding.root
    }

    private var refreshListener: RefreshNotificationCount? = null

    interface RefreshNotificationCount {
        fun refreshNotification()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        refreshListener = context as RefreshNotificationCount
    }

    override fun onDetach() {
        super.onDetach()
        refreshListener?.refreshNotification()
    }

}