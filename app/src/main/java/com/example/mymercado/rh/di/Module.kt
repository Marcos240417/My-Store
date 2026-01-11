package com.example.mymercado.rh.di

import com.example.mymercado.core.dao.CarrinhoDao
import com.example.mymercado.core.dao.ProdutoDao
import com.example.mymercado.core.dao.UsuarioDao
import com.example.mymercado.core.dao.VendedorDao
import com.example.mymercado.core.data.remotestorefake.FakeStoreService
import com.example.mymercado.core.data.remoteviacep.ViaCepService
import com.example.mymercado.core.db.AppDatabase
import com.example.mymercado.domain.repository.VendasRepository
import com.example.mymercado.domain.repository.VendasRepositoryImpl
import com.example.mymercado.features.cadastroviewmodel.CadastroViewModel
import com.example.mymercado.features.carrinho.CarrinhoViewModel
import com.example.mymercado.features.detalhes.DetalhesViewModel
import com.example.mymercado.features.home.HomeViewModel
import com.example.mymercado.features.perfil.PerfilViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 1. Definição do Banco de Dados e DAOs
val databaseModule = module {
    single { AppDatabase.getDatabase(androidContext()) }

    single<UsuarioDao> { get<AppDatabase>().usuarioDao() }
    single<ProdutoDao> { get<AppDatabase>().produtoDao() }
    single<VendedorDao> { get<AppDatabase>().vendedorDao() }
    single<CarrinhoDao> { get<AppDatabase>().carrinhoDao() }
}

// 2. Definição dos Repositórios
val repositoryModule = module {
    single<VendasRepository> {
        VendasRepositoryImpl(
            get(), // produtoDao
            get(), // carrinhoDao
            get(), // usuarioDao
            get(), // vendedorDao
            get(), // FakeStoreService
            get()  // ViaCepService (O Koin agora vai encontrar este get)
        )
    }
}

// 3. Definição das ViewModels
val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { CarrinhoViewModel(get()) }
    viewModel { DetalhesViewModel(get()) }
    viewModel { PerfilViewModel(get()) }
    viewModel { CadastroViewModel(get()) }
}

// 4. Definição de Rede (Network)
val networkModule = module {

    // Serviço da Fake Store (Produtos)
    single<FakeStoreService> {
        Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FakeStoreService::class.java)
    }

    // ADICIONADO: Serviço do ViaCEP (Endereço)
    // Sem isso, o Repository falha ao ser criado
    single<ViaCepService> {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepService::class.java)
    }
}

// Lista unificada para o startKoin
val appModule = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule,
    networkModule
)