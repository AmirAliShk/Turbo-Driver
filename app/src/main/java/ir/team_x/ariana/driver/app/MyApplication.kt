package ir.team_x.ariana.driver.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class MyApplication : Application() {

    companion object {
        lateinit var context: Context
        lateinit var currentActivity: Activity
        lateinit var handler: Handler
        lateinit var prefManager: PrefManager
        val iranSans = "fonts/IRANSans.otf"
        val iranSansBold = "fonts/IRANSANS_BOLD.TTF"
        val iranSansMedium = "fonts/IRANSANS_MEDIUM.TTF"
        lateinit var iranSansTF: Typeface
        lateinit var iranSansBoldTF: Typeface
        lateinit var iranSansMediumTF: Typeface

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

    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        handler = Handler()
        initTypeFace()

        prefManager = PrefManager(context)

    }

    fun initTypeFace() {
        iranSansTF = Typeface.createFromAsset(assets, iranSans)
        iranSansBoldTF = Typeface.createFromAsset(assets, iranSansBold)
        iranSansMediumTF = Typeface.createFromAsset(assets, iranSansMedium)
    }

}