package ir.team_x.cloud_transport.taxi_driver.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication.Companion.DIR_DOWNLOAD
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication.Companion.DIR_ROOT
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication.Companion.context
import ir.team_x.cloud_transport.taxi_driver.databinding.DialogDownloadUpdateBinding
import ir.team_x.cloud_transport.taxi_driver.utils.ApkInstallerHelper
import ir.team_x.cloud_transport.taxi_driver.utils.HashHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtilJava
import java.io.File

class DownloadUpdateDialog {

    lateinit var dialog: Dialog
    lateinit var binding: DialogDownloadUpdateBinding

    fun show(url: String) {
        if (MyApplication.currentActivity.isFinishing) return
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogDownloadUpdateBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window?.attributes
        dialog.window?.attributes = wlp
        wlp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        TypeFaceUtilJava.overrideFonts(binding.root)
        startDownload(url)

        val prdown = PRDownloaderConfig.newBuilder().build()
        PRDownloader.initialize(context, prdown)

        dialog.show()
    }

    private fun startDownload(url: String) {

        PRDownloader.download(url, DIR_ROOT + DIR_DOWNLOAD, "${HashHelper.md5Generator(url)}.apk")
            .build()
            .setOnProgressListener {
                binding.updateProgress.max = 100
                binding.updateProgress.progress = it.currentBytes.toInt().toString().substring(0,1).toInt()
                binding.textProgress.text = it.currentBytes.toInt().toString().substring(0,2)
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    ApkInstallerHelper.install(
                        MyApplication.currentActivity,
                        "${HashHelper.md5Generator(url)}.apk"
                    )
                }

                override fun onError(error: com.downloader.Error?) {
                    GeneralDialog()
                        .message("مشکلی در به روز رسانی برنامه به وجود امد لطفا بعد از چند لحظه دوباره تلاش نمایید")
                        .firstButton("تلاش مجدد", ({ startDownload(url) }))
                        .secondButton("فعلا نه") { MyApplication.currentActivity.finish() }
                        .show()
                }
            })
    }
}