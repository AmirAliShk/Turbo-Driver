package ir.team_x.cloud_transport.taxi_driver.fragment.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.cloud_transport.taxi_driver.adapter.CurrentServiceAdapter
import ir.team_x.cloud_transport.taxi_driver.app.EndPoint
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.FragmentCurrentServicesBinding
import ir.team_x.cloud_transport.taxi_driver.model.ServiceDataModel
import ir.team_x.cloud_transport.taxi_driver.okHttp.RequestHelper
import ir.team_x.cloud_transport.taxi_driver.push.AvaCrashReporter
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtil
import org.json.JSONObject

class CurrentServiceFragment : Fragment() {


    companion object {
        lateinit var binding: FragmentCurrentServicesBinding
        val serviceModels: ArrayList<ServiceDataModel> = ArrayList()
        var isRunning: Boolean =false
        var adapter = CurrentServiceAdapter()

        fun getActiveService() {
            binding.vfCurrentService.displayedChild = 0
            RequestHelper.builder(EndPoint.ACTIVES)
                .listener(activeServiceCallBack)
                .get()
        }

        private val activeServiceCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        serviceModels.clear()
                        val jsonObject = JSONObject(args[0].toString())
                        val success = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (success) {
                            val dataArr = jsonObject.getJSONArray("data")
                            for (i in 0 until dataArr.length()) {
                                val dataObj = dataArr.getJSONObject(i)
                                val model = ServiceDataModel(
                                    dataObj.getInt("id"),
                                    dataObj.getInt("customerId"),
                                    dataObj.getInt("sourceAddressId"),
                                    dataObj.getInt("count"),
                                    dataObj.getString("description"),
                                    dataObj.getString("fixedMessage"),
                                    dataObj.getInt("carType"),
                                    dataObj.getInt("stopTime"),
                                    dataObj.getString("saveDate"),
                                    dataObj.getInt("userId"),
                                    dataObj.getInt("driverId"),
                                    dataObj.getString("finishDate"),
                                    dataObj.getString("voipId"),
                                    dataObj.getString("acceptDate"),
                                    dataObj.getString("price"),
                                    dataObj.getString("customerName"),
                                    dataObj.getString("phoneNumber"),
                                    dataObj.getString("mobile"),
                                    dataObj.getString("carTypeName"),
                                    dataObj.getString("sourceAddress"),
                                    dataObj.getString("destinationAddress"),
                                    dataObj.getString("priceService"),
                                    dataObj.getString("discount")
                                )
                                serviceModels.add(model)
                            }

                            if (serviceModels.size == 0) {
                                binding.vfCurrentService.displayedChild = 1
                            } else {
                                binding.vfCurrentService.displayedChild = 3
                                adapter = CurrentServiceAdapter(serviceModels)
                                binding.listCurrentService.adapter = adapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.vfCurrentService.displayedChild = 2
                        AvaCrashReporter.send(e, "CurrentServiceFragment,activeServiceCallBack")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfCurrentService.displayedChild = 2

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentServicesBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        getActiveService()

        return binding.root
    }



    override fun onResume() {
        super.onResume()
        isRunning = true
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
    }
}