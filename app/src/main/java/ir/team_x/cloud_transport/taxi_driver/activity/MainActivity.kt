package ir.team_x.cloud_transport.taxi_driver.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ActivityMainBinding
import ir.team_x.cloud_transport.taxi_driver.dialog.AvailableServiceDialog
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.taxi_driver.fragment.ProfileFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.news.NewsDetailsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.news.NewsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.ServiceHistoryFragment
import ir.team_x.cloud_transport.taxi_driver.utils.*
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtil
import ir.team_x.cloud_transport.taxi_driver.fragment.MapFragment


class MainActivity : AppCompatActivity(), NewsDetailsFragment.RefreshNotificationCount {

    companion object {
        lateinit var binding: ActivityMainBinding

        fun openDrawer() {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }

    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtCharge, MyApplication.iranSansMediumTF)
        TypeFaceUtil.overrideFont(binding.txtDriverName, MyApplication.iranSansMediumTF)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.colorBlack)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        binding.txtAppVersion.text = AppVersionHelper(MyApplication.context).versionName
        binding.txtDriverName.text = MyApplication.prefManager.getUserName()

        MyApplication.handler.postDelayed({
            if (!MainActivity().isFinishing) {
                FragmentHelper.toFragment(MyApplication.currentActivity, MapFragment()).replace()
            }
        }, 100)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding.txtAppVersion.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        binding.llAccount.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ProfileFragment())
                .replace()
            binding.drawerLayout.closeDrawers()
        }

        binding.llServiceHistory.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ServiceHistoryFragment())
                .replace()
            binding.drawerLayout.closeDrawers()
        }

        binding.llNewsMenu.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, NewsFragment()).replace()
            binding.drawerLayout.closeDrawers()
        }

        binding.llCall.setOnClickListener {
            CallHelper.make(MyApplication.prefManager.supportNumber)
            binding.drawerLayout.closeDrawers()
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                binding.txtCharge.text =
                    "شارژ شما ${StringHelper.toPersianDigits(StringHelper.setComma(MyApplication.prefManager.getCharge()))} تومان "
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })

    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
        KeyBoardHelper.hideKeyboard()
        MyApplication.prefManager.setAppRun(true)
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onPause() {
        super.onPause()
        MyApplication.prefManager.setAppRun(false)
        AvailableServiceDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        KeyBoardHelper.hideKeyboard()

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        var mapFragment = supportFragmentManager.findFragmentByTag(MapFragment.TAG)
        if (mapFragment == null) return

        if (supportFragmentManager.backStackEntryCount > 0 && !mapFragment.isVisible) {
            super.onBackPressed()
        }else if (mapFragment.isVisible) {
            GeneralDialog()
                .message("آیا از خروج خود اطمینان دارید؟")
                .firstButton("بله") {
                    finish()
                }
                .secondButton("خیر") {}
                .show()
        }
    }

    override fun refreshNotification() {
//        TODO("Not yet implemented")
    }

}