package ir.transport_x.taxi.fragment.services

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.downloader.Progress
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import ir.transport_x.taxi.app.EndPoint
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentServiceDetailsBinding
import ir.transport_x.taxi.dialog.*
import ir.transport_x.taxi.model.ServiceDataModel
import ir.transport_x.taxi.okHttp.RequestHelper
import ir.transport_x.taxi.utils.*
import ir.transport_x.taxi.webServices.UpdateCharge
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ServiceDetailsFragment(
    serviceModel: ServiceDataModel,
    cancelServiceListener: CancelServiceListener
) : Fragment() {
    companion object {
        val TAG: String = ServiceDetailsFragment::class.java.simpleName
    }

    private val serviceModel = serviceModel
    private lateinit var binding: FragmentServiceDetailsBinding
    var lastTime: Long = 0

    interface CancelServiceListener {
        fun onCanceled(isCancel: Boolean)
        fun onFinishService(isFinish: Boolean)
    }

    val cancelServiceListener: CancelServiceListener = cancelServiceListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentServiceDetailsBinding.inflate(inflater, container, false)
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        binding.txtDate.text = StringHelper.toPersianDigits(
            DateHelper.strPersianEghit(
                DateHelper.parseFormat(
                    serviceModel.acceptDate + "",
                    null
                )
            )
        )

        StringHelper.toPersianDigits(
            JSONArray(serviceModel.destinationAddress).getJSONObject(0).getString("address")
        )

        var stopTime = "بدون توقف"

        when (serviceModel.stopTime) {
            0 -> {
                stopTime = "بدون توقف"
            }
            5 -> {
                stopTime = "۵ دقیقه"
            }
            10 -> {
                stopTime = "۱۰ دقیقه"
            }
            20 -> {
                stopTime = "۲۰ دقیقه"
            }
            30 -> {
                stopTime = "۳۰ دقیقه"
            }
            40 -> {
                stopTime = "۴۰ دقیقه"
            }
            50 -> {
                stopTime = "۵۰ دقیقه"
            }
            60 -> {
                stopTime = "۱ ساعت"
            }
            90 -> {
                stopTime = "۱.۵ ساعت"
            }
            120 -> {
                stopTime = "۲ ساعت"
            }
            150 -> {
                stopTime = "۲.۵ ساعت"
            }
            180 -> {
                stopTime = "۳ ساعت"
            }
        }

        binding.txtStopTime.text = stopTime

        binding.txtCustomerName.text = serviceModel.customerName
        binding.txtOriginAddress.text = StringHelper.toPersianDigits(serviceModel.sourceAddress)
        binding.txtPrice.text = "${
            StringHelper.toPersianDigits(
                StringHelper.setComma(serviceModel.price)
            )
        } تومان "
        binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(
            JSONArray(serviceModel.destinationAddress).getJSONObject(0).getString("address")
        )
        binding.txtTell.text = StringHelper.toPersianDigits(serviceModel.phoneNumber)
        binding.txtMobile.text = StringHelper.toPersianDigits(serviceModel.mobile)
        if (serviceModel.description.trim() == "" && serviceModel.fixedDescription.trim() == "") {
            binding.llDesc.visibility = View.GONE
        } else {
            if (serviceModel.description.trim() != "" && serviceModel.fixedDescription.trim() != "") {
                binding.txtDescriptionDetail.text = StringHelper.toPersianDigits(
                    "${serviceModel.description} و ${serviceModel.fixedDescription}"
                )
            } else if (serviceModel.description.trim() != "") {
                binding.txtDescriptionDetail.text =
                    StringHelper.toPersianDigits(serviceModel.description)
            } else if (serviceModel.fixedDescription.trim() != "") {
                binding.txtDescriptionDetail.text =
                    StringHelper.toPersianDigits(serviceModel.fixedDescription)
            }
        }
        if (serviceModel.discount == "0") {
            binding.llDiscount.visibility = View.GONE
        } else {
            binding.llDiscount.visibility = View.VISIBLE
            binding.txtDiscount.text =
                StringHelper.toPersianDigits(
                    StringHelper.setComma(serviceModel.discount)
                )
        }

        binding.llCancel.setOnClickListener {
            CancelServiceDialog(object : CancelServiceDialog.CancelServiceListener {
                override fun onCanceled(isCancel: Boolean) {
                    cancelServiceListener.onCanceled(isCancel)
                }
            }).show(serviceModel)
        }
        binding.llCall.setOnClickListener {
            CallDialog().show(serviceModel.phoneNumber, serviceModel.mobile)
        }
        binding.txtFinish.setOnClickListener {
            if (MyApplication.prefManager.pricing == 1) {
                bill(serviceModel.id, serviceModel.priceService)
            } else {
                GetPriceDialog().show(serviceModel.id,
                    object : GetPriceDialog.FinishServiceListener {
                        override fun onFinishService(isFinish: Boolean) {
                            cancelServiceListener.onFinishService(isFinish)
                        }

                    })
            }
        }

        binding.imgPlayVoice.setOnClickListener {
            val voiceName = "voipId.mp3"
            VoiceHelper.getInstance()
                .autoplay("", voiceName, "voipId", object : VoiceHelper.OnVoiceListener {
                    override fun onFileExist() {
                    }

                    override fun onStartDownload() {
                        binding.vfDownloadOrPlay.displayedChild = 1
                    }

                    override fun onProgressDownload(progress: Progress?) {
                        val percent =
                            (progress!!.currentBytes / progress.totalBytes.toDouble() * 100).toInt()
                        Log.i("ServiceDetailsFragment", "onProgress: $percent")

                        binding.progressBar.progress = percent
                        if (Calendar.getInstance().timeInMillis - lastTime > 500) {
                            binding.textProgress.text = "$percent %"
                            lastTime = Calendar.getInstance().timeInMillis
                        }
                    }

                    override fun onDownloadCompleted() {
                        binding.vfDownloadOrPlay.displayedChild = 0
                        binding.vfPlayPause.displayedChild = 1
                    }

                    override fun onDownloadError() {
                    }

                    override fun onDownload401Error() {
                    }

                    override fun onDownload404Error() {
                        binding.vfDownloadOrPlay.displayedChild = 2
                    }

                    override fun onDuringInit() {
//                        binding.skbTimer.setProgress(0f)
                    }

                    override fun onEndOfInit(maxDuration: Int) {
                        binding.skbTimer.max = maxDuration.toFloat()
                    }

                    override fun onPlayVoice() {
                        binding.vfPlayPause.displayedChild = 1
                    }

                    override fun onTimerTask(currentDuration: Int) {
                        binding.skbTimer.setProgress(currentDuration.toFloat())
                        val timeRemaining: Int = currentDuration / 1000
                        val strTimeRemaining = String.format(
                            Locale("en_US"),
                            "%02d:%02d",
                            timeRemaining / 60,
                            timeRemaining % 60
                        )
                        binding.txtTime.text = strTimeRemaining
                    }

                    override fun onPauseVoice() {
                        binding.vfPlayPause.displayedChild = 0
                    }

                    override fun onVoipIdEqual0() {
                        binding.vfDownloadOrPlay.displayedChild = 2
                    }
                })

            binding.skbTimer.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    val timeRemaining = seekParams.progress / 1000
                    val strTimeRemaining = String.format(
                        Locale("en_US"),
                        "%02d:%02d",
                        timeRemaining / 60,
                        timeRemaining % 60
                    )
                    binding.txtTime.text = strTimeRemaining
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {}
                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                    seekBar.let { VoiceHelper.getInstance().staticMd()?.seekTo(it.progress) }
                }
            }

        }

        return binding.root
    }

    private fun bill(serviceId: Int, price: String) {
        binding.vfEndService.displayedChild = 1
        RequestHelper.builder(EndPoint.BILL)
            .listener(billCallBack)
            .addPath(serviceId.toString())
            .addPath(price)
            .get()
    }

    private val billCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfEndService.displayedChild = 0
                    val jsonObject = JSONObject(args[0].toString())
                    val success = jsonObject.getBoolean("success")
                    val message = jsonObject.getString("message")
                    if (success) {
                        val dataObj = jsonObject.getJSONObject("data")

                        FactorDialog().show(dataObj, serviceModel.id,
                            object : FactorDialog.FinishServiceListener {
                                override fun onFinishService(isFinish: Boolean) {
                                    cancelServiceListener.onFinishService(isFinish)
                                }

                            })

                    } else {
                        cancelServiceListener.onCanceled(false)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.vfEndService.displayedChild = 0
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfEndService.displayedChild = 0
            }
        }
    }

}