package ir.team_x.ariana.driver.fragment.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.*
import ir.team_x.ariana.driver.dialog.CallDialog
import ir.team_x.ariana.driver.dialog.FactorDialog
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.dialog.GetPriceDialog
import ir.team_x.ariana.driver.model.ServiceDataModel
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.DateHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import ir.team_x.ariana.driver.webServices.UpdateCharge
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
    val isCreditCustomer = serviceModel.isCreditCustomer
    private lateinit var binding: FragmentServiceDetailsBinding

    interface CancelServiceListener {
        fun onCanceled(isCancel: Boolean)
        fun onFinishSerice(isFinish: Boolean)
    }

    val cancelServiceListener: CancelServiceListener = cancelServiceListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        val destJArr = JSONArray(serviceModel.destinationAddress)
        for (i in 0 until destJArr.length()) {
            val destinationOBJ = destJArr.getJSONObject(i)
            when (i) {
                0 -> {
                    binding.txtFirstDestAddress.text =
                        StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
                1 -> {
                    binding.llSecondDest.visibility = View.VISIBLE
                    binding.txtSecondDestAddress.text =
                        StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
                2 -> {
                    binding.llThirdDest.visibility = View.VISIBLE
                    binding.txtThirdDestAddress.text =
                        StringHelper.toPersianDigits(destinationOBJ.getString("address"))
                }
            }
        }
        if (serviceModel.packageValue == "0") {
            binding.llAttentionCost.visibility = View.GONE
        } else {
            binding.txtAttentionCost.text =
                StringHelper.toPersianDigits(" مبلغ ${StringHelper.setComma(serviceModel.packageValue)} تومان بابت ارزش مرسوله به کرایه اضافه شد ")
            binding.llAttentionCost.visibility = View.VISIBLE
        }

        binding.txtCargoCheckout.text = serviceModel.checkoutName
        binding.txtCustomerName.text = serviceModel.customerName
        binding.txtCreditCustomer.text = serviceModel.isCreditCustomerStr
        binding.imgCredit.setImageResource(if (serviceModel.isCreditCustomer == 0) R.drawable.ic_money else R.drawable.ic_card)
        binding.txtCargoWeight.text =
            if (serviceModel.weightName == "null") "ثبت نشده" else StringHelper.toPersianDigits(
                serviceModel.weightName
            )
        binding.txtOriginAddress.text = StringHelper.toPersianDigits(serviceModel.sourceAddress)
        binding.txtTell.text = StringHelper.toPersianDigits(serviceModel.phoneNumber)
        binding.txtMobile.text = StringHelper.toPersianDigits(serviceModel.mobile)
//        binding.txtCargoType.text = serviceModel.cargoName
        binding.txtCargoType.text =
            if (serviceModel.cargoName == "null") "ثبت نشده" else serviceModel.cargoName
        binding.txtCargoCost.text = StringHelper.toPersianDigits(serviceModel.costName)
        binding.txtPaymentSide.text = if (serviceModel.paymentSide == 0) "مقصد" else "مبدا"
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
        binding.txtDiscount.text =
            StringHelper.toPersianDigits(StringHelper.setComma(serviceModel.discount))
        binding.imgDriverHelp.setImageResource(if (serviceModel.driverHelp == 1) R.drawable.ic_ticke else R.drawable.ic_cancle)
        binding.imgReturnBack.setImageResource(if (serviceModel.returnBack == 1) R.drawable.ic_ticke else R.drawable.ic_cancle)
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
            bill(serviceModel.id, serviceModel.priceService)
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

                        GetPriceDialog().show(serviceModel.packageValue, isCreditCustomer,
                            dataObj, serviceModel.id,
                            object : GetPriceDialog.FinishServiceListener {
                                override fun onFinishService(isFinish: Boolean) {
                                    cancelServiceListener.onFinishSerice(isFinish)
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