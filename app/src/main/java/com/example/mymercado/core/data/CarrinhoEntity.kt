package com.example.mymercado.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrinho")
data class CarrinhoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val produtoId: Int,
    val usuarioEmail: String,
    val quantidade: Int,
    val precoNoMomento: Double,
    val titulo: String, // Adicionado para facilitar exibição
    val urlImagem: String // Adicionado para exibir a foto no carrinho
)
