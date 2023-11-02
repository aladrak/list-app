package com.example.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.list.R
import com.example.list.databinding.AddItemFragmentBinding
import com.example.list.view.FirstFragment.Companion.ADD_ITEM_POSITION_KEY
import com.example.list.view.FirstFragment.Companion.ADD_ITEM_REQUEST_KEY
import com.example.list.view.FirstFragment.Companion.ADD_ITEM_TITLE_KEY
import com.example.list.view.FirstFragment.Companion.EDIT_ITEM_REQUEST_KEY
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AddItemFragment : Fragment() {

    private val itemPosition: Int?
        get() = arguments?.getInt(POSITION_KEY)

    private val itemTitle: String?
        get() = arguments?.getString(TITLE_KEY)

    private val itemDescription: String?
        get() = arguments?.getString(DESCRIPTION_KEY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = AddItemFragmentBinding.inflate(inflater, container, false).apply {
        title.setText(itemTitle ?: "")
        description.setText(itemDescription ?: "")

        floatbtnCreate.setOnClickListener {
            val item = ListItemViewModel(0, title.text.toString(), description.text.toString())
            val serializedItem = Json.encodeToString(item)
            if (itemPosition == null) {
                requireActivity().supportFragmentManager.setFragmentResult(
                    ADD_ITEM_REQUEST_KEY,
                    bundleOf(ADD_ITEM_TITLE_KEY to serializedItem)
                )
            } else {
                requireActivity().supportFragmentManager.setFragmentResult(
                    EDIT_ITEM_REQUEST_KEY,
                    bundleOf(
                        ADD_ITEM_TITLE_KEY to serializedItem,
                        ADD_ITEM_POSITION_KEY to itemPosition
                    )
                )
            }
            requireActivity().supportFragmentManager.popBackStack()
//            parentFragmentManager.popBackStack()
        }
    }.root

    companion object {

        const val POSITION_KEY = "POSITION_KEY"
        const val TITLE_KEY = "TITLE_KEY"
        const val DESCRIPTION_KEY = "DESCRIPTION_KEY"

        fun editItemInstance(position: Int, title: String, description: String): Fragment {
            val fragment = AddItemFragment()
            val bundle = bundleOf(POSITION_KEY to position, TITLE_KEY to title, DESCRIPTION_KEY to description)
            fragment.arguments = bundle
            return fragment
        }
    }
}