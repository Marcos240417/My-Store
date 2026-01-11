package com.example.mymercado.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mymercado.ui.screen.CarrinhoScreen
import com.example.mymercado.ui.screen.DetalhesScreen
import com.example.mymercado.ui.screen.HomeScreen
import com.example.mymercado.ui.screen.PerfilScreen
import com.example.mymercado.ui.screen.CadastroScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // TELA HOME: Ponto de entrada principal
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = koinViewModel(),
                onNavigateToDetalhes = { id ->
                    navController.navigate(Screen.DetalhesProduto.createRoute(id))
                },
                onNavigateToCarrinho = {
                    navController.navigate(Screen.Carrinho.route)
                },
                onNavigateToPerfil = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }

        // TELA DETALHES: Recebe o ID do produto como argumento
        composable(
            route = Screen.DetalhesProduto.route,
            arguments = listOf(navArgument("produtoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val produtoId = backStackEntry.arguments?.getInt("produtoId") ?: 0
            DetalhesScreen(
                produtoId = produtoId,
                viewModel = koinViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        // TELA CARRINHO
        composable(Screen.Carrinho.route) {
            CarrinhoScreen(
                viewModel = koinViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        // TELA PERFIL: Alterna entre Card de Dados ou Botão Cadastrar
        composable(Screen.Perfil.route) {
            PerfilScreen(
                viewModel = koinViewModel(),
                onBack = { navController.popBackStack() },
                onNavigateToCadastro = {
                    navController.navigate(Screen.Cadastro.route)
                },
                onLogout = {
                    // Ao "Sair", limpa o estado e volta para a Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // TELA CADASTRO: Implementação do ViaCEP e Salvamento
        composable(Screen.Cadastro.route) {
            CadastroScreen(
                viewModel = koinViewModel(),
                onSucesso = {
                    // Após salvar no Room, volta para o Perfil
                    // O popUpTo garante que o formulário saia da pilha de telas
                    navController.navigate(Screen.Perfil.route) {
                        popUpTo(Screen.Cadastro.route) { inclusive = true }
                    }
                }
            )
        }
    }
}