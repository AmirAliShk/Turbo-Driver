package ir.team_x.ariana.driver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.team_x.ariana.driver.R
import ir.team_x.ariana.driver.app.MyApplication
import ir.team_x.ariana.driver.databinding.ItemFreeLoadsBinding
import ir.team_x.ariana.driver.fragment.CurrentServiceFragment
import ir.team_x.ariana.driver.fragment.ServiceDetailsFragment
import ir.team_x.ariana.driver.model.WaitingLoadsModel
import ir.team_x.ariana.driver.utils.FragmentHelper
import ir.team_x.ariana.driver.utils.SoundHelper
import ir.team_x.ariana.driver.webServices.AcceptService
import ir.team_x.ariana.operator.utils.TypeFaceUtil

class WaitingLoadsAdapter(list: ArrayList<WaitingLoadsModel>) :
    RecyclerView.Adapter<WaitingLoadsAdapter.ViewHolder>() {

    private val models = list

    class ViewHolder(val binding: ItemFreeLoadsBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFreeLoadsBinding.inflate(
            LayoutInflater.from(MyApplication.context),
            parent,
            false
        )
        TypeFaceUtil.overrideFont(binding.root)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtCargoType.text=model.cargoId.toString()
        holder.binding.txtRange.text="station name"
        holder.binding.btnAccept.setOnClickListener {
            holder.binding.vfAccept.displayedChild = 1
            AcceptService().accept(model.id.toString(), object : AcceptService.Listener {
                override fun onSuccess() {
                    holder.binding.vfAccept.displayedChild = 0
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
                    holder.binding.vfAccept.displayedChild = 0
                    //TODO print the error
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

}