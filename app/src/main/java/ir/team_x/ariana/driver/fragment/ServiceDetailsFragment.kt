package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.adapter.CurrentServiceAdapter
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.*
import ir.team_x.ariana.driver.dialog.CallDialog
import ir.team_x.ariana.driver.dialog.FactorDialog
import ir.team_x.ariana.driver.dialog.GeneralDialog
import ir.team_x.ariana.driver.model.ServiceDataModel
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil
import org.json.JSONObject

class ServiceDetailsFragment(serviceModel: ServiceDataModel) : Fragment() {
    val serviceModel = serviceModel

    private lateinit var binding: FragmentServiceDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServiceDetailsBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        binding.txtCustomerName.text = serviceModel.acceptDate
        binding.txtCustomerName.text = serviceModel.customerName
        binding.txtOriginAddress.text = serviceModel.sourceAddress
        binding.txtDestAddress.text = serviceModel.destinationAddress
        binding.txtTell.text = serviceModel.phoneNumber
        binding.txtMobile.text = serviceModel.mobile
        binding.txtCargoType.text = serviceModel.cargoName
        binding.txtCargoCost.text = serviceModel.costName
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
                        val dataArr = jsonObject.getJSONArray("data")
                        val result = dataArr.getJSONObject(0).getBoolean("result")
                        if (result) {
                            MyApplication.Toast(message, Toast.LENGTH_SHORT)
                            MyApplication.currentActivity.onBackPressed()
                        }
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