package ir.transport_x.taxi.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.transport_x.taxi.app.MyApplication
import ir.transport_x.taxi.databinding.FragmentSuggestStationBinding
import ir.transport_x.taxi.utils.TypeFaceUtil

class SuggestionStationFragment : Fragment() {
    private lateinit var binding: FragmentSuggestStationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSuggestStationBinding.inflate(inflater, container, false)
        TypeFaceUtil.overrideFont(binding.root)
        TypeFaceUtil.overrideFont(binding.txtTitle, MyApplication.iranSansMediumTF)

        binding.llBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }


        return binding.root
    }

}