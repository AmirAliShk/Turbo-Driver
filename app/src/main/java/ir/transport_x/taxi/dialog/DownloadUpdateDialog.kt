package ir.transport_x.taxi.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.app.MyApplication.Companion.DIR_DOWNLOAD
import ir.transport_x.taxi.app.MyApplication.Companion.DIR_ROOT
import ir.transport_x.taxi.app.MyApplication.Companion.context
import ir.transport_x.taxi.databinding.DialogDownloadUpdateBinding
import ir.transport_x.taxi.utils.ApkInstallerHelper
import ir.transport_x.taxi.utils.HashHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava

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
                binding.updateProgress.progress = ((it.currentBytes * 100) / it.totalBytes).toInt()
                binding.textProgress.text = "${((it.currentBytes * 100) / it.totalBytes).toInt()} %"
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