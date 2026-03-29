package com.smd.penni.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smd.penni.databinding.BudgetCardBinding
import com.smd.penni.models.BudgetItem
import kotlin.math.roundToInt

class BudgetAdapter : ListAdapter<BudgetItem, BudgetAdapter.BudgetViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = BudgetCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BudgetViewHolder(
        private val binding: BudgetCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetItem) {
            binding.budgetLabel.text = item.label
            binding.budgetAmount.text = item.amountLabel
            val fraction = item.progressFraction.coerceIn(0f, 1f)
            binding.budgetTrack.post {
                val trackW = binding.budgetTrack.width
                val w = (trackW * fraction).roundToInt().coerceAtLeast(4)
                val lp = binding.budgetProgressFill.layoutParams
                lp.width = w
                binding.budgetProgressFill.layoutParams = lp
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<BudgetItem>() {
        override fun areItemsTheSame(oldItem: BudgetItem, newItem: BudgetItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BudgetItem, newItem: BudgetItem): Boolean =
            oldItem == newItem
    }
}
