package ir.team_x.ariana.driver.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.team_x.ariana.driver.databinding.FragmentFreeLoadsBinding

class FreeLoadsFragment : Fragment() {
 private lateinit var binding : FragmentFreeLoadsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentFreeLoadsBinding.inflate(inflater, container, false)



        return binding.root
    }

    companion object {

    }
}