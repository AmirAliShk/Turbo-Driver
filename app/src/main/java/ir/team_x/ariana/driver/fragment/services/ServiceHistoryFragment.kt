package ir.team_x.ariana.driver.fragment.services

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.adapter.FinishedAdapter
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.*
import ir.team_x.ariana.driver.model.FinishedModel
import ir.team_x.ariana.driver.okHttp.RequestHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil
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
                                dataObj.getInt("sourceAddressId"),
                                dataObj.getInt("destinationAddressId"),
                                dataObj.getString("saveDate"),
                                dataObj.getInt("status"),
                                dataObj.getString("finishDate"),
                                dataObj.getString("acceptDate"),
                                dataObj.getString("price"),
                                dataObj.getString("statusDes"),
                                dataObj.getString("sourceAddress"),
                                dataObj.getString("destinationAddress"),
                                dataObj.getString("statusColor")
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