package ir.transport_x.taxi.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.transport_x.taxi.adapter.CancelServiceAdapter
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.DialogReasonForCancelBinding
import ir.transport_x.taxi.model.ItemModel
import ir.transport_x.taxi.model.ServiceDataModel
import ir.transport_x.taxi.push.AvaCrashReporter
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtil
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class CancelServiceDialog(
    cancelServiceListener: CancelServiceListener
) {
    lateinit var dialog: Dialog
    lateinit var binding: DialogReasonForCancelBinding

    interface CancelServiceListener {
        fun onCanceled(isCancel: Boolean)
    }

    var cancelServiceListener: CancelServiceListener = cancelServiceListener

    /**
     * @param serviceModel
     * @param recordPosition if this param is -1 is mean from turbo else from 1880 mange service page
     * @param count          Service limit canceled
     * @param penaltyPrice   penalty price
     * @param rewardPrice    gift price
     */
    fun show(serviceModel: ServiceDataModel) {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogReasonForCancelBinding.inflate(LayoutInflater.from(dialog.context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window!!.attributes
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = wlp
        TypeFaceUtil.overrideFont(binding.root)

        val title = "دلایل لغو سفر را بیان نمایید."
        val list: ArrayList<ItemModel> = ArrayList<ItemModel>()

        val reasonArr = JSONArray(MyApplication.prefManager.cancelReason)

        for (i in 0 until reasonArr.length()) {
            val reasonObj: JSONObject = reasonArr.getJSONObject(i)
            val typeServiceModel = ItemModel()
            typeServiceModel.id = reasonObj.getInt("id")
            typeServiceModel.name = reasonObj.getString("name")
            list.add(typeServiceModel)
        }

        val availableServiceAdapter = CancelServiceAdapter(
            list,
            serviceModel,
            object : CancelServiceAdapter.CancelServiceListener {
                override fun onCanceled(isCancel: Boolean) {
                    cancelServiceListener.onCanceled(isCancel)
                    dismiss()
                }
            })

        availableServiceAdapter.notifyDataSetChanged()
        binding.reasonList.adapter = availableServiceAdapter

        binding.title.text = title
        TypeFaceUtil.overrideFont(binding.title, MyApplication.iranSansMediumTF)

        binding.cancel.setOnClickListener { dismiss() }
        binding.rlroot.setOnClickListener { dismiss() }
        dialog.show()
    }

    fun dismiss() {
        try {
            if (dialog != null) if (dialog.isShowing) dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG , dismiss")
        }
//        dialog = null
    }

    companion object {
        private val TAG = CancelServiceDialog::class.java.simpleName
    }
}