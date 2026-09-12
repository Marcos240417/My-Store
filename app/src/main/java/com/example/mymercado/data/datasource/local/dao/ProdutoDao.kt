package com.example.mymercado.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymercado.data.datasource.local.entity.ProdutoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos ORDER BY titulo ASC")
    fun listarTodos(): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos WHERE categoria = :categoria")
    fun buscarPorCategoria(categoria: String): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produtos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): ProdutoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(produto: ProdutoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(produtos: List<ProdutoEntity>)

    // --- FAVORITOS ---
    @Query("SELECT * FROM produtos WHERE isFavorito = 1")
    fun listarFavoritos(): Flow<List<ProdutoEntity>>

    @Query("UPDATE produtos SET isFavorito = :favorito WHERE id = :id")
    suspend fun atualizarFavorito(id: Int, favorito: Boolean)

    // --- CONTROLE DE ESTOQUE ---
    @Query("UPDATE produtos SET estoque = CASE WHEN estoque >= :qtd THEN estoque - :qtd ELSE 0 END WHERE id = :produtoId")
    suspend fun diminuirEstoque(produtoId: Int, qtd: Int)

    @Query("SELECT estoque FROM produtos WHERE id = :produtoId")
    suspend fun obterSaldoEstoque(produtoId: Int): Int?
}