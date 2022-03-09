package ir.transport_x.taxi.fragment.financial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.transport_x.taxi.adapter.PaymentReportAdapter
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentPaymentReportBinding
import ir.transport_x.taxi.model.PaymentReportModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONObject

class PaymentReportFragment : Fragment() {


    private lateinit var binding: FragmentPaymentReportBinding
    var models: ArrayList<PaymentReportModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPaymentReportBinding.inflate(inflater, container, false)
        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle,MyApplication.iranSansMediumTF)

        getReport()

        return binding.root
    }

    private fun getReport() {
        binding.vfReport.displayedChild = 0
        RequestHelper.builder(EndPoint.GET_ATM)
            .listener(getReportCallBack)
            .get()
    }

    private val getReportCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        for (i in 0 until dataArr.length()) {
                            val dataObj = dataArr.getJSONObject(i)
                            val paymentReportModel = PaymentReportModel(
                                dataObj.getInt("id"),
                                dataObj.getInt("driverId"),
                                dataObj.getString("saveDate"),
                                dataObj.getString("price"),
                                dataObj.getString("cardNumber"),
                                dataObj.getString("bankName"),
                                dataObj.getString("description"),
                                dataObj.getString("trackingCode"),
                                dataObj.getInt("replyStatus"),
                                dataObj.getString("replyDate"),
                                dataObj.getString("trackingAccept")
                            )
                            models.add(paymentReportModel)
                        }
                        if (models.size == 0) {
                            binding.vfReport.displayedChild = 1
                        } else {
                            binding.vfReport.displayedChild = 3
                            val adapter = PaymentReportAdapter(models)
                            binding.listReport.adapter = adapter
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfReport.displayedChild = 2
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfReport.displayedChild = 2
            }
        }
    }


}