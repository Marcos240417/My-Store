package com.example.mymercado.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descricao: String,
    val preco: Double,
    val precoPromocional: Double? = null,
    val urlImagem: String,
    val categoria: String,
    val vendedorId: Int,
    val estoque: Int = 50,
    val isFavorito: Boolean = false, // Campo adicionado para corrigir o erro do KSP
    val nota: Double = 4.8,
    val totalAvaliacoes: Int = 120,
    val freteGratis: Boolean = true
)