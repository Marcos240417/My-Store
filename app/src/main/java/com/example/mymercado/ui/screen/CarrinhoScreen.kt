package com.example.mymercado.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.features.carrinho.CarrinhoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(
    viewModel: CarrinhoViewModel,
    onBack: () -> Unit,
    onNavigateToCheckout: (FormaPagamento) -> Unit
) {
    val itensAgrupados by viewModel.itensAgrupados.collectAsState()
    val selecionados by viewModel.selecionados.collectAsState()
    val valorTotal by viewModel.valorTotal.collectAsState()
    val localeBr = remember { Locale.forLanguageTag("pt-BR") }

    val totalDeItensNoBanco = remember(itensAgrupados) { itensAgrupados.values.flatten().size }
    val todosSelecionados = totalDeItensNoBanco > 0 && selecionados.size == totalDeItensNoBanco

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrinho de Compras", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            // RESOLVE: Superfície do rodapé com cor de superfície do tema
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = todosSelecionados,
                            onCheckedChange = { viewModel.selecionarTudo(it) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Tudo", style = MaterialTheme.typography.bodyMedium)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = String.format(localeBr, "R$ %.2f", valorTotal),
                            color = MaterialTheme.colorScheme.primary, // Laranja MyMercado
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        onClick = { onNavigateToCheckout(FormaPagamento.PIX) },
                        enabled = selecionados.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Continuar (${selecionados.size})", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background // Fundo dinâmico da tela
    ) { padding ->
        if (itensAgrupados.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("Seu carrinho está vazio", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itensAgrupados.forEach { (vendedor, itens) ->
                    item {
                        // RESOLVE: Cabeçalho do vendedor com cor de superfície dinâmica
                        Row(
                            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = vendedor, style = MaterialTheme.typography.titleSmall)
                        }
                    }

                    items(itens) { item ->
                        Row(
                            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.produtoId in selecionados,
                                onCheckedChange = { viewModel.alternarSelecao(item.produtoId) }
                            )

                            AsyncImage(
                                model = item.urlImagem,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).padding(4.dp)
                            )

                            Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                                Text(item.titulo, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = String.format(localeBr, "R$ %.2f", item.precoNoMomento),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                                    IconButton(onClick = { viewModel.diminuirQuantidade(item) }, Modifier.size(24.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                            color = Color.Transparent
                                        ) {
                                            Text("-", Modifier.padding(horizontal = 8.dp), textAlign = TextAlign.Center)
                                        }
                                    }
                                    Text(item.quantidade.toString(), Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.bodyMedium)
                                    IconButton(onClick = { viewModel.aumentarQuantidade(item) }, Modifier.size(24.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                            color = Color.Transparent
                                        ) {
                                            Text("+", Modifier.padding(horizontal = 8.dp), textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }

                            IconButton(onClick = { viewModel.removerItem(item) }) {
                                Icon(Icons.Default.Delete, "Remover", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}