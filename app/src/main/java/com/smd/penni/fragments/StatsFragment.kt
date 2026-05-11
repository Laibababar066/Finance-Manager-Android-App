package com.smd.penni.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smd.penni.NavExtras
import com.smd.penni.R
import com.smd.penni.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = requireArguments().getString(NavExtras.ARG_DISPLAY_NAME) ?: "Guest"
        binding.statsSubtitle.text = getString(R.string.stats_subtitle_format, name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(displayName: String): StatsFragment = StatsFragment().apply {
            arguments = Bundle().apply {
                putString(NavExtras.ARG_DISPLAY_NAME, displayName)
            }
        }
    }
}
