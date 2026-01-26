package com.example.mymercado.ui.screen

import androidx.compose.foundation.clickable // CORREÇÃO: Import necessário para o .clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // CORREÇÃO: AutoMirrored version
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mymercado.features.favoritos.FavoritosViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel = koinViewModel(),
    onProdutoClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val favoritos by viewModel.produtosFavoritos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // CORREÇÃO: Usando a versão AutoMirrored recomendada
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (favoritos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Você ainda não tem favoritos.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(favoritos) { produto ->
                    ListItem(
                        headlineContent = { Text(produto.titulo) },
                        supportingContent = { Text("R$ ${produto.preco}") },
                        trailingContent = {
                            IconButton(onClick = { viewModel.removerDosFavoritos(produto.produtoId) }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    tint = Color.Red, // CORREÇÃO: Parâmetro correto é 'tint', não 'color'
                                    contentDescription = "Remover dos favoritos"
                                )
                            }
                        },
                        // CORREÇÃO: Agora o import do clickable foi adicionado
                        modifier = Modifier.clickable { onProdutoClick(produto.produtoId) }
                    )
                }
            }
        }
    }
}