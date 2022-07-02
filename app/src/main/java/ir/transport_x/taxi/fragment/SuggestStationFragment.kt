package ir.transport_x.taxi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.transport_x.taxi.adapter.SuggestStationAdapter
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentSuggestStationBinding
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.model.Medal
import ir.transport_x.taxi.model.SuggestStationModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONObject

class SuggestStationFragment : Fragment() {
    val TAG: String = SuggestStationFragment::class.java.simpleName
    lateinit var binding: FragmentSuggestStationBinding

    var adapter: SuggestStationAdapter? = null
    var data = ArrayList<SuggestStationModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuggestStationBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)

//        binding.skbDistance.max = MyApplication.prefManager.getSuggestStationMaxDistance()
//        binding.skbDistance.min = MyApplication.prefManager.getSuggestStationMinDistance()
//        binding.skbDistance.tickCount = MyApplication.prefManager.getSuggestStationCount()//todo
        binding.skbDistance.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {}
            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                MyApplication.handler.postDelayed(
                    { if (seekBar != null) getSuggestStation(seekBar.progress) },
                    500
                )
            }
        }
        MyApplication.handler.postDelayed({
            getSuggestStation(
                binding.skbDistance.progress
            )
        }, 300)
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        return binding.root
    }

    private fun getSuggestStation(distance: Int) {
        binding.vfLoader.displayedChild = 0
        RequestHelper.builder(EndPoint.STATION)//todo
            .addPath(MyApplication.prefManager.getLastLocation().latitude.toString() + "")
            .addPath(MyApplication.prefManager.getLastLocation().longitude.toString() + "")
            .addPath(distance.toString() + "")
            .listener(onGetSuggestStation)
            .get()
    }

    private var onGetSuggestStation: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any) {
            MyApplication.handler.post {
                try {
                    val `object` = JSONObject(args[0].toString())
                    val success = `object`.getBoolean("success")
                    val message = `object`.getString("message")
                    data.clear()
                    if (success) {
                        val dataObg = `object`.getJSONArray("data")
                        for (i in 0 until dataObg.length()) {
                            val ssModel = SuggestStationModel()
                            val items = dataObg.getJSONObject(i)
                            ssModel.stationCode = items.getInt("StCode")
                            ssModel.countService = items.getInt("countService")
                            ssModel.position = LatLng(
                                items.getDouble("lat"),
                                items.getDouble("lng")
                            )
                            ssModel.fromTime = items.getString("fromTime")
                            ssModel.toTime = items.getString("toTime")
                            ssModel.distance = items.getInt("distance")
                            ssModel.stationRadius = items.getInt("dist")
                            ssModel.reachedTime = items.getInt("reachedTime")
                            ssModel.estimatedService = items.getInt("estimatedService")
                            ssModel.registerCarCount = items.getInt("registerCarCount")
                            ssModel.passengerCount = items.getInt("passengerCount")
                            ssModel.updateTime = items.getString("updateTime")
                            ssModel.estimatedTime = items.getInt("estimatedTime")
                            ssModel.medal = Medal.valueOf(items.getString("medal"))
                            ssModel.stationName = items.getString("stName")
                            data.add(ssModel)
                        }
                        if (data.size < 1) {
                            binding.vfLoader.displayedChild = 2
                        } else {
                            binding.vfLoader.displayedChild = 1
                            adapter = SuggestStationAdapter(data)
                            binding.lstSuggestStation.adapter = adapter
                        }
                    } else {
                        GeneralDialog()
                            .message(message)
                            .secondButton("باشه", null)
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "$TAG ,onGetSuggestStation")
                }
            }
        }
    }

}