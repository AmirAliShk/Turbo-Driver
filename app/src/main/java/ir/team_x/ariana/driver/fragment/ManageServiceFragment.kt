package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.app.EndPoint
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.FragmentFreeLoadsBinding
import ir.team_x.ariana.driver.databinding.FragmentManageServicesBinding
import ir.team_x.ariana.driver.okHttp.RequestHelper
import org.json.JSONObject

class ManageServiceFragment : Fragment() {

 private lateinit var binding : FragmentManageServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentManageServicesBinding.inflate(inflater, container, false)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        getActiveService()

        return binding.root
    }

    private fun getActiveService(){
        RequestHelper.builder(EndPoint.ACTIVES)
            .listener(activeServiceCallBack)
            .get()
    }

    private val activeServiceCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {

            }
        }
    }

}