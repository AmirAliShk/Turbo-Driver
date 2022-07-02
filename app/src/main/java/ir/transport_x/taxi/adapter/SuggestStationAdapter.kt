package ir.transport_x.taxi.adapter

import java.util.ArrayList
import ir.transport_x.taxi.model.SuggestStationModel
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import com.romainpiel.shimmer.Shimmer
import ir.transport_x.taxi.model.Medal
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.DataHolder
import ir.transport_x.taxi.dialog.LoadingDialog
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.FragmentHelper
import ir.transport_x.taxi.app.MyApplication
import java.lang.Exception
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.databinding.ItemSuggestStationBinding
import java.lang.Runnable
import org.json.JSONObject
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.fragment.ShowRouteTOStationFragment
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONException

class SuggestStationAdapter(private val listData: ArrayList<SuggestStationModel>) : BaseAdapter() {

    lateinit var binding: ItemSuggestStationBinding
    var layoutInflater: LayoutInflater = LayoutInflater.from(MyApplication.currentActivity)

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): Any {
        return listData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, container: View, parent: ViewGroup): View {
        try {
            val suggest = listData[position]
            binding = ItemSuggestStationBinding.inflate(layoutInflater, parent, false)
            TypeFaceUtil.overrideFont(binding.root)
            binding.txtStationCode.setText(suggest.stationCode)
            binding.txtDistanceToStation.text =
                if (suggest.distance <= 0) "در محدوده" else "${suggest.distance} متر"
            binding.txtArrivingTime.text = "${suggest.reachedTime} دقیقه"
            binding.txtPassengerCount.text = "${suggest.passengerCount} نفر"
            binding.txtDriverCount.text = "${suggest.registerCarCount} نفر"
            binding.btnStationRegister.text = "ثبت در محدوده " + suggest.stationCode

            val shimmer = Shimmer()
            shimmer.setDuration(2000)
                .setStartDelay(100)
                .start(binding.shTxtEstimatedTime)

            when (suggest.medal) {
                Medal.golden -> {
                    binding.imgType.setImageResource(R.mipmap.golden)
                    binding.shTxtEstimatedTime.text = "محدوده طلایی"
                }
                Medal.silver -> {
                    binding.imgType.setImageResource(R.mipmap.silver)
                    binding.shTxtEstimatedTime.text = "محدوده نقره ای"
                }
                else -> {
                    binding.imgType.setImageResource(R.mipmap.bronze)
                    binding.shTxtEstimatedTime.text = "محدوده برنزی"
                }
            }

            binding.btnStationRegister.setOnClickListener {
                LoadingDialog.makeLoader()
                RequestHelper.builder(EndPoint.STATION)//todo
                    .addParam("stationCode", suggest.stationCode)
                    .addParam("time", 0)
                    .addParam("lat", suggest.position!!.latitude)
                    .addParam("lng", suggest.position!!.longitude)
                    .listener(onStationRegisterListener)
                    .post()
            }

            binding.btnShowLocation.setOnClickListener {
                DataHolder.instance().suggest = SuggestStationModel(suggest)
                FragmentHelper.toFragment(
                    MyApplication.currentActivity,
                    ShowRouteTOStationFragment()
                )
                    .setFrame(R.id.frame_container)
                    .replace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG ,getView")
        }
        return binding.root
    }

    private var onStationRegisterListener: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable, vararg args: Any) {
            MyApplication.handler.post {
                try {
                    LoadingDialog.dismiss()
                    val `object` = JSONObject(args[0].toString())
                    val success = `object`.getBoolean("success")
                    val message = `object`.getString("message")
                    if (success) {
                        val dataObg = `object`.getJSONObject("data")
                        val status = dataObg.getBoolean("status")
                        val description = dataObg.getString("description")
                        if (status) {
                            MyApplication.currentActivity.onBackPressed()
                            GeneralDialog()
                                .message(description)
                                .firstButton("باشه", null)
                                .show()
                        } else {
                            GeneralDialog()
                                .message(description)
                                .secondButton("باشه", null)
                                .show()
                        }
                    } else {
                        GeneralDialog()
                            .message(message)
                            .secondButton("باشه", null)
                            .show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "$TAG class, onStationRegisterListener method")
                }
            }
        }

        override fun onFailure(reCall: Runnable, e: Exception) {
            MyApplication.handler.post { LoadingDialog.dismiss() }
        }
    }

    companion object {
        private val TAG = SuggestStationAdapter::class.java.simpleName
    }

}