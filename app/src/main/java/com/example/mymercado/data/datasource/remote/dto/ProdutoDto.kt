package com.example.mymercado.data.datasource.remote.dto

import com.google.gson.annotations.SerializedName

data class ProdutoDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    @SerializedName("image") val imageUrl: String
)