package com.example.mymercado.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mymercado.features.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetalhes: (Int) -> Unit,
    onNavigateToCarrinho: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToFavoritos: () -> Unit
) {
    val produtos by viewModel.produtos.collectAsState()
    val estaCarregando by viewModel.estaCarregando.collectAsState()
    val busca by viewModel.textoBusca.collectAsState()
    val categoriaAtiva by viewModel.categoriaAtiva.collectAsState()
    val itensCarrinho by viewModel.itensCarrinho.collectAsState()
    val avisoOffline by viewModel.mostrarAvisoOffline.collectAsState()

    val totalQuantidade = remember(itensCarrinho) { itensCarrinho.sumOf { it.quantidade } }
    val animOffset = remember { Animatable(0f) }

    LaunchedEffect(totalQuantidade) {
        if (totalQuantidade > 0) {
            animOffset.animateTo(10f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
            animOffset.animateTo(0f)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mercado Galga", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToFavoritos) {
                        Icon(Icons.Default.FavoriteBorder, null)
                    }
                    IconButton(
                        onClick = onNavigateToCarrinho,
                        modifier = Modifier.offset(x = animOffset.value.dp)
                    ) {
                        BadgedBox(badge = {
                            if (totalQuantidade > 0) {
                                Badge(containerColor = Color.Red) { Text(totalQuantidade.toString()) }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                        }
                    }
                    IconButton(onClick = onNavigateToPerfil) { Icon(Icons.Default.Person, null) }
                }
            )
        }
    ) { padding ->
        // COMPONENTE DE PUXAR PARA ATUALIZAR
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = estaCarregando,
            onRefresh = { viewModel.sincronizar() }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // AVISO DE MODO OFFLINE
                AnimatedVisibility(visible = avisoOffline) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Sem conexão. Exibindo dados locais.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                OutlinedTextField(
                    value = busca,
                    onValueChange = { viewModel.atualizarBusca(it) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("O que você procura hoje?") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                val categorias = listOf("Todos", "electronics", "jewelery", "men's clothing", "women's clothing")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    items(categorias) { cat ->
                        FilterChip(
                            selected = cat == categoriaAtiva,
                            onClick = { viewModel.atualizarCategoria(cat) },
                            label = { Text(cat.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                if (produtos.isEmpty() && !estaCarregando) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nenhum produto encontrado.\nPuxe para tentar novamente.", textAlign = TextAlign.Center)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(produtos) { produto ->
                            ProdutoCard(
                                produto = produto,
                                onProdutoClick = { onNavigateToDetalhes(produto.produtoId) },
                                onAdicionarCarrinho = { viewModel.adicionarAoCarrinho(produto) },
                                onFavoritarClick = { viewModel.alternarFavorito(produto.produtoId, !produto.isFavorito) }
                            )
                        }
                    }
                }
            }
        }
    }
}