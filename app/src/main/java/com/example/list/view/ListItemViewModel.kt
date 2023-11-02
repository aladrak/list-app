package com.example.list.view

import kotlinx.serialization.Serializable

@Serializable
data class ListItemViewModel (
    var id: Int,
    val title: String,
    val description: String,
)