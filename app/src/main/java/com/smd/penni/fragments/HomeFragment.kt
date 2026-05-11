package com.smd.penni.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.smd.penni.NavExtras
import com.smd.penni.R
import com.smd.penni.activities.LoginActivity
import com.smd.penni.adapters.BudgetAdapter
import com.smd.penni.adapters.CategoryAdapter
import com.smd.penni.adapters.TransactionAdapter
import com.smd.penni.data.DatabaseHelper
import com.smd.penni.databinding.FragmentHomeBinding
import com.smd.penni.models.BudgetItem
import com.smd.penni.models.CategoryItem
import com.smd.penni.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var dbHelper: DatabaseHelper

    private var allTransactions: List<Transaction> = emptyList()
    private var searchQuery: String = ""
    private var categoryFilter: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        
        val displayName = requireArguments().getString(NavExtras.ARG_DISPLAY_NAME) ?: "Guest"
        binding.toolbarTitle.text = getString(R.string.home_toolbar_format, displayName)

        setupStaticLists()
        setupTransactionList()
        setupFilters()
        
        // Manual Logout: Click the "Penni..." title to logout and test Login/Register pages
        binding.toolbarTitle.setOnClickListener {
             FirebaseAuth.getInstance().signOut()
             val intent = Intent(requireContext(), LoginActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)
             requireActivity().finish()
        }
        
        loadDatabaseData()
    }

    private fun setupStaticLists() {
        val budgetAdapter = BudgetAdapter()
        binding.budgetList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.budgetList.adapter = budgetAdapter
        budgetAdapter.submitList(sampleBudgets())

        categoryAdapter = CategoryAdapter()
        val grid = GridLayoutManager(requireContext(), 3)
        grid.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                categoryAdapter.currentList.getOrNull(position)?.spanSize ?: 1
        }
        binding.categoriesList.layoutManager = grid
        binding.categoriesList.adapter = categoryAdapter
        categoryAdapter.submitList(sampleCategories())
    }

    private fun setupTransactionList() {
        transactionAdapter = TransactionAdapter { transaction ->
            parentFragmentManager.commit {
                replace(R.id.main_nav_content, TransactionDetailFragment.newInstance(transaction))
                addToBackStack(null)
            }
        }
        binding.transactionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionsList.adapter = transactionAdapter
    }

    private fun setupFilters() {
        binding.searchTransactions.doAfterTextChanged { editable ->
            searchQuery = editable?.toString().orEmpty().trim().lowercase()
            applyFilters()
        }
    }

    private fun loadDatabaseData() {
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                val transactions = dbHelper.getAllTransactions()
                val balance = dbHelper.getTotalBalance()
                Pair(transactions, balance)
            }
            
            allTransactions = data.first
            binding.tvTotalBalance.text = "$" + String.format("%.2f", data.second)
            
            applyFilters()
            updateFilterSpinner()
        }
    }

    private fun updateFilterSpinner() {
        val filterLabels = buildList {
            add(getString(R.string.category_all))
            addAll(allTransactions.map { it.category }.distinct().sorted())
        }
        binding.categoryFilter.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            filterLabels
        )
        binding.categoryFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                categoryFilter = if (pos == 0) null else filterLabels[pos]
                applyFilters()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun applyFilters() {
        val q = searchQuery
        val filtered = allTransactions.filter { tx ->
            val categoryOk = categoryFilter == null || tx.category == categoryFilter
            if (!categoryOk) return@filter false
            if (q.isEmpty()) return@filter true
            tx.title.lowercase().contains(q) ||
                tx.category.lowercase().contains(q) ||
                tx.dateLabel.lowercase().contains(q)
        }
        transactionAdapter.submitList(filtered)
    }

    override fun onResume() {
        super.onResume()
        loadDatabaseData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sampleBudgets(): List<BudgetItem> = listOf(
        BudgetItem("b1", "Weekly", "$400", 0.72f),
        BudgetItem("b2", "Dining", "$120", 0.45f)
    )

    private fun sampleCategories(): List<CategoryItem> = listOf(
        CategoryItem("c1", "Groceries", 1),
        CategoryItem("c2", "Transport", 1),
        CategoryItem("c3", "Shopping", 1)
    )

    companion object {
        fun newInstance(displayName: String): HomeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(NavExtras.ARG_DISPLAY_NAME, displayName)
            }
        }
    }
}
