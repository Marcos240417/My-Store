package com.example.mymercado.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mymercado.features.detalhes.DetalhesViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    produtoId: Int,
    viewModel: DetalhesViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToCarrinho: () -> Unit,
    onComprarAgora: () -> Unit
) {
    LaunchedEffect(produtoId) {
        viewModel.carregarProduto(produtoId)
    }

    val produto by viewModel.produto.collectAsState()
    val haptic = LocalHapticFeedback.current
    val localeBr = remember { Locale.forLanguageTag("pt-BR") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Produto", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Compartilhar */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartilhar")
                    }
                    IconButton(onClick = onNavigateToCarrinho) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            produto?.let {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.adicionarAoCarrinho(it)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Adicionar", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            produto?.let {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.adicionarAoCarrinho(it)
                                onComprarAgora()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Comprar Agora", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    ) { padding ->
        produto?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = item.urlImagem,
                    contentDescription = item.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = String.format(localeBr, "R$ %.2f", item.preco),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = item.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Text(
                        text = "Descrição",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = item.descricao,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}