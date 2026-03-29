package com.smd.penni.models

data class BudgetItem(
    val id: String,
    val label: String,
    val amountLabel: String,
    /** 0f–1f portion of the bar filled */
    val progressFraction: Float
)
