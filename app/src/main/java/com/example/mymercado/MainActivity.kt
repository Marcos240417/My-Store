package com.example.mymercado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.mymercado.navigation.NavGraph
import com.example.mymercado.ui.theme.MyMercadoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMercadoTheme {
                val navController = rememberNavController()
                // Se esta linha estiver vermelha ou não estiver roxa/colorida,
                // o import acima está errado.
                NavGraph(navController = navController)
            }
        }
    }
}