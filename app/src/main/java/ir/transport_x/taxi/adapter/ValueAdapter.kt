package ir.transport_x.taxi.adapter

import android.annotation.SuppressLint
import java.util.*
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.transport_x.taxi.R
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.ItemValueBinding
import ir.transport_x.taxi.model.ValueModel
import ir.transport_x.taxi.utils.StringHelper
import ir.transport_x.taxi.utils.TypeFaceUtil

class ValueAdapter(list: ArrayList<ValueModel>, var selected: SelectedValue) :
    RecyclerView.Adapter<ValueAdapter.ViewHolder>() {
    private val valueModel = list
    private var lastPositionPos = -1

    interface SelectedValue {
        fun getSelected(s: String)
    }

    class ViewHolder(val binding: ItemValueBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemValueBinding.inflate(
            LayoutInflater.from(MyApplication.currentActivity),
            parent,
            false
        )
        TypeFaceUtil.overrideFont(binding.root)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = valueModel[position]

        holder.binding.txtValue.text = StringHelper.setComma(model.value)

        holder.binding.txtValue.isSelected = model.isSelected
        holder.binding.imgCheck.isSelected = model.isSelected

        holder.binding.txtValue.setOnClickListener {
            if (lastPositionPos == -1)
                lastPositionPos = position
            else {
                valueModel[lastPositionPos].isSelected = false
                notifyItemChanged(lastPositionPos)
            }
            holder.binding.txtValue.isSelected = !holder.binding.txtValue.isSelected
            holder.binding.imgCheck.isSelected = !holder.binding.imgCheck.isSelected
            lastPositionPos = position
            if (holder.binding.txtValue.isSelected) {
                selected.getSelected(holder.binding.txtValue.text.toString())
            } else {
                selected.getSelected("0")
                lastPositionPos = -1
            }
        }
    }

    override fun getItemCount() = valueModel.size
}