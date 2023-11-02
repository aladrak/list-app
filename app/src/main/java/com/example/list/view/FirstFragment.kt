package com.example.list.view;

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater;
import android.view.View
import android.view.ViewGroup;
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.savedstate.SavedStateRegistryOwner
import androidx.viewpager2.widget.ViewPager2
import com.example.list.R
import com.example.list.databinding.FirstFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FirstFragment (): Fragment(){

    private lateinit var prefs: ListPrefs

    private val adapter = ListAdapter(listOf()) {position, title, description ->
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, AddItemFragment.editItemInstance(position, title, description))
            .addToBackStack(AddItemFragment::class.java.canonicalName)
            .commit()
    }

    // Детектор свайпа
    private var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            if (swipeDir == ItemTouchHelper.RIGHT ) {
                Toast.makeText(
                    this@FirstFragment.requireContext(),
                    "It's done!",
                    Toast.LENGTH_SHORT
                ).show()
                val position = viewHolder.adapterPosition
                // перемещение во второй список
                val serializedItem = Json.encodeToString(adapter.getItem(position))
                setFragmentResult(
                    SEC_ADD_ITEM_REQUEST_KEY,
                    bundleOf(SEC_ADD_ITEM_TITLE_KEY to serializedItem)
                )
                // удаление из первого списка
                adapter.remove(position)
            }
//            else (swipeDir == ItemTouchHelper.LEFT) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = ListPrefs(requireContext())
        adapter.setDataSet(prefs.getFirstList())
        setFragmentResultListener(FIR_ADD_ITEM_REQUEST_KEY) { requestKey, bundle ->
            bundle.getString(FIR_ADD_ITEM_TITLE_KEY)?.let {
                val newItem = toListItemViewModel(it)
                adapter.add(newItem)
            }
        }
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(ADD_ITEM_REQUEST_KEY, this) { requestKey, bundle ->
            bundle.getString(ADD_ITEM_TITLE_KEY)?.let {
                val newItem = toListItemViewModel(it)
                adapter.add(newItem)
            }
        }
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(EDIT_ITEM_REQUEST_KEY, this) { requestKey, bundle ->
            val position = bundle.getInt(ADD_ITEM_POSITION_KEY)
            val editItem = toListItemViewModel(bundle.getString(ADD_ITEM_TITLE_KEY, ""))
            adapter.edit(position, editItem)
        }
    }

    override fun onCreateView(
            inflater:LayoutInflater,
            container:ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FirstFragmentBinding.inflate(inflater, container, false).apply {
            list.layoutManager = LinearLayoutManager(requireContext())
            list.adapter = adapter
            list.addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                    setDrawable(ResourcesCompat.getDrawable(resources,
                        R.drawable.item_separator, requireContext().theme)!!)
                }
            )
            ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(list)
            floatbtnAdd.setOnClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, AddItemFragment())
                    .addToBackStack(AddItemFragment::class.java.canonicalName)
                    .commit()
            }
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        prefs.firstListToPrefs(adapter.getList())
        // Жизненный цикл фрагмента
    }

    companion object {
        // Константы для отправки элеметов в первый список
        const val FIR_ADD_ITEM_REQUEST_KEY = "FIR_ADD_ITEM_REQUEST_KEY"
        const val FIR_ADD_ITEM_TITLE_KEY = "FIR_ADD_ITEM_TITLE_KEY"

        // Константы для отправки элементов во второй список
        const val SEC_ADD_ITEM_REQUEST_KEY = "SEC_ADD_ITEM_REQUEST_KEY"
        const val SEC_ADD_ITEM_TITLE_KEY = "SEC_ADD_ITEM_TITLE_KEY"

        //
        const val ADD_ITEM_REQUEST_KEY = "ADD_ITEM_REQUEST_KEY"
        const val EDIT_ITEM_REQUEST_KEY = "EDIT_ITEM_REQUEST_KEY"

        //
        const val ADD_ITEM_TITLE_KEY = "ADD_ITEM_TITLE_KEY"
        const val ADD_ITEM_POSITION_KEY = "ADD_ITEM_EXTRA_KEY"

        fun toListItemViewModel(str: String) : ListItemViewModel = Json.decodeFromString<ListItemViewModel>(str)
    }
}
