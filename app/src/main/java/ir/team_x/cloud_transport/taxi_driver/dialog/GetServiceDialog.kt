package ir.team_x.cloud_transport.taxi_driver.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import ir.team_x.cloud_transport.taxi_driver.R
import ir.team_x.cloud_transport.taxi_driver.app.MyApplication
import ir.team_x.cloud_transport.taxi_driver.databinding.DialogGetServiceBinding
import ir.team_x.cloud_transport.taxi_driver.model.ServiceModel
import ir.team_x.cloud_transport.taxi_driver.utils.StringHelper
import ir.team_x.cloud_transport.taxi_driver.utils.TypeFaceUtilJava
import ir.team_x.cloud_transport.taxi_driver.webServices.AcceptService
import ir.team_x.cloud_transport.taxi_driver.webServices.AcceptService.Listener

class GetServiceDialog() {

    private lateinit var dialog: Dialog
    lateinit var binding: DialogGetServiceBinding

    fun show(serviceModel: ServiceModel) {
        if (MyApplication.currentActivity.isFinishing) return

        dialog = Dialog(MyApplication.currentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGetServiceBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypeFaceUtilJava.overrideFonts(binding.root, MyApplication.iranSansMediumTF)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window!!.attributes
        wlp.gravity = Gravity.CENTER
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.windowAnimations = R.style.ExpandAnimation
        dialog.window!!.attributes = wlp
        dialog.setCancelable(false)

        binding.txtOriginAddress.text = StringHelper.toPersianDigits(serviceModel.originAddress)
        binding.txtFirstDestAddress.text =
            StringHelper.toPersianDigits(serviceModel.destinationDesc)
        binding.txtPrice.text = StringHelper.toPersianDigits(
            StringHelper.setComma(serviceModel.servicePrice).toString() + " تومان"
        )

        if (serviceModel.description.trim() == "") {
            binding.llDescription.visibility = View.GONE
        } else {
            binding.txtDescription.text =
                StringHelper.toPersianDigits(serviceModel.description)
        }

        val dataArray: Array<String> = serviceModel.destinationDesc.split("$").toTypedArray()
        for (i in dataArray.indices) {
            when (i) {
                0 -> {
                    binding.txtFirstDestAddress.text = StringHelper.toPersianDigits(dataArray[0])
                }
                1 -> {
                    binding.txtSecondDestAddress.text = StringHelper.toPersianDigits(dataArray[1])
                    binding.llSecondDest.visibility = View.VISIBLE
                }
                2 -> {
                    binding.txtThirdDestAddress.text = StringHelper.toPersianDigits(dataArray[2])
                    binding.llThirdDest.visibility = View.VISIBLE
                }
            }
        }

        binding.btnGetService.setOnClickListener {
            binding.vfAcceptService.displayedChild = 1
            AcceptService().accept(serviceModel.serviceID, object : Listener {
                override fun onSuccess() {
                    binding.vfAcceptService.displayedChild = 0
                    dismiss()

                }

                override fun onFailure() {
                    binding.vfAcceptService.displayedChild = 0
                    dismiss()
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