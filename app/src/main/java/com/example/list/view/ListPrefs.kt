package com.example.list.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ListPrefs (context: Context) {
    private val prefs = context.getSharedPreferences(LIST_PREFS, MODE_PRIVATE)

    fun firstListToPrefs(list: List<ListItemViewModel>) {
        val serializableSet = list.map {
            Json.encodeToString(it)
        }.toSet()
        prefs
            .edit()
            .putStringSet(FIRST_LIST_KEY, serializableSet)
            .apply()
    }

    fun secondListToPrefs(list: List<ListItemViewModel>) {
        val serializableSet = list.map {
            Json.encodeToString(it)
        }.toSet()
        prefs
            .edit()
            .putStringSet(SECOND_LIST_KEY, serializableSet)
            .apply()
    }

    fun getFirstList(): List<ListItemViewModel> = prefs.getStringSet(FIRST_LIST_KEY, setOf())!!
        .map {
            Json.decodeFromString<ListItemViewModel>(it)
        }.toList().sortedBy { it.id }

    fun getSecondList(): List<ListItemViewModel> = prefs.getStringSet(SECOND_LIST_KEY, setOf())!!
        .map {
            Json.decodeFromString<ListItemViewModel>(it)
        }.toList().sortedBy { it.id }

    companion object {
        private const val LIST_PREFS = "LIST_PREFS"
        private const val FIRST_LIST_KEY = "FIRST_LIST_KEY"
        private const val SECOND_LIST_KEY = "SECOND_LIST_KEY"
    }
}