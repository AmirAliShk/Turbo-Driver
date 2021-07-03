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
import ir.team_x.ariana.driver.databinding.ItemServiceHistoryBinding
import ir.team_x.ariana.driver.fragment.CurrentServiceFragment
import ir.team_x.ariana.driver.model.FinishedModel
import ir.team_x.ariana.driver.model.WaitingLoadsModel
import ir.team_x.ariana.driver.utils.*
import ir.team_x.ariana.driver.webServices.AcceptService

class FinishedAdapter(list: ArrayList<FinishedModel>) :
    RecyclerView.Adapter<FinishedAdapter.ViewHolder>() {

    private val models = list

    class ViewHolder(val binding: ItemServiceHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceHistoryBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        val date = DateHelper.strPersianTen(DateHelper.parseFormat( model.saveDate + "", null))
        val time = DateHelper.strPersianFour1(DateHelper.parseFormat( model.saveDate + "", null))

        holder.binding.txtDate.text =  StringHelper.toPersianDigits("$date $time")
        holder.binding.txtOriginAddress.text = model.sourceAddress
        holder.binding.txtDestAddress.text = model.destinationAddress
        holder.binding.txtTraking.text = StringHelper.toPersianDigits(model.id.toString())
        holder.binding.txtPrice.text =  StringHelper.toPersianDigits(StringHelper.setComma(model.price))
        holder.binding.txtStatus.text = model.statusDes

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val bg_blue_border_edge = AppCompatResources.getDrawable(
                MyApplication.context,
                R.drawable.bg_redd
            )
            holder.binding.llStatus.background = bg_blue_border_edge
            DrawableCompat.setTint(bg_blue_border_edge!!, Color.parseColor(model.statusColor))
        } else {
            holder.binding.llStatus.setBackgroundColor(Color.parseColor(model.statusColor))
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

}