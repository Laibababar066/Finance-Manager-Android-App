package com.smd.penni.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.smd.penni.NavExtras
import com.smd.penni.R
import com.smd.penni.databinding.FragmentMainNavBinding

class MainNavFragment : Fragment() {

    private var _binding: FragmentMainNavBinding? = null
    private val binding get() = _binding!!

    private enum class Tab { HOME, DATA, STATS, PROFILE }

    private var currentTab: Tab = Tab.HOME

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainNavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            currentTab = Tab.entries[savedInstanceState.getInt(STATE_TAB, Tab.HOME.ordinal)]
            updateTabUi(currentTab)
        }

        binding.tabHome.setOnClickListener { selectTab(Tab.HOME) }
        binding.tabData.setOnClickListener { selectTab(Tab.DATA) }
        binding.tabStats.setOnClickListener { selectTab(Tab.STATS) }
        binding.tabProfile.setOnClickListener { selectTab(Tab.PROFILE) }

        if (savedInstanceState == null) {
            selectTab(Tab.HOME)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_TAB, currentTab.ordinal)
    }

    private fun displayName(): String =
        requireArguments().getString(NavExtras.ARG_DISPLAY_NAME) ?: "Guest"

    private fun selectTab(tab: Tab) {
        while (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStackImmediate()
        }
        currentTab = tab
        val name = displayName()
        val fragment = when (tab) {
            Tab.HOME -> HomeFragment.newInstance(name)
            Tab.DATA -> DataFragment()
            Tab.STATS -> StatsFragment.newInstance(name)
            Tab.PROFILE -> ProfileFragment.newInstance(name)
        }
        childFragmentManager.commit {
            replace(R.id.main_nav_content, fragment)
        }
        updateTabUi(tab)
    }

    private fun updateTabUi(selected: Tab) {
        val ctx = requireContext()
        val active = ContextCompat.getColor(ctx, R.color.penni_green)
        val muted = ContextCompat.getColor(ctx, R.color.penni_muted)
        binding.labelHome.setTextColor(if (selected == Tab.HOME) active else muted)
        binding.labelData.setTextColor(if (selected == Tab.DATA) active else muted)
        binding.labelStats.setTextColor(if (selected == Tab.STATS) active else muted)
        binding.labelProfile.setTextColor(if (selected == Tab.PROFILE) active else muted)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val STATE_TAB = "main_nav_tab"

        fun newInstance(displayName: String): MainNavFragment = MainNavFragment().apply {
            arguments = Bundle().apply {
                putString(NavExtras.ARG_DISPLAY_NAME, displayName)
            }
        }
    }
}
