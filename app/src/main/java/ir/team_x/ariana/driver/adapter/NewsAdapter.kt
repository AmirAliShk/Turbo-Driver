package ir.team_x.ariana.driver.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ItemFreeLoadsBinding
import ir.team_x.ariana.driver.databinding.ItemNewsBinding
import ir.team_x.ariana.driver.databinding.ItemServiceHistoryBinding
import ir.team_x.ariana.driver.fragment.CurrentServiceFragment
import ir.team_x.ariana.driver.fragment.NewsDetailsFragment
import ir.team_x.ariana.driver.model.FinishedModel
import ir.team_x.ariana.driver.model.NewsModel
import ir.team_x.ariana.driver.model.WaitingLoadsModel
import ir.team_x.ariana.driver.utils.*
import ir.team_x.ariana.driver.webServices.AcceptService

class NewsAdapter(list: ArrayList<NewsModel>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private val models = list

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
        val date = DateHelper.strPersianTen(DateHelper.parseFormat(model.saveDate + "", null))
        val time = DateHelper.strPersianFour1(DateHelper.parseFormat(model.saveDate + "", null))

        holder.binding.txtTitle.text = model.title
        holder.binding.txtNews.text = model.message.substring(0,20)

        holder.itemView.setOnClickListener {
            //TODO call details API here
            FragmentHelper.toFragment(MyApplication.currentActivity,NewsDetailsFragment()).replace()
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

}