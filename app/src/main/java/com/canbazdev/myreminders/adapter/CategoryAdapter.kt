package com.canbazdev.myreminders.adapter

// created by Hamza Canbaz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.databinding.ItemThemeBinding
import com.canbazdev.myreminders.model.Category


class CategoryAdapter(
    private val listener: OnCategoryClickedListener
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var categoriesList = ArrayList<Category>()

    fun setCategoriesListList(list: List<Category>) {
        categoriesList.clear()
        categoriesList.addAll(list)
//        println(list.size)
        notifyDataSetChanged()

    }

    inner class CategoryViewHolder(private val binding: ItemThemeBinding) :
        CategoryAdapter.BaseViewHolder<Category>(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: Category) {
            binding.tvCategoryBackground.text = item.title
            binding.tvCategoryBackground.background.setTint(item.color)

        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClicked(position, categoriesList[position])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemThemeBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding)
    }

//    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
//    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Category)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoriesList[position])

    }

    interface OnCategoryClickedListener {
        fun onItemClicked(position: Int, category: Category)
    }


}

