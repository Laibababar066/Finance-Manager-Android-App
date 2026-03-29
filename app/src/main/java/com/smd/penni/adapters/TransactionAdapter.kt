package com.smd.penni.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smd.penni.databinding.ItemTransactionRowBinding
import com.smd.penni.models.Transaction

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTransactionRowBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(
        private val binding: ItemTransactionRowBinding,
        private val onItemClick: (Transaction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Transaction) {
            binding.transactionTitle.text = item.title
            binding.transactionDate.text = item.dateLabel
            binding.transactionCategory.text = item.category
            binding.transactionAmount.text = item.amountLabel
            binding.transactionStatus.text = item.status
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem == newItem
    }
}
