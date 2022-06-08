package ir.transport_x.taxi.adapter

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.File
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ItemCurrentServicesBinding
import ir.transport_x.taxi.dialog.CallDialog
import ir.transport_x.taxi.dialog.GeneralDialog
import ir.transport_x.taxi.fragment.services.ServiceDetailsFragment
import ir.transport_x.taxi.model.ServiceDataModel
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.sqllite.FindDownloadId
import ir.transport_x.taxi.sqllite.FinishedDownload
import ir.transport_x.taxi.sqllite.StartDownload
import ir.transport_x.taxi.utils.*

class CurrentServiceAdapter() : RecyclerView.Adapter<CurrentServiceAdapter.ViewHolder>() {

    companion object {
        val TAG = CurrentServiceAdapter.javaClass.simpleName
        private var TOTAL_VOICE_DURATION = 0
    }


    private var serviceModels: ArrayList<ServiceDataModel> = ArrayList()
    var position = 0
    var lastTime: Long = 0
    lateinit var mp: MediaPlayer
    var timer: Timer? = null
    lateinit var mHolder: ViewHolder

    constructor (serviceList: ArrayList<ServiceDataModel>) : this() {
        this.serviceModels = serviceList
    }

    class ViewHolder(val binding: ItemCurrentServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrentServicesBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val model = serviceModels[holder.adapterPosition]

        holder.binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(
            JSONArray(model.destinationAddress).getJSONObject(0).getString("address")
        )
        holder.binding.llCall.setOnClickListener {
            CallDialog().show(model.phoneNumber, model.mobile)
        }
        holder.binding.txtPrice.text = "${StringHelper.toPersianDigits(StringHelper.setComma(model.price))} "

        holder.binding.txtDate.text = StringHelper.toPersianDigits(
            DateHelper.strPersianEghit(
                DateHelper.parseFormat(
                    model.acceptDate,
                    null
                )
            )
        )

        holder.binding.txtCustomerName.text = model.customerName
        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)


        holder.itemView.setOnClickListener {
            this.position = position
            FragmentHelper.toFragment(
                MyApplication.currentActivity, ServiceDetailsFragment(model,
                    object : ServiceDetailsFragment.CancelServiceListener {
                        override fun onCanceled(isCancel: Boolean) {
                            if (isCancel) {
                                serviceModels.removeAt(position)
                                notifyDataSetChanged()
                            }
                        }

                        override fun onFinishService(isFinish: Boolean) {
                            if (isFinish) {
                                serviceModels.removeAt(position)
                                notifyDataSetChanged()
                            } else {
                                GeneralDialog()
                                    .message("خطایی پیش امده، لطفا مجدد امتحان کنید")
                                    .secondButton("باشه") {}
                                    .show()
                            }
                        }
                    })
            ).add()
        }
    }

    override fun getItemCount(): Int {
        return serviceModels.size
    }
}