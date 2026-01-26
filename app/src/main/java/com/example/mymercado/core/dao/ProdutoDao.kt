package com.example.mymercado.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymercado.core.data.ProdutoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos ORDER BY titulo ASC")
    fun listarTodos(): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos WHERE categoria = :categoria")
    fun buscarPorCategoria(categoria: String): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos WHERE produtoId = :id")
    suspend fun buscarPorId(id: Int): ProdutoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(produto: ProdutoEntity)

    // --- NOVOS MÉTODOS DE FAVORITOS ---
    @Query("SELECT * FROM produtos WHERE isFavorito = 1")
    fun listarFavoritos(): Flow<List<ProdutoEntity>>

    @Query("UPDATE produtos SET isFavorito = :favorito WHERE produtoId = :id")
    suspend fun atualizarFavorito(id: Int, favorito: Boolean)
}