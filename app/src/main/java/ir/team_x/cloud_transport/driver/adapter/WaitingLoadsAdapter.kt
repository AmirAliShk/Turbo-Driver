package ir.team_x.cloud_transport.driver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.cloud_transport.driver.R
import ir.team_x.cloud_transport.driver.app.MyApplication
import ir.team_x.cloud_transport.driver.databinding.ItemFreeLoadsBinding
import ir.team_x.cloud_transport.driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.driver.model.WaitingLoadsModel
import ir.team_x.cloud_transport.driver.utils.*
import ir.team_x.cloud_transport.driver.webServices.AcceptService
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

        if (model.stopTime == 0) {
            holder.binding.llStopTime.visibility = View.GONE
        } else {
            holder.binding.txtStopTime.text = stopTime
        }

        if (model.packageValue == "0") {
            holder.binding.llAttentionCost.visibility = View.GONE
        } else {
            holder.binding.txtAttentionCost.text =
                StringHelper.toPersianDigits(" مبلغ ${StringHelper.setComma(model.packageValue)} تومان بابت ارزش مرسوله به کرایه اضافه شد ")
            holder.binding.llAttentionCost.visibility = View.VISIBLE

        }

        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)
        val destJArr = JSONArray(model.destinationAddress)
        for (i in 0 until destJArr.length()) {
            val destinationOBJ = destJArr.getJSONObject(i)
            when (i) {
                0 -> {
                    holder.binding.txtFirstDestAddress.text =
                        StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
                1 -> {
                    holder.binding.llSecondDest.visibility = View.VISIBLE
                    holder.binding.txtSecondDestAddress.text =
                        StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
                2 -> {
                    holder.binding.llThirdDest.visibility = View.VISIBLE
                    holder.binding.txtThirdDestAddress.text =
                        StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
            }
        }

        holder.binding.txtCargoCost.text = StringHelper.toPersianDigits(model.costName)
        holder.binding.txtPrice.text =
            "${StringHelper.toPersianDigits(StringHelper.setComma(model.price))} تومان "

        if (model.cargoName.trim() == "") {
            holder.binding.llCargoType.visibility = View.GONE
        } else {
            holder.binding.llCargoType.visibility = View.VISIBLE
            holder.binding.txtCargoType.text = model.cargoName
        }

        holder.binding.imgReturnBack.setImageResource(if (model.returnBack == 1) R.drawable.ic_ticke else R.drawable.ic_cancle)

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
                        override fun onSuccess() {
                            holder.binding.vfAccept.displayedChild = 0

                            MyApplication.handler.postDelayed({
                                models.removeAt(position)
                                notifyDataSetChanged()
                            }, 100)

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