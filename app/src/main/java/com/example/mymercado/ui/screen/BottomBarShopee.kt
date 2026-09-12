package com.example.mymercado.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mymercado.presentation.navigation.Screen

@Composable
fun BottomBarShopee(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val itens = listOf(
            Triple(Screen.Home.route, Icons.Default.Home, "Início"),
            Triple("historico", Icons.Default.LocalMall, "Pedidos"),
            Triple("lives", Icons.Default.Videocam, "Live"),
            Triple("notificacoes", Icons.Default.Notifications, "Avisos"),
            Triple(Screen.Perfil.route, Icons.Default.Person, "Eu")
        )

        itens.forEach { (rota, icone, label) ->
            val isSelected = rotaAtual == rota

            NavigationBarItem(
                icon = { Icon(icone, contentDescription = label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                selected = isSelected,
                onClick = {
                    if (rotaAtual != rota) {
                        navController.navigate(rota) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            )
        }
    }
}