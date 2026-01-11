package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymercado.features.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetalhes: (Int) -> Unit,
    onNavigateToCarrinho: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val produtos by viewModel.produtos.collectAsState()
    val estaCarregando by viewModel.estaCarregando.collectAsState()

    // Observa os itens do carrinho para o contador
    val itensCarrinho by viewModel.itensCarrinho.collectAsState()
    val quantidadeCarrinho = itensCarrinho.sumOf { it.quantidade }

    val categorias = listOf("Todos", "electronics", "jewelery", "men's clothing", "women's clothing")
    var categoriaSelecionada by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mercado Galga", fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = onNavigateToCarrinho) {
                        // Implementação do Contador Vermelho
                        BadgedBox(
                            badge = {
                                if (quantidadeCarrinho > 0) {
                                    Badge(containerColor = Color.Red) {
                                        Text(quantidadeCarrinho.toString(), color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                        }
                    }
                    IconButton(onClick = onNavigateToPerfil) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        }
    ) { padding ->
        if (estaCarregando && produtos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("PRODUTOS FAKESTORE API", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categorias) { categoria ->
                            FilterChip(
                                selected = (categoria == categoriaSelecionada),
                                onClick = {
                                    viewModel.filtrarPorCategoria(categoria)
                                },
                                label = { Text(categoria.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }

                items(produtos) { produto ->
                    ProdutoCard(
                        produto = produto,
                        onProdutoClick = { onNavigateToDetalhes(produto.produtoId) },
                        onAdicionarCarrinho = { viewModel.adicionarAoCarrinho(produto) }
                    )
                }
            }
        }
    }
}