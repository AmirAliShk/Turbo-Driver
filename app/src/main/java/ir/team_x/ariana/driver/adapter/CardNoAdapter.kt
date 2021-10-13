package ir.team_x.ariana.delivery.adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import ir.team_x.ariana.delivery.room.CardNumber


class CardNo(val cardNo: String, val bankName: String) {
    override fun toString(): String {
        return "CardNoAdapter(cardNo='$cardNo', bankName='$bankName')"
    }
}

class CardNoAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    val models: List<CardNumber>
) : ArrayAdapter<CardNumber>(context, layoutResource, models) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.item_bank_card, parent, false)
        }
        val txtCardNumber = v?.findViewById<View>(R.id.txtCardNumber) as TextView
        val txtBankName = v.findViewById<View>(R.id.txtBankName) as TextView

        txtCardNumber.text = models[position].cardNo
        txtBankName.text = models[position].bankName

        return v
    }

    override fun getCount(): Int {
        return models.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }


}