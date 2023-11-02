package com.example.list.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.list.R
import com.example.list.databinding.ListItemBinding

class ListAdapter (
        initialDataSet: List<ListItemViewModel>,
        private val onButtonClick : (position: Int, title: String, description: String) -> Unit
    ): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private val dataSet = initialDataSet.toMutableList()

    inner class ListViewHolder (private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val textView: TextView = binding.itemText
        val moreButton: AppCompatButton = binding.itemMore

        var viewModel: ListItemViewModel? = null
            set(value) {
                binding.viewModel = value
                field = value
            }

        init {
            moreButton.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.context_menu)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                           R.id.action_edit -> {
                               onButtonClick(adapterPosition,
                               textView.text.toString(),
                               dataSet[adapterPosition].description
                               )
                           }
                           R.id.action_del -> {
                               Toast.makeText(
                                   moreButton.context,
                                   "Item deleted.",
                                   Toast.LENGTH_SHORT
                               ).show()
                               val position = adapterPosition
                               remove(position)
                           }
                        }
                        true
                    }
                }.show()
            }
        }
    }

    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int) : ListViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context),
                parent,
                false
        )
        return ListViewHolder(binding)
    }

    override fun getItemCount () : Int = dataSet.size

    override fun onBindViewHolder (holder: ListViewHolder, position: Int) {
        holder.viewModel = dataSet[position]
    }

    fun getList(): List<ListItemViewModel> = dataSet

    @SuppressLint("NotifyDataSetChanged")
    fun setDataSet(list: List<ListItemViewModel>) {
        dataSet.clear()
        dataSet.addAll(list)
        notifyDataSetChanged()
    }

    fun add(item: ListItemViewModel) {
        item.id = getLastItemId() + 1
        dataSet.add(item)
        notifyItemInserted(dataSet.lastIndex)
    }

    fun remove(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun edit(position: Int, newTitle: ListItemViewModel) {
        dataSet[position] = newTitle
        notifyItemChanged(position)
    }

    fun getItem(position: Int): ListItemViewModel {
        return dataSet[position]
    }

    private fun getLastItemId() : Int = if (dataSet.lastOrNull() == null) {
            -1
        } else {
            dataSet.last().id
        }
}