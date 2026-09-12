package com.example.mymercado.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mymercado.core.util.LocalNotificationHelper
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.features.historico.HistoricoViewModel
import com.google.gson.Gson
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ItemPedidoCardUi(
    val pedidoId: Int,
    val produto: CarrinhoEntity,
    val dataEntregaEstimada: Long,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    viewModel: HistoricoViewModel = koinViewModel(),
    onBack: () -> Unit,
    onRastrearPedido: (pedidoId: Int, status: String, dataEntrega: Long) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val pedidos by viewModel.pedidos.collectAsState()
    val localeBr = remember { Locale.forLanguageTag("pt-BR") }
    val formatoData = remember { SimpleDateFormat("dd/MM/yyyy", localeBr) }

    // Desmembra cada pedido para que cada produto tenha seu próprio card individual
    val listaCardsIndividuais: List<ItemPedidoCardUi> = remember(pedidos) {
        pedidos.flatMap { pedido ->
            val itens: List<CarrinhoEntity> = runCatching {
                val array = Gson().fromJson(pedido.itensResumo, Array<CarrinhoEntity>::class.java)
                array?.toList() ?: emptyList()
            }.getOrDefault(emptyList())

            if (itens.isNotEmpty()) {
                itens.map { item ->
                    ItemPedidoCardUi(
                        pedidoId = pedido.id,
                        produto = item,
                        dataEntregaEstimada = pedido.dataEntregaEstimada,
                        status = pedido.status
                    )
                }
            } else {
                listOf(
                    ItemPedidoCardUi(
                        pedidoId = pedido.id,
                        produto = CarrinhoEntity(
                            produtoId = pedido.id,
                            usuarioEmail = pedido.usuarioEmail,
                            titulo = "Pedido #${pedido.id}",
                            precoNoMomento = pedido.total,
                            urlImagem = pedido.urlImagem,
                            quantidade = pedido.quantidadeTotal,
                            vendedorNome = "Loja Oficial"
                        ),
                        dataEntregaEstimada = pedido.dataEntregaEstimada,
                        status = pedido.status
                    )
                )
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Minhas Compras", style = MaterialTheme.typography.titleLarge) },
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
        }
    ) { padding ->
        if (listaCardsIndividuais.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum pedido em andamento.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = listaCardsIndividuais,
                    key = { "${it.pedidoId}_${it.produto.produtoId}" }
                ) { itemCard ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 1. Cabeçalho com Número do Pedido e Badge de Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pedido #${itemCard.pedidoId}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = if (itemCard.status == "ENTREGUE") "Entregue" else "A caminho",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // 2. Previsão de Entrega
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Previsão de entrega: ${formatoData.format(Date(itemCard.dataEntregaEstimada))}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // 3. Detalhe do Produto Único (Foto, Nome e Quantidade)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = itemCard.produto.urlImagem,
                                    contentDescription = itemCard.produto.titulo,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentScale = ContentScale.Fit
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = itemCard.produto.titulo,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Qtd: ${itemCard.produto.quantidade} un.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // 4. Rodapé com Valor Individual e Ações (Rastrear + Confirmar Entrega)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Valor",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format(localeBr, "R$ %.2f", itemCard.produto.precoNoMomento * itemCard.produto.quantidade),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            onRastrearPedido(
                                                itemCard.pedidoId,
                                                itemCard.status,
                                                itemCard.dataEntregaEstimada
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Rastrear", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.confirmarEntregaItem(
                                                pedidoId = itemCard.pedidoId,
                                                produtoId = itemCard.produto.produtoId
                                            )

                                            LocalNotificationHelper.notificar(
                                                context = context,
                                                titulo = "Produto Entregue! 📦",
                                                mensagem = "A entrega de \"${itemCard.produto.titulo}\" foi confirmada com sucesso.",
                                                notificationId = itemCard.pedidoId
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Entregue",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}