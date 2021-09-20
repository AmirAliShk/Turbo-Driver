package ir.team_x.ariana.driver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ItemCurrentServicesBinding
import ir.team_x.ariana.driver.dialog.CallDialog
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.fragment.services.ServiceDetailsFragment
import ir.team_x.ariana.driver.model.DestinationModel
import ir.team_x.ariana.driver.model.ServiceDataModel
import ir.team_x.ariana.driver.utils.DateHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
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
        val destinations : ArrayList<String> = ArrayList()
        val destJArr = JSONArray(model.destinationAddress)
        for ( i in 0 until destJArr.length())
        {
            val destinationOBJ = destJArr.getJSONObject(i)
            val dest = destinationOBJ.getString("address")
            destinations.add(dest)
        }
        if (destinations.size == 1)
        {
            holder.binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(destinations[0])
        }
        if (destinations.size == 2)
        {
            holder.binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(destinations[0])
            holder.binding.llSecondDest.visibility = View.VISIBLE
            holder.binding.txtSecondDestAddress.text = StringHelper.toPersianDigits(destinations[1])
        }
        if(destinations.size == 3)
        {
            holder.binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(destinations[0])
            holder.binding.llSecondDest.visibility = View.VISIBLE
            holder.binding.txtSecondDestAddress.text = StringHelper.toPersianDigits(destinations[1])
            holder.binding.llThirdDest.visibility = View.VISIBLE
            holder.binding.txtThirdDestAddress.text = StringHelper.toPersianDigits(destinations[2])
        }

        holder.binding.llCall.setOnClickListener {
            CallDialog().show(model.phoneNumber, model.mobile)
        }

        holder.binding.txtDate.text = StringHelper.toPersianDigits(DateHelper.strPersianEghit(DateHelper.parseFormat(model.saveDate + "", null)))
        holder.binding.txtCustomerName.text = model.customerName
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
                                    .message("خطایی پیش امده، لطفا مجدد امتحان کنی")
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