package com.example.mymercado.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioEmail: String,
    val dataPedido: Long = System.currentTimeMillis(),
    val dataEntregaEstimada: Long = System.currentTimeMillis() + (15L * 24 * 60 * 60 * 1000),
    val total: Double,
    val formaPagamento: String,
    val itensResumo: String,
    val quantidadeTotal: Int,
    val urlImagem: String,
    val codigoRastreio: String = "BR${(100000..999999).random()}MY",
    val status: String = "EM_PREPARAÇÃO"
)