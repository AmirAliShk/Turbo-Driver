package ir.transport_x.taxi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ItemFreeLoadsBinding
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.model.WaitingLoadsModel
import ir.transport_x.taxi.utils.DateHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import ir.transport_x.taxi.webServices.AcceptService
import org.json.JSONArray

class WaitingLoadsAdapter(list: ArrayList<WaitingLoadsModel>) :
    RecyclerView.Adapter<WaitingLoadsAdapter.ViewHolder>() {

    private val models = list
    lateinit var stopTime: String

    class ViewHolder(val binding: ItemFreeLoadsBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFreeLoadsBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
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

        stopTime = "بدون توقف"
        when (model.stopTime) {
            0 -> {
                stopTime = "بدون توقف"
            }
            5 -> {
                stopTime = "۵ دقیقه"
            }
            10 -> {
                stopTime = "۱۰ دقیقه"
            }
            20 -> {
                stopTime = "۲۰ دقیقه"
            }
            30 -> {
                stopTime = "۳۰ دقیقه"
            }
            40 -> {
                stopTime = "۴۰ دقیقه"
            }
            50 -> {
                stopTime = "۵۰ دقیقه"
            }
            60 -> {
                stopTime = "۱ ساعت"
            }
            90 -> {
                stopTime = "۱.۵ ساعت"
            }
            120 -> {
                stopTime = "۲ ساعت"
            }
            150 -> {
                stopTime = "۲.۵ ساعت"
            }
            180 -> {
                stopTime = "۳ ساعت"
            }
        }

        if (model.serviceTypeId == 2 && MyApplication.prefManager.pricing == 1) {
            holder.binding.llServiceType.visibility = View.VISIBLE
        } else {
            holder.binding.llServiceType.visibility = View.GONE
        }

        holder.binding.txtCustomerName.text = StringHelper.toPersianDigits(model.customerName)
        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)
        holder.binding.txtFirstDestAddress.text =
            StringHelper.toPersianDigits(
                JSONArray(model.destinationAddress).getJSONObject(0).getString("address")
            )

        holder.binding.txtPrice.text =
            "${
                StringHelper.toPersianDigits(
                    StringHelper.setComma(model.price))} تومان "

        if (model.description.trim() == "") {
            holder.binding.llDescription.visibility = View.GONE
        } else {
            holder.binding.txtDescription.text =
                StringHelper.toPersianDigits(model.description)
        }

        holder.binding.btnAccept.setOnClickListener {
            GeneralDialog().message("از دریافت سرویس اطمینان دارید؟")
                .firstButton("بله") {
                    holder.binding.vfAccept.displayedChild = 1
                    AcceptService().accept(model.id.toString(), object : AcceptService.Listener {
                        override fun onSuccess(msg: String) {
                            holder.binding.vfAccept.displayedChild = 0
                            GeneralDialog().message(msg).firstButton("باشه") {
                                MyApplication.handler.postDelayed({
                                    models.removeAt(position)
                                    notifyDataSetChanged()
                                }, 100)
                            }.show()
                        }

                        override fun onFailure() {
                            holder.binding.vfAccept.displayedChild = 0
                        }
                    })
                }
                .secondButton("خیر") {}
                .show()

        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

}