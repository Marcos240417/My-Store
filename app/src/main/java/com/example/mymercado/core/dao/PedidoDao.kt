package com.example.mymercado.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymercado.core.data.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarPedido(pedido: PedidoEntity)

    @Query("SELECT * FROM pedidos WHERE usuarioEmail = :email ORDER BY id DESC")
    fun listarPedidosPorUsuario(email: String): Flow<List<PedidoEntity>>
}