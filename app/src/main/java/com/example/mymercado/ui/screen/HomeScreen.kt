package com.example.mymercado.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mymercado.data.datasource.local.entity.ProdutoEntity
import com.example.mymercado.features.home.HomeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    onNavigateToDetalhes: (Int) -> Unit,
    onNavigateToCarrinho: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToFavoritos: () -> Unit
) {
    val context = LocalContext.current
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
                title = { Text("Mercado Galga", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onNavigateToFavoritos) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favoritos")
                    }
                    IconButton(onClick = onNavigateToCarrinho) {
                        BadgedBox(badge = {
                            if (totalQuantidade > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                    Text(totalQuantidade.toString())
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                        }
                    }
                    IconButton(onClick = onNavigateToPerfil) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomBarShopee(navController = navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = estaCarregando,
            onRefresh = { viewModel.sincronizar() }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. CABEÇALHO (Busca e Banner)
                item(span = { GridItemSpan(2) }, key = "header_main") {
                    Surface(color = MaterialTheme.colorScheme.surface) {
                        Column {
                            AnimatedVisibility(visible = avisoOffline) {
                                Surface(color = MaterialTheme.colorScheme.errorContainer) {
                                    Text(
                                        text = "Modo Offline - Exibindo dados locais",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = busca,
                                onValueChange = { viewModel.atualizarBusca(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                placeholder = { Text("Buscar na Galga", style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )

                            BannerCarousel()
                        }
                    }
                }

                // 2. ATALHOS - Corrigido para não quebrar a busca
                item(span = { GridItemSpan(2) }, key = "shortcuts_section") {
                    CategoriaAtalhos(
                        onAtalhoClick = { atalho ->
                            when (atalho.lowercase()) {
                                "eletrônicos" -> {
                                    viewModel.atualizarCategoria("electronics")
                                }
                                "frete grátis" -> {
                                    viewModel.filtrarFreteGratis()
                                }
                                "ofertas" -> {
                                    viewModel.filtrarOfertasRelampago()
                                }
                                "cupons" -> {
                                    Toast.makeText(context, "Use o cupom GALGA10 no checkout!", Toast.LENGTH_SHORT).show()
                                }
                                "moedas" -> {
                                    Toast.makeText(context, "Você possui 150 Moedas Galga acumuladas!", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    viewModel.atualizarCategoria("Todos")
                                }
                            }
                        }
                    )
                }

                // 3. OFERTA RELÂMPAGO
                item(span = { GridItemSpan(2) }, key = "flash_sale_section") {
                    Surface(
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        FlashSaleHeader()
                    }
                }

                // 4. SELETOR DE CATEGORIAS
                item(span = { GridItemSpan(2) }, key = "category_selector") {
                    val categorias = listOf("Todos", "electronics", "jewelery", "men's clothing", "women's clothing")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        lazyRowItems(categorias) { cat ->
                            FilterChip(
                                selected = cat == categoriaAtiva,
                                onClick = { viewModel.atualizarCategoria(cat) },
                                label = {
                                    Text(
                                        text = cat.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                // 5. GRADE DE PRODUTOS
                items(
                    items = produtos,
                    key = { it.id }
                ) { produto ->
                    Box(modifier = Modifier.padding(6.dp)) {
                        ProdutoCard(
                            produto = produto,
                            onProdutoClick = { onNavigateToDetalhes(produto.id) },
                            onAdicionarCarrinho = { viewModel.adicionarAoCarrinho(produto) },
                            onFavoritarClick = {
                                viewModel.alternarFavorito(produto.id, !produto.isFavorito)
                            }
                        )
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}