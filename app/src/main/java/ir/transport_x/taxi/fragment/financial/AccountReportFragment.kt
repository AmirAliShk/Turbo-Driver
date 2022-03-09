package ir.transport_x.taxi.fragment.financial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.transport_x.taxi.adapter.AccountReportAdapter
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentAccountReportBinding
import ir.transport_x.taxi.model.AccountReportModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.DateHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONObject

class AccountReportFragment : Fragment() {
 private lateinit var binding : FragmentAccountReportBinding
    var models: ArrayList<AccountReportModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentAccountReportBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        getReport()
        return binding.root
    }

    private fun getReport() {
        binding.vfReport.displayedChild = 0
        RequestHelper.builder(EndPoint.ACCOUNT_REP)
            .listener(getReportCallBack)
            .addParam("fromDate",
                DateHelper.strPersianFive(
                    DateHelper.getBeforeDays(7).time).substring(0,10))
            .addParam("toDate",
                DateHelper.strPersianFive(
                    DateHelper.getCurrentGregorianDate().time).substring(0,10))
            .post()
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
                            val accountReportModel = AccountReportModel(
                                dataObj.getInt("id"),
                                dataObj.getInt("driverId"),
                                dataObj.getString("saveDate"),
                                dataObj.getInt("userId"),
                                dataObj.getString("updateDate"),
                                dataObj.getString("updateUserId"),
                                dataObj.getString("price"),
                                dataObj.getInt("type"),
                                dataObj.getString("description"),
                                dataObj.getString("paymentDate"),
                                dataObj.getString("serviceId"),
                                dataObj.getString("paymentTypeName")
                            )
                            models.add(accountReportModel)
                        }
                        if (models.size==0){
                            binding.vfReport.displayedChild = 1
                        }else{
                            binding.vfReport.displayedChild = 3
                            val adapter = AccountReportAdapter(models)
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