package ir.transport_x.taxi.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import ir.transport_x.taxi.R
import ir.transport_x.taxi.adapter.AvailableServiceAdapter
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.DialogAvailableServiceBinding
import ir.transport_x.taxi.model.ServiceModel
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.KeyBoardHelper
import ir.transport_x.taxi.utils.SoundHelper
import ir.transport_x.taxi.utils.TypeFaceUtilJava

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