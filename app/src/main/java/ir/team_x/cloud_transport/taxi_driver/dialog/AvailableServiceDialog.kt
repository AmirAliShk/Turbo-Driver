package ir.team_x.cloud_transport.taxi_driver.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.adapter.AvailableServiceAdapter
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.DialogAvailableServiceBinding
import ir.team_x.cloud_transport.taxi_driver.model.ServiceModel
import ir.team_x.cloud_transport.taxi_driver.push.AvaCrashReporter
import ir.team_x.cloud_transport.taxi_driver.utils.KeyBoardHelper
import ir.team_x.cloud_transport.taxi_driver.utils.SoundHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtilJava

class AvailableServiceDialog {

    companion object {
        var dialog: Dialog? = null
        lateinit var binding: DialogAvailableServiceBinding
        lateinit var serviceModels: ArrayList<ServiceModel>

        fun show(serviceModel: ServiceModel) {
            if (MyApplication.currentActivity.isFinishing) return
            if (dialog == null) {
                dialog = Dialog(MyApplication.currentActivity)
                dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
                binding =
                    DialogAvailableServiceBinding.inflate(LayoutInflater.from(MyApplication.context))
                dialog!!.setContentView(binding.root)
                TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
                dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val wlp: WindowManager.LayoutParams? = dialog!!.window?.attributes
                wlp?.gravity = Gravity.CENTER
                wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
                wlp?.windowAnimations = R.style.ExpandAnimation
                dialog!!.window?.attributes = wlp
                dialog!!.setCancelable(false)

                serviceModels = ArrayList()
            }
            val serviceModel = serviceModel

            for (i in 0 until serviceModels.size) {
                if (serviceModel.serviceID == serviceModels[i].serviceID) {
                    SoundHelper.stop()
                    return
                }
            }
            serviceModels.add(serviceModel)

            val availableServiceAdapter = AvailableServiceAdapter(serviceModels)
            availableServiceAdapter.notifyDataSetChanged()
            binding.listServices.adapter = availableServiceAdapter

            dialog!!.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                }
                false
            }

            dialog!!.show()

        }

        fun dismiss() {
            try {
                dialog?.dismiss()
                dialog = null
                SoundHelper.stop()
                if (::serviceModels.isInitialized)
                    serviceModels.clear()
                KeyBoardHelper.hideKeyboard()
            } catch (e: Exception) {
                AvaCrashReporter.send(e, "AvailableServiceDialog class, dismiss method")
            }
        }
    }
}