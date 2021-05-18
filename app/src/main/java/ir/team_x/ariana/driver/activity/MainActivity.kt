package ir.team_x.ariana.driver.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityMainBinding
import ir.team_x.ariana.driver.fragment.*
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = resources.getColor(R.color.pageBackground)
            window.statusBarColor = resources.getColor(R.color.actionBar)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        TypeFaceUtil.overrideFont(binding.root, MyApplication.iranSansMediumTF)

        binding.imgMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        }

        binding.llMap.setOnClickListener {
            Toast.makeText(MyApplication.context, "map ", Toast.LENGTH_SHORT).show()
        }

        binding.llServiceManagement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ManageServiceFragment())
                .replace()
        }

        binding.llFreeLoads.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, FreeLoadsFragment()).replace()
        }

        binding.llFinancial.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, FinancialFragment()).replace()
        }

        binding.imgAnnouncement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, AnnouncementFragment())
                .replace()
        }

    }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0 || supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

}