package com.smd.penni.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smd.penni.NavExtras
import com.smd.penni.databinding.FragmentTransactionDetailBinding
import com.smd.penni.models.Transaction

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private fun requireTransaction(): Transaction {
        val args = requireArguments()
        return if (Build.VERSION.SDK_INT >= 33) {
            args.getParcelable(NavExtras.ARG_TRANSACTION, Transaction::class.java)
        } else {
            @Suppress("DEPRECATION")
            args.getParcelable(NavExtras.ARG_TRANSACTION)
        } ?: throw IllegalStateException("Missing transaction argument")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tx = requireTransaction()
        binding.detailTitle.text = tx.title
        binding.detailAmount.text = tx.amountLabel
        binding.detailCategory.text = tx.category
        binding.detailDate.text = tx.dateLabel
        binding.detailStatus.text = tx.status
        binding.detailNote.text = tx.note

        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(transaction: Transaction): TransactionDetailFragment =
            TransactionDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(NavExtras.ARG_TRANSACTION, transaction)
                }
            }
    }
}
