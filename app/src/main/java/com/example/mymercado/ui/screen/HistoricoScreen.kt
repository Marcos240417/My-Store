package com.example.mymercado.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
// IMPORTS CRÍTICOS PARA O 'by' FUNCIONAR
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.features.historico.HistoricoViewModel
import com.google.gson.Gson
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    viewModel: HistoricoViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val pedidos by viewModel.pedidos.collectAsState()
    val localeBr = remember { Locale.Builder().setLanguage("pt").setRegion("BR").build() }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = { Text("Meus Pedidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (pedidos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("Nenhuma compra encontrada.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pedidos) { pedido ->
                    val itens = remember(pedido.itensResumo) {
                        try {
                            Gson().fromJson(pedido.itensResumo, Array<CarrinhoEntity>::class.java).toList()
                        } catch (_: Exception) { emptyList() }
                    }

                    // Card da Shopee mantido conforme solicitado anteriormente
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, null, Modifier.size(16.dp))
                                Text("Pedido #${pedido.id}", Modifier.padding(start = 6.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.weight(1f))
                                Text("Concluído", color = Color(0xFFEE4D2D), fontSize = 12.sp)
                            }
                            HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                            itens.forEach { item ->
                                Row(Modifier.padding(vertical = 4.dp)) {
                                    AsyncImage(
                                        model = item.urlImagem,
                                        contentDescription = null,
                                        modifier = Modifier.size(60.dp).background(Color(0xFFF9F9F9)),
                                        contentScale = ContentScale.Fit
                                    )
                                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                                        Text(item.titulo, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("x${item.quantidade}", color = Color.Gray, fontSize = 12.sp)
                                        Text(
                                            text = String.format(localeBr, "R$ %.2f", item.precoNoMomento),
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.End,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                Text("Total do Pedido: ", fontSize = 12.sp)
                                Text(
                                    text = String.format(localeBr, "R$ %.2f", pedido.total),
                                    color = Color(0xFFEE4D2D),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}