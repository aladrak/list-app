package com.example.list.view;

import android.os.Bundle
import android.view.LayoutInflater;
import android.view.View
import android.view.ViewGroup;
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.list.R
import com.example.list.databinding.SecondFragmentBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SecondFragment (): Fragment() {

    private lateinit var prefs: ListPrefs

    private val adapter = SecondListAdapter(listOf()) {position, title, description ->
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, AddItemFragment.editItemInstance(position, title, description))
            .addToBackStack(AddItemFragment::class.java.canonicalName)
            .commit()
    }

    /* Детектор свайпа */
    private var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            if (swipeDir == ItemTouchHelper.LEFT) {
                Toast.makeText(
                    this@SecondFragment.requireContext(),
                    "Return to current.",
                    Toast.LENGTH_SHORT
                ).show()
                val position = viewHolder.adapterPosition
                // Перемещение в первый список
                val serializedItem = Json.encodeToString(adapter.getItem(position))
                setFragmentResult(
                    FIR_ADD_ITEM_REQUEST_KEY,
                    bundleOf(FIR_ADD_ITEM_TITLE_KEY to serializedItem)
                )
                // Удаление из второго списка
                adapter.remove(position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = ListPrefs(requireContext())
        adapter.setDataSet(prefs.getSecondList())
        // Сохранение добавления из первого списка
        setFragmentResultListener(SEC_ADD_ITEM_REQUEST_KEY) { _, bundle ->
            bundle.getString(SEC_ADD_ITEM_TITLE_KEY)?.let {
                val newItem = toListItemViewModel(it)
                adapter.add(newItem)
            }
        }
//        // Сохранение редактирования
        requireActivity()
            .supportFragmentManager.
            setFragmentResultListener(SEC_EDIT_ITEM_REQUEST_KEY, this) { _, bundle ->
            val position = bundle.getInt(SEC_ADD_ITEM_POSITION_KEY)
            val editItem = toListItemViewModel(bundle.getString(SEC_ADD_ITEM_TITLE_KEY, ""))
            adapter.edit(position, editItem)
        }
    }

    override fun onCreateView(
        inflater:LayoutInflater,
        container:ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SecondFragmentBinding.inflate(inflater, container, false).apply {
            secondList.layoutManager = LinearLayoutManager(requireContext())
            secondList.adapter = adapter
            secondList.addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                    setDrawable(ResourcesCompat.getDrawable(resources,
                        R.drawable.item_separator, requireContext().theme)!!)
                }
            )
            ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(secondList)
        }
        return binding.root
    }

    // Сохранение списка в prefs
    override fun onStop() {
        super.onStop()
        prefs.secondListToPrefs(adapter.getList())
        // Жизненный цикл фрагмента
    }

    companion object {
        // Константы для отправки элеметов в первый список
        const val FIR_ADD_ITEM_REQUEST_KEY = "FIR_ADD_ITEM_REQUEST_KEY"
        const val FIR_ADD_ITEM_TITLE_KEY = "FIR_ADD_ITEM_TITLE_KEY"

        // Константы для отправки элементов во второй список
        const val SEC_ADD_ITEM_REQUEST_KEY = "SEC_ADD_ITEM_REQUEST_KEY"
        const val SEC_ADD_ITEM_TITLE_KEY = "SEC_ADD_ITEM_TITLE_KEY"

        const val SEC_EDIT_ITEM_REQUEST_KEY = "SEC_EDIT_ITEM_REQUEST_KEY"

        const val ADD_ITEM_TITLE_KEY = "ADD_ITEM_TITLE_KEY"
        const val SEC_ADD_ITEM_POSITION_KEY = "SEC_ADD_ITEM_EXTRA_KEY"

        fun toListItemViewModel(str: String) : ListItemViewModel = Json.decodeFromString<ListItemViewModel>(str)
    }
}
