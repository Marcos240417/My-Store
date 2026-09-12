package com.example.mymercado.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mymercado.core.common.AppConstants
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.core.datastore.SessionManager
import com.example.mymercado.core.util.LocalNotificationHelper
import com.example.mymercado.features.authViewModel.AuthViewModel
import com.example.mymercado.features.carrinho.CarrinhoViewModel
import com.example.mymercado.features.checkout.CheckoutViewModel
import com.example.mymercado.features.detalhes.DetalhesViewModel
import com.example.mymercado.features.perfil.PerfilViewModel
import com.example.mymercado.ui.screen.CadastroScreen
import com.example.mymercado.ui.screen.CarrinhoScreen
import com.example.mymercado.ui.screen.CheckoutScreen
import com.example.mymercado.ui.screen.ConfiguracoesScreen
import com.example.mymercado.ui.screen.DetalhesScreen
import com.example.mymercado.ui.screen.EnderecosScreen
import com.example.mymercado.ui.screen.FavoritosScreen
import com.example.mymercado.ui.screen.HistoricoScreen
import com.example.mymercado.ui.screen.HomeScreen
import com.example.mymercado.ui.screen.LivesScreen
import com.example.mymercado.ui.screen.LoginScreen
import com.example.mymercado.ui.screen.MeusPedidosScreen
import com.example.mymercado.ui.screen.NotificacoesScreen
import com.example.mymercado.ui.screen.PerfilScreen
import com.example.mymercado.ui.screen.RastreioPedidoScreen
import com.example.mymercado.ui.screen.SplashScreen
import com.example.mymercado.ui.screen.SucessoScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current

    val safeBack: () -> Unit = {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    val sessionManager: SessionManager = koinInject()
    val authViewModel: AuthViewModel = koinViewModel()
    val usuarioEmailSessao by sessionManager.usuarioEmailFlow.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // --- SPLASH SCREEN ---
        composable("splash") {
            SplashScreen(
                sessionManager = sessionManager,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // --- LOGIN SCREEN ---
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSucesso = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- HOME SCREEN ---
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = koinViewModel(),
                navController = navController,
                onNavigateToDetalhes = { id ->
                    navController.navigate(Screen.DetalhesProduto.createRoute(id))
                },
                onNavigateToCarrinho = { navController.navigate(Screen.Carrinho.route) },
                onNavigateToPerfil = { navController.navigate(Screen.Perfil.route) },
                onNavigateToFavoritos = { navController.navigate("favoritos") }
            )
        }

        // --- DETALHES DO PRODUTO ---
        composable(
            route = Screen.DetalhesProduto.route,
            arguments = listOf(navArgument("produtoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val produtoId = backStackEntry.arguments?.getInt("produtoId") ?: 0
            DetalhesScreen(
                produtoId = produtoId,
                viewModel = koinViewModel<DetalhesViewModel>(),
                onBack = safeBack,
                onNavigateToCarrinho = { navController.navigate(Screen.Carrinho.route) },
                onComprarAgora = { navController.navigate(Screen.Carrinho.route) }
            )
        }

        // --- FAVORITOS ---
        composable("favoritos") {
            FavoritosScreen(
                viewModel = koinViewModel(),
                onProdutoClick = { id ->
                    navController.navigate(Screen.DetalhesProduto.createRoute(id))
                },
                onBack = safeBack
            )
        }

        // --- CARRINHO ---
        composable(Screen.Carrinho.route) {
            CarrinhoScreen(
                viewModel = koinViewModel(),
                onBack = safeBack,
                onNavigateToCheckout = { forma: FormaPagamento ->
                    navController.navigate("checkout/${forma.name}")
                }
            )
        }

        // --- CHECKOUT ---
        composable(
            route = "checkout/{forma}",
            arguments = listOf(navArgument("forma") { type = NavType.StringType })
        ) { backStackEntry ->
            val formaName = backStackEntry.arguments?.getString("forma") ?: "PIX"
            val forma = runCatching { FormaPagamento.valueOf(formaName) }.getOrDefault(FormaPagamento.PIX)

            val checkoutViewModel: CheckoutViewModel = koinViewModel()
            val carrinhoViewModel: CarrinhoViewModel = koinViewModel()

            val itensDoCarrinho by carrinhoViewModel.itensAgrupados.collectAsState()
            val itensSelecionados by carrinhoViewModel.selecionados.collectAsState()

            val itensParaComprar = remember(itensDoCarrinho, itensSelecionados) {
                val todos = itensDoCarrinho.values.flatten()
                val filtrados = todos.filter { it.produtoId in itensSelecionados }
                filtrados.ifEmpty { todos }
            }

            CheckoutScreen(
                formaPagamentoInicial = forma,
                itensNoCarrinho = itensParaComprar,
                viewModel = checkoutViewModel,
                onBack = { navController.popBackStack() },
                onConfirmarPagamento = { desconto, frete, diasEntrega ->
                    val emailAtivo = usuarioEmailSessao ?: AppConstants.DEFAULT_USER_EMAIL
                    checkoutViewModel.confirmarPagamento(
                        usuarioEmail = emailAtivo,
                        formaPagamento = forma.name,
                        itensNoCarrinho = itensParaComprar,
                        valorDesconto = desconto,
                        valorFrete = frete,
                        diasEntrega = diasEntrega,
                        onSucesso = {
                            LocalNotificationHelper.notificar(
                                context = context,
                                titulo = "Compra Realizada! 🎉",
                                mensagem = "Seu pedido de ${itensParaComprar.sumOf { it.quantidade }} item(ns) foi aprovado.",
                                notificationId = System.currentTimeMillis().toInt()
                            )

                            navController.navigate("sucesso") {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    )
                }
            )
        }

        // --- SUCESSO ---
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

        // --- PERFIL ---
        composable(Screen.Perfil.route) {
            PerfilScreen(
                viewModel = koinViewModel<PerfilViewModel>(),
                onBack = safeBack,
                onNavigateToCadastro = { navController.navigate(Screen.Cadastro.route) },
                onNavigateToPedidos = { navController.navigate("historico") },
                onNavigateToEnderecos = { navController.navigate("enderecos") },
                onNavigateToConfiguracoes = { navController.navigate("configuracoes") },
                onLogout = {
                    authViewModel.deslogar {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- CONFIGURAÇÕES ---
        composable("configuracoes") {
            ConfiguracoesScreen(
                onBack = { navController.popBackStack() },
                onContaExcluida = {
                    authViewModel.deslogar {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- MEUS ENDEREÇOS ---
        composable("enderecos") {
            EnderecosScreen(onBack = safeBack)
        }

        // --- HISTÓRICO / MINHAS COMPRAS ---
        composable("historico") {
            HistoricoScreen(
                viewModel = koinViewModel(),
                onBack = safeBack,
                onRastrearPedido = { pedidoId, status, dataEntrega ->
                    navController.navigate("rastreio/$pedidoId/$status/$dataEntrega")
                }
            )
        }

        composable("meus_pedidos") {
            MeusPedidosScreen(
                viewModel = koinViewModel(),
                onVoltar = safeBack,
                onIrParaLoja = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // --- RASTREIO DE PEDIDO ---
        composable(
            route = "rastreio/{pedidoId}/{status}/{dataEntrega}",
            arguments = listOf(
                navArgument("pedidoId") { type = NavType.IntType },
                navArgument("status") { type = NavType.StringType },
                navArgument("dataEntrega") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: 0
            val status = backStackEntry.arguments?.getString("status") ?: "A_CAMINHO"
            val dataEntrega = backStackEntry.arguments?.getLong("dataEntrega") ?: System.currentTimeMillis()

            RastreioPedidoScreen(
                pedidoId = pedidoId,
                statusAtual = status,
                dataEntregaEstimada = dataEntrega,
                onBack = safeBack
            )
        }

        // --- NOTIFICAÇÕES ---
        composable("notificacoes") {
            NotificacoesScreen(
                onBack = safeBack,
                onNavigateToHistorico = { navController.navigate("historico") }
            )
        }

        // --- LIVES ---
        composable("lives") {
            LivesScreen(onBack = safeBack)
        }

        // --- CADASTRO ---
        composable(Screen.Cadastro.route) {
            CadastroScreen(
                viewModel = koinViewModel(),
                onBack = safeBack,
                onSucesso = {
                    navController.navigate(Screen.Perfil.route) {
                        popUpTo(Screen.Cadastro.route) { inclusive = true }
                    }
                }
            )
        }
    }
}