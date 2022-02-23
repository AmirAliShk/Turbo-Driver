package ir.team_x.cloud_transport.taxi_driver.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Environment
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import ir.team_x.cloud_transport.taxi_driver.BuildConfig
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.fragment.services.FreeLoadsFragment
import ir.team_x.cloud_transport.taxi_driver.push.AvaCrashReporter
import ir.team_x.cloud_transport.taxi_driver.push.AvaFactory
import ir.team_x.cloud_transport.taxi_driver.utils.FragmentHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtil
import org.acra.ACRA
import org.acra.annotation.AcraHttpSender
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.HttpSenderConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.sender.HttpSender
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*

@AcraHttpSender(uri = "http://transport.team-x.ir:6061/api/crashReport", httpMethod = HttpSender.Method.POST)
class MyApplication : Application() {

    companion object {
        lateinit var context: Context
        lateinit var currentActivity: Activity
        lateinit var handler: Handler
        lateinit var prefManager: PrefManager
        val iranSans = "fonts/IRANSans.otf"
        val iranSansBold = "fonts/IRANSANS_BOLD.TTF"
        val iranSansMedium = "fonts/IRANSANS_MEDIUM.TTF"
        lateinit var SOUND: String
        lateinit var iranSansTF: Typeface
        lateinit var iranSansBoldTF: Typeface
        lateinit var iranSansMediumTF: Typeface

        val DIR_ROOT = Environment.getExternalStorageDirectory().absolutePath + "/TaxiDriverCloudTransport/"
        val VOICE_FOLDER_NAME = "voice/"
        val DIR_DOWNLOAD = "Download/"

        fun showSnackBar(text: String) {
            val coordinatorLayout = currentActivity.findViewById(android.R.id.content) as View

            val snackBar =
                Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).setAction("مشاهده") {
                    if (FreeLoadsFragment.isRunning)
                        return@setAction
                    FragmentHelper.toFragment(currentActivity, FreeLoadsFragment())
                        .replace()
                }
            snackBar.setActionTextColor(Color.WHITE)
            val snackBarView = snackBar.view
            snackBarView.setBackgroundColor(currentActivity.resources.getColor(R.color.colorPink))
            val textView =
                snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.WHITE)
            textView.text = text
            textView.gravity = Gravity.RIGHT
            textView.textSize = 20f
            TypeFaceUtil.overrideFont(snackBarView)
            snackBar.show()
        }

        fun Toast(message: String?, duration: Int) {
            handler.post(Runnable {
                val layoutInflater = LayoutInflater.from(currentActivity)
                val v = layoutInflater.inflate(R.layout.item_toast, null)
                TypeFaceUtil.overrideFont(v)
                val text = v.findViewById<View>(R.id.text) as TextView
                text.text = message
                val toast = android.widget.Toast(currentActivity)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.duration = duration
                toast.view = v
                toast.show()
            })
        }

        fun avaStart() {
            if (prefManager.getAvaPID() == 0) return
            if (prefManager.getAvaToken() == null) return
            AvaFactory.getInstance(context)
                .setUserID(prefManager.getDriverId().toString())
                .setProjectID(prefManager.getAvaPID())
                .setToken(prefManager.getAvaToken())
                .setAddress(EndPoint.PUSH_ADDRESS)
                .start();
        }

    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        handler = Handler()
        initTypeFace()
        SOUND = "android.resource://${context.packageName}/"
        prefManager = PrefManager(context)

        File(DIR_ROOT + DIR_DOWNLOAD).mkdirs()
        val file = File("$DIR_ROOT$VOICE_FOLDER_NAME.nomedia")
        try {
            if (!file.parentFile.exists())
                file.parentFile.mkdirs()
            if (!file.exists())
                file.createNewFile()
//            file.createNewFile();
        } catch (e:Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "MyApplication class, onCreate method ")
        }

        val languageToLoad = "fa_IR"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        avaStart()
        initACRA()

    }

    private fun initACRA() {
//        val authHeaderMap: MutableMap<String, String?> = HashMap()
//        authHeaderMap["Authorization"] = prefManager.getAuthorization()
//        authHeaderMap["id_token"] = prefManager.getIdToken()
//        val builder: CoreConfigurationBuilder = CoreConfigurationBuilder(this)
//            .setBuildConfigClass(BuildConfig::class.java)
//            .setReportFormat(StringFormat.JSON)
//        val httpPluginConfigBuilder: HttpSenderConfigurationBuilder =
//            builder.getPluginConfigurationBuilder(
//                HttpSenderConfigurationBuilder::class.java
//            )
//                .setUri(EndPoint.CRASH_REPORT)
//                .setHttpMethod(HttpSender.Method.POST)
//                .setHttpHeaders(authHeaderMap)
//                .setEnabled(true)
        //        if (!BuildConfig.DEBUG)
        ACRA.init(this)
    }

    fun initTypeFace() {
        iranSansTF = Typeface.createFromAsset(assets, iranSans)
        iranSansBoldTF = Typeface.createFromAsset(assets, iranSansBold)
        iranSansMediumTF = Typeface.createFromAsset(assets, iranSansMedium)
    }

}