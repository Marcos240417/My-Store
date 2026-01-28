package com.example.mymercado.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mercado Galga", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToFavoritos) {
                        Icon(Icons.Default.FavoriteBorder, null)
                    }
                    IconButton(onClick = onNavigateToCarrinho) {
                        BadgedBox(badge = {
                            if (totalQuantidade > 0) {
                                Badge(containerColor = Color(0xFFEE4D2D)) {
                                    Text(totalQuantidade.toString())
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                        }
                    }
                    IconButton(onClick = onNavigateToPerfil) { Icon(Icons.Default.Person, null) }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5) // Fundo cinza suave Shopee
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = estaCarregando,
            onRefresh = { viewModel.sincronizar() }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // 1. CABEÇALHO COMERCIAL (Banner e Categorias)
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.background(Color.White)) {
                        AnimatedVisibility(visible = avisoOffline) {
                            Surface(color = MaterialTheme.colorScheme.errorContainer) {
                                Text(
                                    "Modo Offline - Exibindo dados locais",
                                    Modifier.fillMaxWidth().padding(8.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        // Carrossel com as 5 imagens locais que incluímos
                        BannerCarousel()

                        // Ícones de atalhos rápidos
                        CategoriaAtalhos()

                        // Barra de busca estilizada
                        OutlinedTextField(
                            value = busca,
                            onValueChange = { viewModel.atualizarBusca(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = { Text("Buscar na Galga", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                            shape = RoundedCornerShape(4.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF0F0F0),
                                focusedContainerColor = Color(0xFFF0F0F0),
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color(0xFFEE4D2D)
                            )
                        )
                    }
                }

                // 2. OFERTA RELÂMPAGO (Seção com fundo branco e elevação)
                item(span = { GridItemSpan(2) }) {
                    Surface(
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        FlashSaleHeader()
                    }
                }

                // 3. SELETOR DE CATEGORIAS (Filtros)
                item(span = { GridItemSpan(2) }) {
                    val categorias = listOf("Todos", "electronics", "jewelery", "men's clothing", "women's clothing")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        items(categorias) { cat ->
                            FilterChip(
                                selected = cat == categoriaAtiva,
                                onClick = { viewModel.atualizarCategoria(cat) },
                                label = { Text(cat.replaceFirstChar { it.uppercase() }, fontSize = 12.sp) },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFE0D8),
                                    selectedLabelColor = Color(0xFFEE4D2D)
                                )
                            )
                        }
                    }
                }

                // 4. GRADE DE PRODUTOS
                items(produtos) { produto ->
                    Box(modifier = Modifier.padding(6.dp)) {
                        ProdutoCard(
                            produto = produto,
                            onProdutoClick = { onNavigateToDetalhes(produto.produtoId) },
                            onAdicionarCarrinho = { viewModel.adicionarAoCarrinho(produto) },
                            onFavoritarClick = {
                                viewModel.alternarFavorito(produto.produtoId, !produto.isFavorito)
                            }
                        )
                    }
                }
            }
        }
    }
}