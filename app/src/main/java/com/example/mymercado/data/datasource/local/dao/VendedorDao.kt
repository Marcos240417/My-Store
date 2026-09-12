package com.example.mymercado.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mymercado.data.datasource.local.entity.VendedorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VendedorDao {
    @Query("SELECT * FROM vendedores")
    fun listarVendedores(): Flow<List<VendedorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cadastrarVendedor(vendedor: VendedorEntity)
}