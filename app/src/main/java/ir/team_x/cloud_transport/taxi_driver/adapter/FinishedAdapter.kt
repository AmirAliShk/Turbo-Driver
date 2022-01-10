package ir.team_x.cloud_transport.taxi_driver.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ItemServiceHistoryBinding
import ir.team_x.cloud_transport.taxi_driver.model.FinishedModel
import ir.team_x.cloud_transport.taxi_driver.utils.*
import org.json.JSONArray

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
        var date : String
        var time : String

        if (model.finishDate == "0000-00-00 00:00:00") {
            date = DateHelper.strPersianTen(DateHelper.parseFormat(model.cancelDate + "", null))
            time = DateHelper.strPersianFour1(DateHelper.parseFormat(model.cancelDate + "", null))
        } else {
            date = DateHelper.strPersianTen(DateHelper.parseFormat(model.finishDate + "", null))
            time = DateHelper.strPersianFour1(DateHelper.parseFormat(model.finishDate + "", null))
        }

        val destJArr = JSONArray(model.destinationAddress)
        for (i in 0 until destJArr.length()) {
            val destinationOBJ = destJArr.getJSONObject(i)
            when (i) {
                0 -> {
                    holder.binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
                1 -> {
                    holder.binding.llSecondDest.visibility = View.VISIBLE
                    holder.binding.txtSecondDestAddress.text = StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
                2 -> {
                   holder.binding.llThirdDest.visibility = View.VISIBLE
                   holder.binding.txtThirdDestAddress.text = StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
            }
        }

        holder.binding.txtCustomerName.text = model.customerName
        holder.binding.txtDate.text = StringHelper.toPersianDigits("$date $time")
        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)
        holder.binding.txtCreditCustomer.text = model.isCreditCustomerStr
        holder.binding.txtTraking.text = StringHelper.toPersianDigits(model.id.toString())
        holder.binding.txtPrice.text =
            StringHelper.toPersianDigits(StringHelper.setComma(model.price))
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