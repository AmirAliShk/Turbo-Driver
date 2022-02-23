package ir.team_x.cloud_transport.taxi_driver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ItemAvailableServiceBinding
import ir.team_x.cloud_transport.taxi_driver.dialog.AvailableServiceDialog
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.taxi_driver.fragment.services.CurrentServiceFragment
import ir.team_x.cloud_transport.taxi_driver.model.ServiceModel
import ir.team_x.cloud_transport.taxi_driver.utils.FragmentHelper
import ir.team_x.cloud_transport.taxi_driver.utils.StringHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtilJava
import ir.team_x.cloud_transport.taxi_driver.webServices.AcceptService

class AvailableServiceAdapter(list: ArrayList<ServiceModel>):RecyclerView.Adapter<AvailableServiceAdapter.ViewHolder>() {

    private val models = list
    
    class ViewHolder(val binding: ItemAvailableServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAvailableServiceBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.originAddress)
        holder.binding.txtPrice.text = StringHelper.toPersianDigits(StringHelper.setComma(model.servicePrice).toString() + " تومان")

        if (model.description.trim() == "") {
            holder.binding.llDescription.visibility = View.GONE
        } else {
            holder.binding.llDescription.visibility = View.VISIBLE
            holder.binding.txtDescription.text = StringHelper.toPersianDigits(model.description)
        }
        holder.binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(model.destinationDesc)

        holder.binding.btnGetService.setOnClickListener {
            GeneralDialog()
                .message("از دریافت سرویس اطمینان دارید؟")
                .firstButton("بله") {
                    holder.binding.vfAcceptService.displayedChild = 1
                    AcceptService().accept(model.serviceID, object : AcceptService.Listener {
                        override fun onSuccess(msg: String) {
                            holder.binding.vfAcceptService.displayedChild = 0
//                            dismiss()
                            GeneralDialog().message(msg).firstButton("باشه") {
                                MyApplication.handler.postDelayed({
                                    if (CurrentServiceFragment.isRunning) {
                                        CurrentServiceFragment.getActiveService()
                                    } else {
                                        FragmentHelper.toFragment(
                                            MyApplication.currentActivity,
                                            CurrentServiceFragment()
                                        ).replace()
                                    }
                                }, 100)
                            }
                                .show()

                        }

                        override fun onFailure() {
                            holder.binding.vfAcceptService.displayedChild = 0
//                            dismiss()
                        }
                    })
                }
                .secondButton("خیر") {}
                .show()
        }

        holder.binding.btnRejectService.setOnClickListener {
            models.removeAt(position)
            notifyDataSetChanged()
            if (models.size == 0) {
                AvailableServiceDialog.dismiss()
            }
        }

    }

    override fun getItemCount(): Int {
       return models.size
    }
}