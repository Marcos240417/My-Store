package com.example.mymercado.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Carrinho : Screen("carrinho")
    data object Perfil : Screen("perfil")
    data object Cadastro : Screen("cadastro") // Nova rota para o cadastro real

    data object DetalhesProduto : Screen("detalhes/{produtoId}") {
        fun createRoute(produtoId: Int): String {
            return "detalhes/$produtoId"
        }
    }
}