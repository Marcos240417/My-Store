package com.example.mymercado.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymercado.core.data.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE email = :email")
    fun obterUsuarioPorEmail(email: String): Flow<UsuarioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarOuAtualizarUsuario(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios WHERE email = :email")
    suspend fun deletarUsuario(email: String)
}