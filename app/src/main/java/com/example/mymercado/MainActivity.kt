package com.example.mymercado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.mymercado.features.configuracoes.ConfiguracoesViewModel
import com.example.mymercado.presentation.navigation.NavGraph
import com.example.mymercado.ui.theme.MyMercadoTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val configViewModel: ConfiguracoesViewModel = koinViewModel()
            val modoEscuroAtivo by configViewModel.modoEscuro.collectAsState()

            MyMercadoTheme(darkTheme = modoEscuroAtivo) {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}