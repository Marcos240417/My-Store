package com.example.mymercado.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.features.checkout.CheckoutViewModel
import com.example.mymercado.features.home.HomeViewModel
import com.example.mymercado.ui.screen.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // --- TELA SPLASH ---
        composable("splash") {
            SplashScreen(onFinished = {
                navController.navigate(Screen.Home.route) {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // --- TELA HOME ---
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
                },
                onNavigateToFavoritos = {
                    navController.navigate("favoritos")
                }
            )
        }

        // --- TELA DETALHES ---
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

        // --- TELA FAVORITOS ---
        composable("favoritos") {
            FavoritosScreen(
                viewModel = koinViewModel(),
                onProdutoClick = { id ->
                    navController.navigate(Screen.DetalhesProduto.createRoute(id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- TELA CARRINHO ---
        composable(Screen.Carrinho.route) {
            CarrinhoScreen(
                viewModel = koinViewModel(),
                onBack = { navController.popBackStack() },
                onNavigateToCheckout = { forma: FormaPagamento ->
                    navController.navigate("checkout/${forma.name}")
                }
            )
        }

        // --- TELA DE CHECKOUT (SINCRONIZADA) ---
        composable(
            route = "checkout/{forma}",
            arguments = listOf(navArgument("forma") { type = NavType.StringType })
        ) { backStackEntry ->
            val formaName = backStackEntry.arguments?.getString("forma") ?: "PIX"
            val forma = try {
                FormaPagamento.valueOf(formaName)
            } catch (e: Exception) {
                FormaPagamento.PIX
            }

            val checkoutViewModel: CheckoutViewModel = koinViewModel()
            val homeViewModel: HomeViewModel = koinViewModel()

            // Coletamos os itens do carrinho para passar à Screen e à função de finalizar
            val itensNoCarrinho by homeViewModel.itensCarrinho.collectAsState()

            CheckoutScreen(
                formaPagamento = forma,
                itensNoCarrinho = itensNoCarrinho,
                viewModel = checkoutViewModel,
                onBack = { navController.popBackStack() },
                onConfirmarPagamento = { desconto ->
                    checkoutViewModel.confirmarPagamento(
                        usuarioEmail = "user@galga.com",
                        formaPagamento = forma.name,
                        itensNoCarrinho = itensNoCarrinho,
                        valorDesconto = desconto,
                        onSucesso = {
                            navController.navigate("sucesso") {
                                // Limpa a pilha até a Home para evitar que o usuário volte ao checkout
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    )
                }
            )
        }

        // --- TELA DE SUCESSO ---
        composable("sucesso") {
            SucessoScreen(
                onVoltarParaHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onIrParaPedidos = {
                    navController.navigate("historico") {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        // --- TELA PERFIL ---
        composable(Screen.Perfil.route) {
            PerfilScreen(
                viewModel = koinViewModel(),
                onBack = { navController.popBackStack() },
                onNavigateToCadastro = {
                    navController.navigate(Screen.Cadastro.route)
                },
                onNavigateToPedidos = {
                    navController.navigate("historico")
                },
                onLogout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // --- TELA HISTÓRICO DE PEDIDOS ---
        composable("historico") {
            HistoricoScreen(
                viewModel = koinViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        // --- TELA CADASTRO ---
        composable(Screen.Cadastro.route) {
            CadastroScreen(
                viewModel = koinViewModel(),
                onBack = { navController.popBackStack() },
                onSucesso = {
                    navController.navigate(Screen.Perfil.route) {
                        popUpTo(Screen.Cadastro.route) { inclusive = true }
                    }
                }
            )
        }
    }
}