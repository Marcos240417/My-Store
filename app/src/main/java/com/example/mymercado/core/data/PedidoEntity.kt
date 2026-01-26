package com.example.mymercado.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioEmail: String,
    val dataPedido: Long = System.currentTimeMillis(),
    val total: Double,
    val formaPagamento: String,
    val itensResumo: String
)