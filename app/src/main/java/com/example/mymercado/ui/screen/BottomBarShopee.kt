package com.example.mymercado.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomBarShopee(navController: NavController) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, "Início") },
            label = { Text("Início") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFEE4D2D))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocalMall, "Descobertas") },
            label = { Text("Descobertas") },
            selected = false,
            onClick = { }
        )
        // ... ícones de Notificações e Perfil (Eu)
    }
}