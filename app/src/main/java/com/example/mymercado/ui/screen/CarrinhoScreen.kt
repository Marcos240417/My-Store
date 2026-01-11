package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.features.carrinho.CarrinhoViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(
    viewModel: CarrinhoViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val itens by viewModel.itensCarrinho.collectAsState()
    val total by viewModel.valorTotal.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Carrinho", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total do pedido:")
                        Text(
                            "R$ ${String.format(Locale.getDefault(), "%.2f", total)}",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Button(
                        onClick = { viewModel.finalizarCompra() },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(56.dp),
                        enabled = itens.isNotEmpty()
                    ) {
                        Text("FINALIZAR COMPRA")
                    }
                }
            }
        }
    ) { innerPadding ->
        if (itens.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Seu carrinho está vazio 🛒", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(itens, key = { it.id }) { item ->
                    ItemCarrinhoCard(
                        item = item,
                        onRemove = { viewModel.removerItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCarrinhoCard(item: CarrinhoEntity, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem Real do Produto no Carrinho
            AsyncImage(
                model = item.urlImagem,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(item.titulo, fontWeight = FontWeight.Bold, maxLines = 2)
                Text("Qtd: ${item.quantidade}", color = Color.Gray)
                Text(
                    "R$ ${String.format(Locale.getDefault(), "%.2f", item.precoNoMomento)}",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}