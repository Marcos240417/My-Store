package com.example.mymercado.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymercado.data.datasource.local.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarPedido(pedido: PedidoEntity)

    // Lista apenas os pedidos em andamento (que ainda não foram entregues)
    @Query("SELECT * FROM pedidos WHERE usuarioEmail = :email AND status != 'ENTREGUE' ORDER BY id DESC")
    fun listarPedidosAtivosPorUsuario(email: String): Flow<List<PedidoEntity>>

    // Busca direta por ID para manipulação e remoção de itens individuais
    @Query("SELECT * FROM pedidos WHERE id = :pedidoId LIMIT 1")
    suspend fun listarTodosPedidosPorUsuarioDirect(pedidoId: Int): PedidoEntity?

    @Query("UPDATE pedidos SET status = :novoStatus WHERE id = :pedidoId")
    suspend fun atualizarStatusPedido(pedidoId: Int, novoStatus: String)

    @Query("DELETE FROM pedidos WHERE id = :pedidoId")
    suspend fun deletarPedido(pedidoId: Int)
}