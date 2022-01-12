package ir.team_x.cloud_transport.taxi_driver.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ItemCurrentServicesBinding
import ir.team_x.cloud_transport.taxi_driver.dialog.CallDialog
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.taxi_driver.fragment.services.ServiceDetailsFragment
import ir.team_x.cloud_transport.taxi_driver.model.ServiceDataModel
import ir.team_x.cloud_transport.taxi_driver.utils.DateHelper
import ir.team_x.cloud_transport.taxi_driver.utils.FragmentHelper
import ir.team_x.cloud_transport.taxi_driver.utils.StringHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtilJava
import org.json.JSONArray

class CurrentServiceAdapter() :
    RecyclerView.Adapter<CurrentServiceAdapter.ViewHolder>() {

    private var serviceModels: ArrayList<ServiceDataModel> = ArrayList()
    var position = 0

    constructor (serviceList: ArrayList<ServiceDataModel>) : this() {
        this.serviceModels = serviceList
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

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = serviceModels[holder.adapterPosition]

        holder.binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(JSONArray(model.destinationAddress).getJSONObject(0).getString("address"))
        holder.binding.llCall.setOnClickListener {
            CallDialog().show(model.phoneNumber, model.mobile)
        }

        holder.binding.txtDate.text = StringHelper.toPersianDigits(DateHelper.strPersianEghit(DateHelper.parseFormat(model.saveDate + "", null)))
        holder.binding.txtCustomerName.text = model.customerName
        holder.binding.txtCreditCustomer.text = model.isCreditCustomerStr
//        holder.binding.imgCredit.setImageResource(if (model.isCreditCustomer == 0) R.drawable.ic_money else R.drawable.ic_card)
        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)
        holder.itemView.setOnClickListener {
            this.position = position
            FragmentHelper.toFragment(MyApplication.currentActivity, ServiceDetailsFragment(model,
                object : ServiceDetailsFragment.CancelServiceListener {
                        override fun onCanceled(isCancel: Boolean) {
                            if (isCancel) {
                                serviceModels.removeAt(position)
                                notifyDataSetChanged()
                            }
                        }

                        override fun onFinishService(isFinish: Boolean) {
                            if (isFinish) {
                                serviceModels.removeAt(position)
                                notifyDataSetChanged()
                            } else {
                                GeneralDialog()
                                    .message("خطایی پیش امده، لطفا مجدد امتحان کنید")
                                    .secondButton("باشه") {}
                                    .show()
                            }
                        }
                    })).add()
        }
    }

    override fun getItemCount(): Int {
        return serviceModels.size
    }

}