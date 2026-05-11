package com.smd.penni.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.smd.penni.adapters.TransactionAdapter
import com.smd.penni.data.DatabaseHelper
import com.smd.penni.data.FirestoreHelper
import com.smd.penni.databinding.FragmentDataBinding
import com.smd.penni.models.Transaction
import com.smd.penni.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataFragment : Fragment() {

    private var _binding: FragmentDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var firestoreHelper: FirestoreHelper
    private lateinit var adapter: TransactionAdapter
    
    private var editingTransactionId: Int? = null
    private var categoryMap: Map<Int, String> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())
        firestoreHelper = FirestoreHelper()
        
        setupRecyclerView()
        setupListeners()
        loadCategories()
        
        fetchBitcoinPriceStatus()
        refreshList()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            startEditing(transaction)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }

        binding.btnCancel.setOnClickListener {
            resetForm()
        }

        binding.etSearch.doAfterTextChanged { text ->
            searchInDb(text?.toString().orEmpty())
        }

        binding.btnDeposit.setOnClickListener {
            val amountStr = binding.etDepositAmount.text.toString()
            if (amountStr.isNotBlank()) {
                val amount = amountStr.toDoubleOrNull() ?: 0.0
                performDeposit(amount)
            } else {
                Toast.makeText(context, "Enter amount to deposit", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnReset.setOnClickListener {
            showResetConfirmation()
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            categoryMap = withContext(Dispatchers.IO) { dbHelper.getCategories() }
            val categories = categoryMap.values.toList()
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = spinnerAdapter
        }
    }

    private fun fetchBitcoinPriceStatus() {
        lifecycleScope.launch {
            try {
                binding.tvStatus.text = "F1 Syncing Live BTC Price..."
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getBitcoinPrice()
                }
                val price = response["bitcoin"]?.get("usd")
                binding.tvStatus.text = "Live Market Status: BTC is at $$price USD"
            } catch (e: Exception) {
                binding.tvStatus.text = "API F1 Error: Check Connection"
            }
        }
    }

    private fun performDeposit(amount: Double) {
        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dbHelper.insertTransaction("Deposit", amount, date, 1)
                // F2: Sync to Firestore
                if (userId != null) {
                    val data = mapOf(
                        "title" to "Deposit",
                        "amount" to amount,
                        "date" to date,
                        "category" to "Salary"
                    )
                    firestoreHelper.addTransaction(userId, data)
                }
                Unit // Fix compilation error
            }
            Toast.makeText(context, "$$amount Deposited & Synced", Toast.LENGTH_SHORT).show()
            binding.etDepositAmount.text = null
            refreshList()
        }
    }

    private fun saveTransaction() {
        val title = binding.etTitle.text.toString()
        val amountStr = binding.etAmount.text.toString()
        val categoryName = binding.spinnerCategory.selectedItem?.toString()

        if (title.isBlank() || amountStr.isBlank() || categoryName == null) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        var amount = amountStr.toDoubleOrNull() ?: 0.0
        val catId = categoryMap.filterValues { it == categoryName }.keys.firstOrNull() ?: 1
        
        if (categoryName != "Salary") {
            amount = -java.lang.Math.abs(amount)
        } else {
            amount = java.lang.Math.abs(amount)
        }

        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (editingTransactionId == null) {
                    dbHelper.insertTransaction(title, amount, date, catId)
                    // F2: Save to Firestore
                    if (userId != null) {
                        val data = mapOf(
                            "title" to title,
                            "amount" to amount,
                            "date" to date,
                            "category" to categoryName
                        )
                        firestoreHelper.addTransaction(userId, data)
                    }
                } else {
                    dbHelper.updateTransaction(editingTransactionId!!, title, amount, catId)
                }
                Unit // Fix compilation error
            }
            Toast.makeText(context, "Transaction Saved & Synced", Toast.LENGTH_SHORT).show()
            resetForm()
            refreshList()
        }
    }

    private fun startEditing(tx: Transaction) {
        editingTransactionId = tx.id.toInt()
        binding.formTitle.text = "Edit Transaction"
        binding.etTitle.setText(tx.title)
        val cleanAmount = tx.amountLabel.replace("+", "").replace("-", "").replace("$", "")
        binding.etAmount.setText(cleanAmount)
        
        val catIndex = (binding.spinnerCategory.adapter as ArrayAdapter<String>).getPosition(tx.category)
        binding.spinnerCategory.setSelection(catIndex)
        
        binding.btnSave.text = "Update"
        binding.btnCancel.text = "Delete"
        binding.btnCancel.setOnClickListener {
            deleteTransaction(editingTransactionId!!)
        }
    }

    private fun deleteTransaction(id: Int) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dbHelper.deleteTransaction(id)
            }
            Toast.makeText(context, "Transaction Deleted Locally", Toast.LENGTH_SHORT).show()
            resetForm()
            refreshList()
        }
    }

    private fun resetForm() {
        editingTransactionId = null
        binding.formTitle.text = "New Transaction"
        binding.etTitle.text = null
        binding.etAmount.text = null
        binding.btnSave.text = "Save"
        binding.btnCancel.text = "Cancel"
        binding.btnCancel.setOnClickListener { resetForm() }
    }

    private fun showResetConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset Database")
            .setMessage("This will reset all data and the $10,000 balance. Continue?")
            .setPositiveButton("Reset") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        dbHelper.onUpgrade(dbHelper.writableDatabase, 1, 2)
                    }
                    refreshList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun searchInDb(query: String) {
        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                if (query.isBlank()) dbHelper.getAllTransactions()
                else dbHelper.searchTransactions(query)
            }
            adapter.submitList(results)
        }
    }

    private fun refreshList() {
        lifecycleScope.launch {
            val transactions = withContext(Dispatchers.IO) {
                dbHelper.getAllTransactions()
            }
            adapter.submitList(transactions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
