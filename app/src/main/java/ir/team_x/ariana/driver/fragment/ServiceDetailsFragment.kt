package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.*
import ir.team_x.ariana.driver.dialog.CallDialog
import ir.team_x.ariana.driver.dialog.FactorDialog
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.model.ServiceDataModel
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.driver.utils.DateHelper
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import org.json.JSONObject

class ServiceDetailsFragment(
    serviceModel: ServiceDataModel,
    cancelServiceListener: CancelServiceListener
) : Fragment() {
    companion object {
        val TAG = ServiceDetailsFragment::class.java.simpleName
    }

    private val serviceModel = serviceModel
    private lateinit var binding: FragmentServiceDetailsBinding

    interface CancelServiceListener {
        fun onCanceled(isCancel: Boolean)
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
        TypeFaceUtilJava.overrideFonts(binding.root,MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        binding.txtSendDate.text = StringHelper.toPersianDigits(
            DateHelper.strPersianFour1(
                DateHelper.parseFormat(
                    serviceModel.acceptDate + "",
                    null
                )
            )
        )
        binding.txtCustomerName.text = serviceModel.customerName
//        binding.txtCargoWeight.text=StringHelper.toPersianDigits(serviceModel.weightName) //TODO uncomment
        binding.txtOriginAddress.text = serviceModel.sourceAddress
        binding.txtDestAddress.text = serviceModel.destinationAddress
        binding.txtTell.text = StringHelper.toPersianDigits(serviceModel.phoneNumber)
        binding.txtMobile.text = StringHelper.toPersianDigits(serviceModel.mobile)
        binding.txtCargoType.text = serviceModel.cargoName
        binding.txtCargoCost.text = StringHelper.toPersianDigits(serviceModel.costName)
        binding.txtPaymentSide.text = if (serviceModel.paymentSide == 0) "مقصد" else "مبدا"
        binding.imgDriverHelp.setImageResource(if (serviceModel.driverHelp == 1) R.drawable.ic_completed else R.drawable.ic_cancle)
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
            FactorDialog().show(serviceModel)
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
                        val result = dataObj.getBoolean("result")
                        if (result) {
                            FragmentHelper.taskFragment(MyApplication.currentActivity, TAG).remove()
                            cancelServiceListener.onCanceled(true)
                        } else {
                            cancelServiceListener.onCanceled(false)
                        }
                    } else {
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

}