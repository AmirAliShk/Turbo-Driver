package ir.team_x.ariana.driver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ItemAccountReportBinding
import ir.team_x.ariana.driver.model.AccountReportModel
import ir.team_x.ariana.driver.utils.*

class AccountReportAdapter(list: ArrayList<AccountReportModel>) :
    RecyclerView.Adapter<AccountReportAdapter.ViewHolder>() {

    private val models = list

    class ViewHolder(val binding: ItemAccountReportBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAccountReportBinding.inflate(
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
        holder.binding.txtType.text = model.paymentTypeName
        holder.binding.txtPrice.text = StringHelper.toPersianDigits(StringHelper.setComma(model.price)) + "تومان"
        if(model.type==1){
            holder.binding.txtType.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.colorGreen))
        }else{
            holder.binding.txtType.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.colorRed))
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

}