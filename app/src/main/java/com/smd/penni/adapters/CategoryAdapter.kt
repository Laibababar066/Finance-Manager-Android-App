package com.smd.penni.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smd.penni.databinding.ItemCategoryCellBinding
import com.smd.penni.models.CategoryItem

class CategoryAdapter : ListAdapter<CategoryItem, CategoryAdapter.CategoryViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryCellBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryItem) {
            binding.categoryTitle.text = item.title
        }
    }

    private object Diff : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean =
            oldItem == newItem
    }
}
