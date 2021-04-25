package ir.team_x.ariana.driver.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.os.Handler

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