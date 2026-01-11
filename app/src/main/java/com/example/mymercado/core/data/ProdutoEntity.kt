package com.example.mymercado.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey(autoGenerate = true) val produtoId: Int = 0,
    val titulo: String,
    val descricao: String,
    val preco: Double,
    val categoria: String,
    val urlImagem: String,
    val estoque: Int,
    val vendedorDonoId: Int // Relaciona com o VendedorEntity
)
