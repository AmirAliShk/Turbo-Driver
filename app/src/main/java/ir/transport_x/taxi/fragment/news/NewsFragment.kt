package ir.transport_x.taxi.fragment.news

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.transport_x.taxi.adapter.NewsAdapter
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentNewsBinding
import ir.transport_x.taxi.model.NewsModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONObject

class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    var newsModels: ArrayList<NewsModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        getNews()

        return binding.root
    }


    private fun getNews() {
        binding.vfNews.displayedChild = 0
        RequestHelper.builder(EndPoint.GET_NEWS)
            .listener(newsCallBack)
            .get()
    }

    private val newsCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    newsModels.clear()
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        for (i in 0 until dataArr.length()) {
                            val dataObj = dataArr.getJSONObject(i)
                            val model = NewsModel(
                                dataObj.getInt("id"),
                                dataObj.getString("image"),
                                dataObj.getString("saveDate"),
                                dataObj.getString("title"),
                                dataObj.getInt("newMessage")
                            )

                            newsModels.add(model)
                        }
                        if (newsModels.size == 0) {
                            binding.vfNews.displayedChild = 1
                        } else {
                            binding.vfNews.displayedChild = 3
                            val adapter = NewsAdapter(newsModels)
                            binding.lisetNews.adapter = adapter
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfNews.displayedChild = 2
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfNews.displayedChild = 2
            }
        }
    }

}