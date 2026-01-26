package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.features.carrinho.CarrinhoViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(
    viewModel: CarrinhoViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToCheckout: (FormaPagamento) -> Unit
) {
    val itens by viewModel.itensCarrinho.collectAsState()
    val total by viewModel.valorTotal.collectAsState()
    val localeBr = remember { Locale.forLanguageTag("pt-BR") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Carrinho", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (itens.isNotEmpty()) {
                        IconButton(onClick = { viewModel.finalizarCompra() }) {
                            Icon(Icons.Default.DeleteSweep, "Limpar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (itens.isNotEmpty()) {
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.navigationBarsPadding().padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = String.format(localeBr, "R$ %.2f", total),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigateToCheckout(FormaPagamento.PIX) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("IR PARA O PAGAMENTO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (itens.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Seu carrinho está vazio", color = Color.Gray)
                    TextButton(onClick = onBack) { Text("Voltar para a loja") }
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                // CORREÇÃO: Removido 'key' para evitar erro Key Already Used
                items(itens) { item ->
                    ListItem(
                        headlineContent = { Text(item.titulo, fontWeight = FontWeight.SemiBold) },
                        supportingContent = {
                            Text("${item.quantidade}x ${String.format(localeBr, "R$ %.2f", item.precoNoMomento)}")
                        },
                        trailingContent = {
                            IconButton(onClick = { viewModel.removerItem(item) }) {
                                Icon(Icons.Default.Delete, "Remover", tint = Color.Red)
                            }
                        }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}