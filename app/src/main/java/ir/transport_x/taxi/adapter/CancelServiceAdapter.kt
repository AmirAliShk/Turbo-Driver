package ir.transport_x.taxi.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ItemCancelReasonBinding
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.dialog.LoadingDialog
import ir.transport_x.taxi.fragment.services.ServiceDetailsFragment
import ir.transport_x.taxi.model.ItemModel
import ir.transport_x.taxi.model.ServiceDataModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.DateHelper
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import ir.transport_x.taxi.webServices.UpdateCharge
import org.json.JSONObject
import java.util.*

class CancelServiceAdapter(
    listData: ArrayList<ItemModel>, private val serviceModel: ServiceDataModel,
    cancelServiceListener: CancelServiceListener
) :
    RecyclerView.Adapter<CancelServiceAdapter.ViewHolder>() {

    interface CancelServiceListener {
        fun onCanceled(isCancel: Boolean)
    }

    private val models = listData

    class ViewHolder(val binding: ItemCancelReasonBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemCancelReasonBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.desc.text = model.name

        holder.binding.autocompeleteRow.setOnClickListener {
            when (model.id) {

                1 -> {
                    GeneralDialog().message(
                        if (serviceModel.cancelCount > 0) {
                            "شما فقط مجاز به لغو ${serviceModel.count} سفر دیگر در این ماه هستید در صورت گذر از حد مجاز مشمول جریمه میشوید"
                        } else {
                            "درخواست های شما برای لغو سفر از حد مجاز رد شده است. درصورت درخواست لغو برای این سفر مبلغ " + StringHelper.setComma(
                                serviceModel.punishmentPrice.toString()
                            ) + " تومان جریمه به شما تعلق می گیرد"
                        }
                    ).firstButton("کنسل کردن") {
                        if (serviceModel.timeRequiredCancellation * 60000 < DateHelper.getCurrentGregorianDate().time) {
                            val msg11 =
                                "با توجه به اینکه از زمان دریافت سفر بیشتراز ${serviceModel.timeRequiredCancellation} دقیقه گذشته و سفر را زمانبر نموده اید در صورت لغو سفر مشمول " + StringHelper.setComma(
                                    serviceModel.punishmentPrice.toString()
                                ).toString() + " تومان جریمه می شوید."
                            GeneralDialog().message(msg11).secondButton("بازگشت", null)
                                .firstButton("می پذیرم") {
                                    GeneralDialog().message("آیا از کنسل کردن به دلیل ${model.name} اطمینان دارید؟")
                                        .secondButton("بازگشت", null)
                                        .firstButton("لغو سفر") {
                                            cancel(
                                                serviceModel.id,
                                                model.id
                                            )
                                        }.show()
                                }.show()
                        }
                    }.secondButton("منصرف شدم", null).show()
                }

                3 -> {
                    GeneralDialog().message(
                        "همکار گرامی خوب دقت نمایید در صورت اعلام اشتباه، مبلغ " + StringHelper.setComma(
                            serviceModel.punishmentPrice.toString()
                        ) + " تومان جریمه به شما تعلق خواهد گرفت."
                    ).firstButton("کنسل کردن") {
                        cancel(serviceModel.id, model.id)
                    }.secondButton("منصرف شدم", null).show()
                }

                12 -> {
                    val periodPerService: Date =
                        DateHelper.parseFormat(serviceModel.acceptDate, null)
                    if (periodPerService.time + (serviceModel.timeRequiredCancellation * 60000) > DateHelper.getCurrentGregorianDate().time
                    ) {
                        val periodTime =
                            ((periodPerService.time + serviceModel.timeRequiredCancellation * 60000 - DateHelper.getCurrentGregorianDate().time) / 60000).toInt() + 1
                        val msg11 =
                            "شما مجاز به استفاده از این گزینه در " + (if (periodTime == 0) 1 else periodTime) + " دقیقه آینده می باشید."
                        GeneralDialog()
                            .message("آیا از کنسل کردن به دلیل ${model.name} اطمینان دارید؟")
                            .secondButton("بازگشت", null)
                            .firstButton("لغو سفر") {
                                GeneralDialog()
                                    .message(msg11)
                                    .secondButton("بازگشت", null)
                                    .show()
                            }
                            .show()
                    } else {
                        GeneralDialog()
                            .message("آیا از کنسل کردن به دلیل ${model.name} اطمینان دارید؟")
                            .secondButton("بازگشت", null)
                            .firstButton("لغو سفر") { cancel(serviceModel.id, model.id) }
                            .show()
                    }
                }

                else -> {
                    GeneralDialog().message("آیا از کنسل کردن به دلیل ${model.name} اطمینان دارید؟")
                        .firstButton("کنسل کردن") {
                            cancel(serviceModel.id, model.id)
                        }.secondButton("منصرف شدم", null).show()
                }
            }
        }

    }

    private fun cancel(serviceId: Int, reasonCancelId: Int) {
        LoadingDialog.makeLoader()
        RequestHelper.builder(EndPoint.CANCEL)
            .listener(cancelCallBack)
            .addParam("serviceId", serviceId)
            .addParam("reasonCancelId", reasonCancelId)
            .post()
    }

    private val cancelCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    LoadingDialog.dismiss()
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val dataMsg = dataObj.getString("message")
                        val result = dataObj.getBoolean("result")
                        if (result) {
                            GeneralDialog().message(dataMsg).firstButton("باشه") {}.show()
                            FragmentHelper.taskFragment(
                                MyApplication.currentActivity,
                                ServiceDetailsFragment.TAG
                            ).remove()
                            cancelServiceListener.onCanceled(true)
                            UpdateCharge().update(object : UpdateCharge.ChargeListener {
                                override fun getCharge(charge: String, response: String) {
                                    MyApplication.prefManager.setCharge(charge)
                                }
                            })
                        } else {
                            GeneralDialog().message(dataMsg).secondButton("باشه") {}.show()
                            cancelServiceListener.onCanceled(false)
                        }
                    } else {
                        GeneralDialog().message(message).secondButton("باشه") {}.show()
                        cancelServiceListener.onCanceled(false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    LoadingDialog.dismiss()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                LoadingDialog.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }
}