package com.canbazdev.myreminders.adapter

// created by Hamza Canbaz

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.model.Category
import com.canbazdev.myreminders.databinding.ItemThemeBinding


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
            val backgroundOff: Drawable = binding.flCategoryBackground.background
            backgroundOff.setTint(item.color)
            binding.flCategoryBackground.background = backgroundOff


            if (item.isSelected) {
                val newBackground: Drawable = ResourcesCompat.getDrawable(
                    itemView.resources,
                    R.drawable.bg_select_category_with_border,
                    null
                )!!
                binding.tvCategoryBackground.background = newBackground
            } else {
                binding.tvCategoryBackground.background = null
            }

        }


        override fun onClick(view: View?) {
            if (!categoriesList[layoutPosition].isSelected) {
                deleteAllItemBackgrounds()
                categoriesList[layoutPosition].isSelected = true
                notifyDataSetChanged()
            } else {
                categoriesList[layoutPosition].isSelected = false
                notifyDataSetChanged()
            }

            if (layoutPosition != RecyclerView.NO_POSITION) {
                listener.onItemClicked(layoutPosition, categoriesList[layoutPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemThemeBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding)
    }


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

    private fun deleteAllItemBackgrounds() {
        categoriesList.forEach { category ->
            category.isSelected = false
        }
    }


}

