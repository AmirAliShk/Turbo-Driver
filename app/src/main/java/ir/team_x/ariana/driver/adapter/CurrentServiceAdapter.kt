package ir.team_x.ariana.driver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ItemCurrentServicesBinding
import ir.team_x.ariana.driver.fragment.ServiceDetailsFragment
import ir.team_x.ariana.driver.model.ServiceDataModel
import ir.team_x.ariana.driver.utils.DateHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class CurrentServiceAdapter() :
    RecyclerView.Adapter<CurrentServiceAdapter.ViewHolder>() {

    private var models: ArrayList<ServiceDataModel> = ArrayList()
    var position = 0

    constructor (list: ArrayList<ServiceDataModel>) : this() {
        this.models = list
    }

    class ViewHolder(val binding: ItemCurrentServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrentServicesBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtil.overrideFont(binding.root)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        holder.binding.txtDate.text = StringHelper.toPersianDigits(
            DateHelper.strPersianEghit(
                DateHelper.parseFormat(
                    model.saveDate + "",
                    null
                )
            )
        )
        holder.binding.txtCustomerName.text = model.customerName
        holder.binding.txtOriginAddress.text = model.sourceAddress
        holder.binding.txtDestAddress.text = model.destinationAddress
        holder.binding.txtCargoType.text = model.cargoName
        holder.itemView.setOnClickListener {
            this.position = position
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                ServiceDetailsFragment(
                    model,
                    object : ServiceDetailsFragment.CancelServiceListener {
                        override fun onCanceled(isCancel: Boolean) {
                            if (isCancel) {
                                models.removeAt(position)
                                notifyDataSetChanged()
                            } else {
                                //TODO what to do here?
                                MyApplication.Toast("not canceled", Toast.LENGTH_SHORT)
                            }
                        }
                    })
            )
                .add()
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

}