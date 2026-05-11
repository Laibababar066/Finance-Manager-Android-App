package com.smd.penni.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.smd.penni.NavExtras
import com.smd.penni.R
import com.smd.penni.activities.LoginActivity
import com.smd.penni.adapters.MarketAdapter
import com.smd.penni.databinding.FragmentProfileBinding
import com.smd.penni.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var marketAdapter: MarketAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = requireArguments().getString(NavExtras.ARG_DISPLAY_NAME) ?: "Guest"
        binding.profileWelcome.text = "Market Dashboard: $name"

        setupRecyclerView()
        fetchLiveMarketData()

        // Implement Logout Button
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun setupRecyclerView() {
        marketAdapter = MarketAdapter(emptyList())
        binding.rvMarketTrends.layoutManager = LinearLayoutManager(requireContext())
        marketAdapter.updateData(emptyList()) // Ensure clean start
        binding.rvMarketTrends.adapter = marketAdapter
    }

    private fun fetchLiveMarketData() {
        lifecycleScope.launch {
            try {
                val coins = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getMarketTrends()
                }
                if (isAdded) {
                    marketAdapter.updateData(coins)
                }
            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(context, "Market data offline", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(displayName: String): ProfileFragment = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(NavExtras.ARG_DISPLAY_NAME, displayName)
            }
        }
    }
}
