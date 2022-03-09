package ir.transport_x.taxi.fragment.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentServiceDetailsBinding
import ir.transport_x.taxi.dialog.CallDialog
import ir.transport_x.taxi.dialog.FactorDialog
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.dialog.GetPriceDialog
import ir.transport_x.taxi.model.ServiceDataModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.DateHelper
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava
import ir.transport_x.taxi.webServices.UpdateCharge
import org.json.JSONArray
import org.json.JSONObject

class ServiceDetailsFragment(
    serviceModel: ServiceDataModel,
    cancelServiceListener: CancelServiceListener
) : Fragment() {
    companion object {
        val TAG: String = ServiceDetailsFragment::class.java.simpleName
    }

    private val serviceModel = serviceModel
    private lateinit var binding: FragmentServiceDetailsBinding

    interface CancelServiceListener {
        fun onCanceled(isCancel: Boolean)
        fun onFinishService(isFinish: Boolean)
    }

    val cancelServiceListener: CancelServiceListener = cancelServiceListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServiceDetailsBinding.inflate(inflater, container, false)
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        binding.txtDate.text = StringHelper.toPersianDigits(
            DateHelper.strPersianEghit(
                DateHelper.parseFormat(
                    serviceModel.acceptDate + "",
                    null
                )
            )
        )

        StringHelper.toPersianDigits(
            JSONArray(serviceModel.destinationAddress).getJSONObject(0).getString("address")
        )

        var stopTime = "بدون توقف"

        when (serviceModel.stopTime) {
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

        binding.txtStopTime.text = stopTime

        binding.txtCustomerName.text = serviceModel.customerName
        binding.txtOriginAddress.text = StringHelper.toPersianDigits(serviceModel.sourceAddress)
        binding.txtPrice.text =  "${
            StringHelper.toPersianDigits(
                StringHelper.setComma(serviceModel.price))} تومان "
        binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(
            JSONArray(serviceModel.destinationAddress).getJSONObject(0).getString("address")
        )
        binding.txtTell.text = StringHelper.toPersianDigits(serviceModel.phoneNumber)
        binding.txtMobile.text = StringHelper.toPersianDigits(serviceModel.mobile)
        if (serviceModel.description.trim() == "" && serviceModel.fixedDescription.trim() == "") {
            binding.llDesc.visibility = View.GONE
        } else {
            if (serviceModel.description.trim() != "" && serviceModel.fixedDescription.trim() != "") {
                binding.txtDescriptionDetail.text = StringHelper.toPersianDigits(
                    "${serviceModel.description} و ${serviceModel.fixedDescription}"
                )
            } else if (serviceModel.description.trim() != "") {
                binding.txtDescriptionDetail.text =
                    StringHelper.toPersianDigits(serviceModel.description)
            } else if (serviceModel.fixedDescription.trim() != "") {
                binding.txtDescriptionDetail.text =
                    StringHelper.toPersianDigits(serviceModel.fixedDescription)
            }
        }
        if (serviceModel.discount == "0") {
            binding.llDiscount.visibility = View.GONE
        } else {
            binding.llDiscount.visibility = View.VISIBLE
            binding.txtDiscount.text =
                StringHelper.toPersianDigits(
                    StringHelper.setComma(serviceModel.discount))
        }

        binding.llCancel.setOnClickListener {
            GeneralDialog()
                .message("از لغو سرویس اطمینان دارید؟")
                .firstButton("بله") {
                    cancel(serviceModel.id, 1)
                }
                .secondButton("خیر") {}
                .show()
        }
        binding.llCall.setOnClickListener {
            CallDialog().show(serviceModel.phoneNumber, serviceModel.mobile)
        }
        binding.txtFinish.setOnClickListener {
            if(MyApplication.prefManager.pricing==1){
                bill(serviceModel.id, serviceModel.priceService)
            }else{
                GetPriceDialog().show(serviceModel.id,
                    object : GetPriceDialog.FinishServiceListener {
                        override fun onFinishService(isFinish: Boolean) {
                            cancelServiceListener.onFinishService(isFinish)
                        }

                    })
            }
        }

        return binding.root
    }

    private fun cancel(serviceId: Int, reasonCancelId: Int) {
        binding.vfCancel.displayedChild = 1
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
                    binding.vfCancel.displayedChild = 0
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")
                        val dataMsg = dataObj.getString("message")
                        val result = dataObj.getBoolean("result")
                        if (result) {
                            GeneralDialog().message(dataMsg).firstButton("باشه") {}.show()
                            FragmentHelper.taskFragment(MyApplication.currentActivity, TAG).remove()
                            cancelServiceListener.onCanceled(true)
                            UpdateCharge().update(object : UpdateCharge.ChargeListener {
                                override fun getCharge(charge: String) {
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
                    binding.vfCancel.displayedChild = 0
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfCancel.displayedChild = 0
            }
        }
    }

    private fun bill(serviceId: Int, price: String) {
        binding.vfEndService.displayedChild = 1
        RequestHelper.builder(EndPoint.BILL)
            .listener(billCallBack)
            .addPath(serviceId.toString())
            .addPath(price)
            .get()
    }

    private val billCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfEndService.displayedChild = 0
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")

                        FactorDialog().show(dataObj, serviceModel.id,
                            object : FactorDialog.FinishServiceListener {
                                override fun onFinishService(isFinish: Boolean) {
                                    cancelServiceListener.onFinishService(isFinish)
                                }

                            })

                    } else {
                        cancelServiceListener.onCanceled(false)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfEndService.displayedChild = 0
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfEndService.displayedChild = 0
            }
        }
    }

}