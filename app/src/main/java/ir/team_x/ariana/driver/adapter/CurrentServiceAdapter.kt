package ir.team_x.ariana.driver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ItemCurrentServicesBinding
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.fragment.services.ServiceDetailsFragment
import ir.team_x.ariana.driver.model.ServiceDataModel
import ir.team_x.ariana.driver.utils.DateHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava

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
        TypeFaceUtilJava.overrideFonts(binding.root,MyApplication.iranSansMediumTF)
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
        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)
        holder.binding.txtDestAddress.text = StringHelper.toPersianDigits(model.destinationAddress)
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
                            }
                        }

                        override fun onFinishSerice(isFinish: Boolean) {
                            if (isFinish) {
                                models.removeAt(position)
                                notifyDataSetChanged()
                            } else {
                                //TODO what to do here?
                                GeneralDialog()
                                    .message("خطایی پیش امده، لطفا مجدد امتحان کنی")
                                    .secondButton("باشه") {}
                                    .show()
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