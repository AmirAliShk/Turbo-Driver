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
                    model.saveDate + "",
                    null
                )
            )
        )

        holder.binding.txtCustomerName.text = model.customerName
        holder.binding.txtOriginAddress.text = StringHelper.toPersianDigits(model.sourceAddress)
        holder.binding.imgPlayVoice.setOnClickListener {
            mHolder = holder
            val voiceName = "${model.id}.mp3"
            val file = File(MyApplication.DIR_ROOT + MyApplication.VOICE_FOLDER_NAME + voiceName)
            if (file.exists()) {
                initVoice(Uri.fromFile(file))
                playVoice()
                holder.binding.vfPlayPause.displayedChild = 1 //todo check
            } else {
                startDownload(
                    holder.binding.vfPlayPause,
//                holder.binding.progressBar,
//                holder.binding.textProgress,
                    "${model.id}",
//                    "${EndPoint.GET_VOICE}/${model.id}",
                    voiceName,
                    mHolder
                )
                holder.binding.vfPlayPause.displayedChild = 1 //todo check
            } }
        holder.binding.imgPauseVoice.setOnClickListener {
//            mHolder.binding.vfPlayPause.
        }

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

    private fun startDownload(
        vfDownload: ViewFlipper?,
//        progressBar: ProgressBar,
//        textProgress: TextView,
        urlString: String,
        fileName: String,
    holder: ViewHolder) {
        holder.binding.vfPlayPause.displayedChild = 2
        Log.i(TAG, "show: $urlString")
        try {
            val url = URL(urlString)
//            String dirPathTemp = MyApplication.DIR_ROOT + "temp/";
            val dirPath: String = MyApplication.DIR_ROOT
            val file = File(dirPath + MyApplication.VOICE_FOLDER_NAME + fileName)
            var downloadId: Int = FindDownloadId.execte(urlString)
            if (file.exists() && downloadId != -1) {
                PRDownloader.resume(downloadId)
            } else {
                downloadId = PRDownloader.download(url.toString(), dirPath, fileName)
                    .setHeader("Authorization", MyApplication.prefManager.getAuthorization())
                    .setHeader("id_token", MyApplication.prefManager.getIdToken())
                    .build()
                    .setOnStartOrResumeListener {}
                    .setOnPauseListener {}
                    .setOnCancelListener {}
                    .setOnProgressListener { progress ->
                        val percent =
                            (progress.currentBytes / progress.totalBytes as Double * 100) as Int
                        Log.i(TAG, "onProgress: $percent")
//                        progressBar.progress = percent //todo checking for progress
                        if (Calendar.getInstance().timeInMillis - lastTime > 500) {
//                            textProgress.text = "$percent %" //todo checking for progress
                            lastTime = Calendar.getInstance().timeInMillis // todo try to checking again
                        }
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            FinishedDownload.execute(urlString)
                            //                                FileHelper.moveFile(dirPath, fileName, dirPath);
//                            if (holder.binding.vfPlayPause != null)
                            holder.binding.vfPlayPause.displayedChild = 0
                            val file = File(dirPath + fileName)
                            MyApplication.handler.postDelayed({
                                initVoice(Uri.fromFile(file))
                                playVoice()
                            }, 500)
                        }

                        override fun onError(error: Error) {
//                    error.getConnectionException().printStackTrace();
                            Log.e(TAG, "onError: " + error.responseCode.toString() + "")
                            vfDownload!!.displayedChild = 2
                            FileHelper.deleteFile(dirPath, fileName)
                        }
                    })
                StartDownload.execute(downloadId, url.toString(), dirPath + fileName)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            AvaCrashReporter.send(
                e,
                TAG + "startDownload MalformedURLException"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(
                e,
                TAG + "startDownload Exception"
            )
        }
    }

    private fun initVoice(uri: Uri) {
        try {
            mp = MediaPlayer.create(MyApplication.context, uri)
            mp.setOnCompletionListener(OnCompletionListener { mp: MediaPlayer? ->
                mHolder.binding.skbTimer.setProgress(0F)
                pauseVoice()
            })
            TOTAL_VOICE_DURATION = mp.duration
            mHolder.binding.skbTimer.max = TOTAL_VOICE_DURATION.toFloat()
            val strTime = String.format(
                Locale("en_US"),
                "%02d:%02d",
                TOTAL_VOICE_DURATION / 1000 / 60,
                TOTAL_VOICE_DURATION / 1000 % 60
            )
//            mHolder.binding.txtTime.setText(strTime)
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, TAG + "initVoice")
        }
    }

    private fun playVoice() {
        try {
            mp.start()
            mHolder.binding.vfPlayPause.displayedChild = 1
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, TAG + "playVoice")
        }
        startTimer()
    }

    private fun startTimer() {
        if (timer != null) {
            return
        }
        timer = Timer()
        val task = UpdateSeekBar()
        timer?.scheduleAtFixedRate(task, 500, 1000)
    }

    private fun onDestroy() {
        try {
            pauseVoice()
            cancelTimer()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, TAG + "onDestroy")
        }
    }

    private fun pauseVoice() {
        try {
            mp.pause()
            mHolder.binding.vfPlayPause.displayedChild = 0
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, TAG + "pauseVoice")
        }
        cancelTimer()
    }

    private fun cancelTimer() {
        try {
            if (timer == null) return;
            timer?.cancel()
            timer = null
        } catch (e: Exception) {
            e.printStackTrace();
            AvaCrashReporter.send(e, TAG + "cancelTimer");
        }
    }

    private inner class UpdateSeekBar : TimerTask() {
        override fun run() {
            if (mp != null) {
                try {
                    MyApplication.handler.post {
                        Log.i(
                            TAG,
                            "onStopTrackingTouch run: " + mp.currentPosition
                        )
                        mHolder.binding.skbTimer.setProgress(mp.currentPosition.toFloat())
                        val timeRemaining: Int = mp.getCurrentPosition() / 1000
                        val strTimeRemaining = String.format(
                            Locale("en_US"),
                            "%02d:%02d",
                            timeRemaining / 60,
                            timeRemaining % 60
                        )
//                        mHolder.binding.txtTimeRemaining.setText(strTimeRemaining)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AvaCrashReporter.send(
                        e,
                        TAG + "UpdateSeekBar"
                    )
                }
            }
        }
    }
}