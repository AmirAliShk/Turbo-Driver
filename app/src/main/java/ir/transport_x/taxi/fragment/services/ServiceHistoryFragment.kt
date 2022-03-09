package ir.transport_x.taxi.fragment.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.transport_x.taxi.adapter.FinishedAdapter
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentServiceHistoryBinding
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.taxi_driver.model.FinishedModel
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONObject

class ServiceHistoryFragment : Fragment() {
    private lateinit var binding: FragmentServiceHistoryBinding
    var finishedModels: ArrayList<FinishedModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServiceHistoryBinding.inflate(inflater, container, false)
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle,MyApplication.iranSansMediumTF)

        serviceHistory()

        return binding.root
    }

    private fun serviceHistory() {
        binding.vfServiceHistory.displayedChild = 0
        RequestHelper.builder(EndPoint.FINISHED)
            .listener(serviceHistoryCallBack)
            .get()
    }

    private val serviceHistoryCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
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
                            val model = FinishedModel(
                                dataObj.getInt("id"),
                                dataObj.getInt("customerId"),
                                dataObj.getString("name"),
                                dataObj.getInt("sourceAddressId"),
                                dataObj.getString("saveDate"),
                                dataObj.getInt("status"),
                                dataObj.getString("finishDate"),
                                dataObj.getString("acceptDate"),
                                dataObj.getString("price"),
                                dataObj.getString("statusDes"),
                                dataObj.getString("sourceAddress"),
                                dataObj.getString("destinationAddress"),
                                dataObj.getString("statusColor"),
                                dataObj.getString("cancelDate"),
                                dataObj.getString("isCreditCustomerStr"),
                                dataObj.getInt("isCreditCustomer")
                            )

                            finishedModels.add(model)
                        }
                        if (finishedModels.size == 0) {
                            binding.vfServiceHistory.displayedChild = 1
                        } else {
                            binding.vfServiceHistory.displayedChild = 3
                            val adapter = FinishedAdapter(finishedModels)
                            binding.listFinished.adapter = adapter
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfServiceHistory.displayedChild = 2
                    AvaCrashReporter.send(e,"ServiceHistoryFragment,serviceHistoryCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfServiceHistory.displayedChild = 2

            }
        }
    }

}