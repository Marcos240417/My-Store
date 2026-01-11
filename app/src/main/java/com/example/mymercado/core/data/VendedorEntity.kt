package com.example.mymercado.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendedores")
data class VendedorEntity(
    @PrimaryKey(autoGenerate = true) val vendedorId: Int = 0,
    val nomeLoja: String,
    val cnpj: String,
    val avaliacaoLoja: Double = 5.0,
    val logoUrl: String? = null
)