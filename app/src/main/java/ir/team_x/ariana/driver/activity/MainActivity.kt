package ir.team_x.ariana.driver.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.MapFragment
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ActivityMainBinding
import ir.team_x.ariana.driver.fragment.AnnouncementFragment
import ir.team_x.ariana.driver.fragment.FinancialFragment
import ir.team_x.ariana.driver.fragment.FreeLoadsFragment
import ir.team_x.ariana.driver.fragment.ServiceManagementFragment
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
            FragmentHelper.toFragment(MyApplication.currentActivity, MapFragment()).replace()
        }

        binding.llServiceManagement.setOnClickListener {
            FragmentHelper.toFragment(MyApplication.currentActivity, ServiceManagementFragment())
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
}