package ir.team_x.ariana.driver.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ItemFreeLoadsBinding
import ir.team_x.ariana.driver.databinding.ItemPeymentReportBinding
import ir.team_x.ariana.driver.databinding.ItemServiceHistoryBinding
import ir.team_x.ariana.driver.fragment.CurrentServiceFragment
import ir.team_x.ariana.driver.model.FinishedModel
import ir.team_x.ariana.driver.model.PaymentReportModel
import ir.team_x.ariana.driver.model.WaitingLoadsModel
import ir.team_x.ariana.driver.utils.*
import ir.team_x.ariana.driver.webServices.AcceptService

class PaymentReportAdapter(list: ArrayList<PaymentReportModel>) :
    RecyclerView.Adapter<PaymentReportAdapter.ViewHolder>() {

    private val models = list

    class ViewHolder(val binding: ItemPeymentReportBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPeymentReportBinding.inflate(
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

        holder.binding.txtDate.text = StringHelper.toPersianDigits("$date $time")
        holder.binding.txtCardNo.text =
            StringHelper.toPersianDigits(StringHelper.setCharAfter(model.cardNumber, "-", 4))
        holder.binding.txtPrice.text =
            StringHelper.toPersianDigits(StringHelper.setComma(model.price)) +" تومان "

        var bg = R.drawable.bg_blue
        var title = "در حال بررسی"
        var icon=R.drawable.ic_refresh
        when (model.replyStatus) {
            0 -> {
                bg = R.drawable.bg_blue
                title = "در حال بررسی"
                icon=R.drawable.ic_refresh
            }
            1 -> {
                bg = R.drawable.bg_green
                title = "تایید شده"
                icon=R.drawable.ic_completed
            }
            2 -> {
                bg = R.drawable.bg_redd
                title = "رد شده"
                icon=R.drawable.ic_canclee
            }
        }
        holder.binding.llStatus.setBackgroundResource(bg)
        holder.binding.txtStatus.text = title
        holder.binding.imgStatus.setImageResource(icon)

    }

    override fun getItemCount(): Int {
        return models.size
    }

}