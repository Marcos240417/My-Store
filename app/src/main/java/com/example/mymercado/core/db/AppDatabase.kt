package com.example.mymercado.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mymercado.core.dao.CarrinhoDao
import com.example.mymercado.core.dao.ProdutoDao
import com.example.mymercado.core.dao.UsuarioDao
import com.example.mymercado.core.dao.VendedorDao
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.core.data.UsuarioEntity
import com.example.mymercado.core.data.VendedorEntity

@Database(
    entities = [
        UsuarioEntity::class,
        VendedorEntity::class,
        ProdutoEntity::class,
        CarrinhoEntity::class
    ],
    version = 3, // Versão atualizada para suportar as novas entidades de vendas
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Definição dos DAOs (Contratos de acesso aos dados)
    abstract fun usuarioDao(): UsuarioDao
    abstract fun vendedorDao(): VendedorDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun carrinhoDao(): CarrinhoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Cria ou retorna a instância única do banco de dados.
         * O Singleton garante que não existam múltiplas conexões abertas.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "galga_vendas_db" // Nome do arquivo do banco de dados
                )
                    .fallbackToDestructiveMigration(false) // Limpa o banco se houver mudança de versão sem Migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}