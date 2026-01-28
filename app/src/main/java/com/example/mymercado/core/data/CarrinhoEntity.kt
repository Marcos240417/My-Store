package com.example.mymercado.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrinho")
data class CarrinhoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val produtoId: Int,
    val usuarioEmail: String,
    val titulo: String,
    val precoNoMomento: Double,
    val urlImagem: String,
    val quantidade: Int,
    val vendedorNome: String // Adicionado para exibir a foto no carrinho
)
