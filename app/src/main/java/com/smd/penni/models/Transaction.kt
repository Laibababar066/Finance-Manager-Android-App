package com.smd.penni.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val id: String,
    val title: String,
    val amountLabel: String,
    val dateLabel: String,
    val category: String,
    val status: String,
    val note: String
) : Parcelable
