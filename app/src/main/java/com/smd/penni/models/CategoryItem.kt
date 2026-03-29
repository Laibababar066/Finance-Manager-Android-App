package com.smd.penni.models

data class CategoryItem(
    val id: String,
    val title: String,
    /** span size for grid (1 or 2) */
    val spanSize: Int = 1
)
