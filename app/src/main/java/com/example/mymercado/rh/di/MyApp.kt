package com.example.mymercado.rh.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Define o nível de log para ajudar no debug se algo falhar
            androidLogger(Level.ERROR)
            // Passa o contexto da aplicação para o Room e o Koin
            androidContext(this@MyApp)
            // Carrega a lista de módulos (as variáveis que criamos)
            modules(appModule)
        }
    }
}