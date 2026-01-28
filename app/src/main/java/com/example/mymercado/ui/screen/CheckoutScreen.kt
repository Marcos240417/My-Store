package com.example.mymercado.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.features.checkout.CheckoutViewModel
import com.example.mymercado.features.checkout.PagamentoStatus
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    formaPagamento: FormaPagamento,
    itensNoCarrinho: List<CarrinhoEntity>,
    viewModel: CheckoutViewModel = koinViewModel(),
    onBack: () -> Unit,
    onConfirmarPagamento: (Double) -> Unit // Passamos o desconto para finalizar
) {
    val context = LocalContext.current
    val localeBr = remember { Locale.forLanguageTag("pt-BR") }

    val statusPagamento by viewModel.status.collectAsState()
    val valorDesconto by viewModel.desconto.collectAsState()

    val subtotal = remember(itensNoCarrinho) {
        itensNoCarrinho.sumOf { it.precoNoMomento * it.quantidade }
    }
    val totalFinal = subtotal - valorDesconto

    // Sincroniza detalhes do pagamento sempre que o total final mudar (ex: aplicar cupom)
    LaunchedEffect(formaPagamento, totalFinal) {
        viewModel.carregarDetalhesPagamento(formaPagamento, totalFinal)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Pagamento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CARD DE RESUMO DE VALORES
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Resumo do Pedido", style = MaterialTheme.typography.labelMedium)
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Subtotal:")
                        Text(String.format(localeBr, "R$ %.2f", subtotal))
                    }
                    if (valorDesconto > 0) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Desconto:", color = Color(0xFF2E7D32))
                            Text(String.format(localeBr, "- R$ %.2f", valorDesconto), color = Color(0xFF2E7D32))
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = String.format(localeBr, "Total: R$ %.2f", totalFinal),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SEÇÃO DE CUPOM
            SecaoCupom(viewModel = viewModel, subtotal = subtotal)

            Spacer(modifier = Modifier.height(24.dp))

            // CONTEÚDO DINÂMICO DO MEIO DE PAGAMENTO
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when (val status = statusPagamento) {
                    is PagamentoStatus.Carregando -> {
                        CircularProgressIndicator()
                    }
                    is PagamentoStatus.PixGerado -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            status.qrCode?.let {
                                Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(180.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("PIX", status.chave))
                                Toast.makeText(context, "Chave PIX copiada!", Toast.LENGTH_SHORT).show()
                            }) { Text("COPIAR CHAVE PIX") }
                        }
                    }
                    is PagamentoStatus.BoletoGerado -> {
                        OutlinedTextField(
                            value = status.codigo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Código de barras do boleto") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is PagamentoStatus.CartaoOpcoes -> {
                        Text("Pagamento via Cartão de Crédito selecionado.")
                        Text("Total: R$ ${String.format(localeBr, "%.2f", totalFinal)}", fontWeight = FontWeight.Bold)
                    }
                    else -> { }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onConfirmarPagamento(valorDesconto) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("CONFIRMAR E FINALIZAR COMPRA")
            }
        }
    }
}