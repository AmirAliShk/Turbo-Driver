package ir.team_x.cloud_transport.taxi_driver.adapter

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = serviceModels[position]

        val destinations: ArrayList<String> = ArrayList()
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

        holder.binding.llCall.setOnClickListener {
            CallDialog().show(model.phoneNumber, model.mobile)
        }

        holder.binding.txtDate.text = StringHelper.toPersianDigits(DateHelper.strPersianEghit(DateHelper.parseFormat(model.saveDate + "", null)))
        holder.binding.txtCustomerName.text = model.customerName
        holder.binding.txtCreditCustomer.text = model.isCreditCustomerStr
        holder.binding.imgCredit.setImageResource(if (model.isCreditCustomer == 0) R.drawable.ic_money else R.drawable.ic_card)
        holder.binding.imgReturnBack.setImageResource(if (model.returnBack == 1) R.drawable.ic_ticke else R.drawable.ic_cancle)
        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)
        holder.binding.txtCargoType.text = model.cargoName
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

                        override fun onFinishSerice(isFinish: Boolean) {
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