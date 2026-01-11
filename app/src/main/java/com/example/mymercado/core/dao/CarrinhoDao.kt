package com.example.mymercado.core.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymercado.core.data.CarrinhoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarrinhoDao {
    @Query("SELECT * FROM carrinho WHERE usuarioEmail = :email")
    fun obterCarrinhoPorUsuario(email: String): Flow<List<CarrinhoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirOuAtualizar(item: CarrinhoEntity)

    @Delete
    suspend fun deletarItem(item: CarrinhoEntity)

    @Query("DELETE FROM carrinho WHERE usuarioEmail = :email")
    suspend fun esvaziarCarrinho(email: String)
}