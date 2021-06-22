package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.adapter.CurrentServiceAdapter
import ir.team_x.ariana.driver.adapter.WaitingLoadsAdapter
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentFreeLoadsBinding
import ir.team_x.ariana.driver.model.ServiceDataModel
import ir.team_x.ariana.driver.model.WaitingLoadsModel
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil
import org.json.JSONObject

class FreeLoadsFragment : Fragment() {

    private lateinit var binding: FragmentFreeLoadsBinding
    var waitingServiceModels: ArrayList<WaitingLoadsModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFreeLoadsBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        waiting()

        return binding.root
    }

    private fun waiting() {
        binding.vfFreeLoads.displayedChild = 0
        RequestHelper.builder(EndPoint.WAITING)
            .listener(waitingCallBack)
            .get()
    }

    private val waitingCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    waitingServiceModels.clear()
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataArr = jsonObject.getJSONArray("data")
                        for (i in 0 until dataArr.length()) {
                            val dataObj = dataArr.getJSONObject(i)
                            val model = WaitingLoadsModel(
                                dataObj.getInt("id"),
                                dataObj.getInt("customerId"),
                                dataObj.getInt("sourceAddressId"),
                                dataObj.getInt("destinationAddressId"),
                                dataObj.getInt("count"),
                                dataObj.getString("description"),
                                dataObj.getInt("carType"),
                                dataObj.getInt("stopTime"),
                                dataObj.getInt("driverHelp"),
                                dataObj.getString("saveDate"),
                                dataObj.getInt("weight"),
                                dataObj.getInt("userId"),
                                dataObj.getInt("costId"),
                                dataObj.getInt("paymentSide"),
                                dataObj.getInt("cargoId"),
                                dataObj.getInt("status"),
                                dataObj.getInt("driverId"),
                                dataObj.getString("finishDate"),
                                dataObj.getString("voipId"),
                                dataObj.getString("acceptDate"),
                                dataObj.getString("price"),
                                dataObj.getString("customerName"),
                                dataObj.getString("phoneNumber"),
                                dataObj.getString("mobile"),
                                dataObj.getString("stationName"),
                                dataObj.getString("cargoName")
                            )

                            waitingServiceModels.add(model)
                        }

                        if (waitingServiceModels.size==0){
                            binding.vfFreeLoads.displayedChild = 1
                        }else{
                            binding.vfFreeLoads.displayedChild = 3
                            val adapter = WaitingLoadsAdapter(waitingServiceModels)
                            binding.listWaitingLoads.adapter = adapter
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfFreeLoads.displayedChild = 2
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfFreeLoads.displayedChild = 2
            }
        }
    }

}