package ir.team_x.ariana.operator.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.DialogGetServiceBinding
import ir.team_x.ariana.driver.fragment.CurrentServiceFragment
import ir.team_x.ariana.driver.model.ServiceModel
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.SoundHelper
import ir.team_x.ariana.driver.utils.StringHelper
import ir.team_x.ariana.driver.utils.TypeFaceUtilJava
import ir.team_x.ariana.driver.webServices.AcceptService
import ir.team_x.ariana.driver.webServices.AcceptService.Listener


class GetServiceDialog() {

    private lateinit var dialog: Dialog
    lateinit var binding: DialogGetServiceBinding

    fun show(serviceModel: ServiceModel) {
        if (MyApplication.currentActivity.isFinishing) return

        dialog = Dialog(MyApplication.currentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGetServiceBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypeFaceUtilJava.overrideFonts(binding.root,MyApplication.iranSansMediumTF)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.windowAnimations = R.style.ExpandAnimation
        dialog.window!!.attributes = wlp
        dialog.setCancelable(false)

        binding.txtOriginAddress.text = StringHelper.toPersianDigits(serviceModel.originAddress)
        binding.txtDestAddress.text = StringHelper.toPersianDigits(serviceModel.destinationDesc)
        binding.txtPrice.text = StringHelper.toPersianDigits(
            StringHelper.setComma(serviceModel.servicePrice).toString() + " تومان"
        )

        binding.btnGetService.setOnClickListener {
            binding.vfAcceptService.displayedChild = 1
            AcceptService().accept(serviceModel.serviceID, object : Listener {
                override fun onSuccess() {
                    binding.vfAcceptService.displayedChild = 0
                    dismiss()
                    MyApplication.handler.postDelayed({
                        SoundHelper.ringing(
                            MyApplication.context,
                            R.raw.accpet,
                            false
                        )
                    }, 2000)

                    FragmentHelper.toFragment(
                        MyApplication.currentActivity,
                        CurrentServiceFragment()
                    )
                        .setStatusBarColor(MyApplication.context.resources.getColor(R.color.colorBlack))
                        .replace()
                }

                override fun onFailure() {
                    binding.vfAcceptService.displayedChild = 0
                }
            })
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

        dialog.show()
    }

    fun dismiss() {
        try {
            if (dialog.isShowing) {
                dialog.dismiss()
                dialog == null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}