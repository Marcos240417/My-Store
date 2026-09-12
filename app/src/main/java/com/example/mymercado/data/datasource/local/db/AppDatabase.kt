package com.example.mymercado.data.datasource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mymercado.data.datasource.local.dao.CarrinhoDao
import com.example.mymercado.data.datasource.local.dao.EnderecoDao
import com.example.mymercado.data.datasource.local.dao.NotificacaoDao
import com.example.mymercado.data.datasource.local.dao.PedidoDao
import com.example.mymercado.data.datasource.local.dao.ProdutoDao
import com.example.mymercado.data.datasource.local.dao.UsuarioDao
import com.example.mymercado.data.datasource.local.dao.VendedorDao
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.data.datasource.local.entity.EnderecoEntity
import com.example.mymercado.data.datasource.local.entity.NotificacaoEntity
import com.example.mymercado.data.datasource.local.entity.PedidoEntity
import com.example.mymercado.data.datasource.local.entity.ProdutoEntity
import com.example.mymercado.data.datasource.local.entity.UsuarioEntity
import com.example.mymercado.data.datasource.local.entity.VendedorEntity

@Database(
    entities = [
        UsuarioEntity::class,
        VendedorEntity::class,
        ProdutoEntity::class,
        CarrinhoEntity::class,
        PedidoEntity::class,
        NotificacaoEntity::class,
        EnderecoEntity::class // Adicionado
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun vendedorDao(): VendedorDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun carrinhoDao(): CarrinhoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun notificacaoDao(): NotificacaoDao
    abstract fun enderecoDao(): EnderecoDao // Adicionado

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "galga_vendas_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}