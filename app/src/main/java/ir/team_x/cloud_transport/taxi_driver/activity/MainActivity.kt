package ir.team_x.cloud_transport.taxi_driver.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.EndPoint
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.ActivityMainBinding
import ir.team_x.cloud_transport.taxi_driver.dialog.GeneralDialog
import ir.team_x.cloud_transport.taxi_driver.fragment.ProfileFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.SupportFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.financial.FinancialFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.news.NewsDetailsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.news.NewsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.CurrentServiceFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.FreeLoadsFragment
import ir.team_x.cloud_transport.taxi_driver.fragment.services.ServiceHistoryFragment
import ir.team_x.cloud_transport.taxi_driver.gps.DataGatheringService
import ir.team_x.cloud_transport.taxi_driver.gps.GPSEnable
import ir.team_x.cloud_transport.taxi_driver.gps.LocationAssistant
import ir.team_x.cloud_transport.taxi_driver.gps.MyLocation
import ir.team_x.cloud_transport.taxi_driver.okHttp.RequestHelper
import ir.team_x.cloud_transport.taxi_driver.utils.*
import ir.team_x.cloud_transport.taxi_driver.webServices.UpdateCharge
import ir.team_x.cloud_transport.operator.utils.TypeFaceUtil
import ir.team_x.cloud_transport.taxi_driver.fragment.MapFragment
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), NewsDetailsFragment.RefreshNotificationCount {

    companion object{
        lateinit var binding: ActivityMainBinding

        fun openDrawer(){
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }

    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        MyApplication.handler.postDelayed({
            if (!MainActivity().isFinishing) {
                FragmentHelper.toFragment(MyApplication.currentActivity, MapFragment()).replace()
            }
        }, 100)

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

        binding.llSupport.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, SupportFragment()).replace()
            binding.drawerLayout.closeDrawers()
        }

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
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        KeyBoardHelper.hideKeyboard()
        if (MapFragment().isInLayout) {

            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return
            }


            GeneralDialog()
                .message("آیا از خروج خود اطمینان دارید؟")
                .firstButton("بله") {
                    finish()
                }
                .secondButton("خیر") {}
                .show()
        } else {
            super.onBackPressed()
        }
    }

    override fun refreshNotification() {
        TODO("Not yet implemented")
    }

}