package ir.transport_x.taxi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ItemNewsBinding
import ir.transport_x.taxi.fragment.news.NewsDetailsFragment
import ir.transport_x.taxi.model.NewsModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.DateHelper
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import org.json.JSONObject

class NewsAdapter(list: ArrayList<NewsModel>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private val models = list
    var pos = 0

    class ViewHolder(val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        val date = DateHelper.strPersianTen(
            DateHelper.parseFormat(model.saveDate + "", null))
        val time = DateHelper.strPersianFour1(
            DateHelper.parseFormat(model.saveDate + "", null))

        holder.binding.txtTitle.text = StringHelper.toPersianDigits(model.title)
        holder.binding.txtDate.text = StringHelper.toPersianDigits("$date $time")
        if (model.newMessage == 1) {
            holder.binding.txtNew.visibility = View.VISIBLE
        } else {
            holder.binding.txtNew.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            this.pos = position
            newsDetails(model.id)
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

    private fun newsDetails(id: Int) {
        RequestHelper.builder(EndPoint.GET_NEWS)
            .addPath(id.toString())
            .listener(newsDetailsCallBack)
            .get()
    }

    private val newsDetailsCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"عملیات با موفقیت انجام شد.","data":{"id":1,"image":"null","saveDate":"2021-05-20T09:41:29.000Z","title":"تست","newMessage":0}}
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        if (MyApplication.prefManager.getCountNotification() > 0)
                            MyApplication.prefManager.setCountNotification(MyApplication.prefManager.getCountNotification() - 1)

                        FragmentHelper.toFragment(
                            MyApplication.currentActivity,
                            NewsDetailsFragment(dataObj.getString("link"),
                                dataObj.getString("title"),
                                dataObj.getString("message")
                            )
                        ).add()

                        models[pos].newMessage = 0
                        notifyDataSetChanged()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
            }
        }
    }


}