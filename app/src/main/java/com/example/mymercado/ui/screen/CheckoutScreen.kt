package com.example.mymercado.ui.screen

import android.content.ClipData
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.core.util.CardNumberVisualTransformation
import com.example.mymercado.core.util.CardValidator
import com.example.mymercado.core.util.CepVisualTransformation
import com.example.mymercado.core.util.ExpiryDateVisualTransformation
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.features.checkout.CheckoutViewModel
import com.example.mymercado.features.checkout.PagamentoStatus
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    formaPagamentoInicial: FormaPagamento,
    itensNoCarrinho: List<CarrinhoEntity>,
    viewModel: CheckoutViewModel = koinViewModel(),
    onBack: () -> Unit,
    onConfirmarPagamento: (desconto: Double, frete: Double, diasUteis: Int) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val localeBr = remember { Locale.forLanguageTag("pt-BR") }

    var formaSelecionada by remember { mutableStateOf(formaPagamentoInicial) }
    var parcelaSelecionada by remember { mutableIntStateOf(1) }
    var menuParcelasExpandido by remember { mutableStateOf(false) }

    var numeroCartao by remember { mutableStateOf("") }
    var nomeTitular by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val statusPagamento by viewModel.status.collectAsState()
    val valorDesconto by viewModel.desconto.collectAsState()

    val cepDigitado by viewModel.cepDigitado.collectAsState()
    val opcoesFrete by viewModel.opcoesFrete.collectAsState()
    val freteSelecionado by viewModel.opcaoFreteSelecionada.collectAsState()

    val subtotal = remember(itensNoCarrinho) {
        itensNoCarrinho.sumOf { it.precoNoMomento * it.quantidade }
    }

    LaunchedEffect(Unit) {
        viewModel.onCepAlterado(cepDigitado, subtotal)
    }

    val valorFrete = freteSelecionado?.valor ?: 0.0

    val totalComEncargos = remember(subtotal, valorFrete, formaSelecionada, parcelaSelecionada) {
        val base = subtotal + valorFrete
        if (formaSelecionada == FormaPagamento.CARTAO_CREDITO && parcelaSelecionada >= 6) {
            base * (1 + (0.02 * (parcelaSelecionada - 5)))
        } else {
            base
        }
    }

    val totalFinal = (totalComEncargos - valorDesconto).coerceAtLeast(0.0)

    LaunchedEffect(formaSelecionada, totalFinal) {
        viewModel.carregarDetalhesPagamento(formaSelecionada, totalFinal)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Pagamento", style = MaterialTheme.typography.titleLarge) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Seção de Entrega e Frete
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Endereço e Opções de Entrega",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cepDigitado,
                        onValueChange = { viewModel.onCepAlterado(it, subtotal) },
                        label = { Text("CEP de entrega") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = CepVisualTransformation(),
                        singleLine = true
                    )

                    if (opcoesFrete.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Selecione o tipo de frete:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))

                        opcoesFrete.forEach { opcao ->
                            val selecionado = opcao.id == freteSelecionado?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selecionado) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                    .border(
                                        width = if (selecionado) 1.5.dp else 0.5.dp,
                                        color = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.selecionarOpcaoFrete(opcao) }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = if (selecionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = opcao.nome,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text(
                                            text = opcao.descricao,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = if (opcao.valor == 0.0) "GRÁTIS" else String.format(localeBr, "R$ %.2f", opcao.valor),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (opcao.valor == 0.0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 2. Resumo de Valores
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumo de Valores",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyLarge)
                        Text(String.format(localeBr, "R$ %.2f", subtotal), style = MaterialTheme.typography.bodyLarge)
                    }

                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Frete", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = if (valorFrete == 0.0) "Grátis" else String.format(localeBr, "R$ %.2f", valorFrete),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (valorFrete == 0.0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (formaSelecionada == FormaPagamento.CARTAO_CREDITO && parcelaSelecionada >= 6) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Juros de parcelamento", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                            Text(String.format(localeBr, "+ R$ %.2f", totalComEncargos - (subtotal + valorFrete)), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (valorDesconto > 0) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Descontos aplicados", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodyMedium)
                            Text(String.format(localeBr, "- R$ %.2f", valorDesconto), color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total a pagar", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = String.format(localeBr, "R$ %.2f", totalFinal),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // 3. Cupom
            Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                SecaoCupom(viewModel = viewModel, subtotal = subtotal)
            }

            Spacer(Modifier.height(12.dp))

            // 4. Seletor de Forma de Pagamento
            PaymentSelector(
                selectedForma = formaSelecionada,
                onFormaSelected = { novaForma ->
                    formaSelecionada = novaForma
                    if (novaForma != FormaPagamento.CARTAO_CREDITO) parcelaSelecionada = 1
                }
            )

            Spacer(Modifier.height(16.dp))

            // 5. Área Dinâmica de Pagamento
            when (formaSelecionada) {
                FormaPagamento.PIX -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pix Copia e Cola",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "Aprovação Imediata",
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            when (val status = statusPagamento) {
                                is PagamentoStatus.PixGerado -> {
                                    Box(
                                        modifier = Modifier
                                            .size(220.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        status.qrCode?.let { qr ->
                                            Image(
                                                bitmap = qr.asImageBitmap(),
                                                contentDescription = "QR Code Pix",
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } ?: CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Escaneie o QR Code acima no app do seu banco ou copie o código abaixo:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = status.chave,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = MaterialTheme.typography.bodySmall,
                                        maxLines = 3,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                val clipData = ClipData.newPlainText("PIX", status.chave)
                                                clipboard.setClipEntry(clipData.toClipEntry())
                                                Toast.makeText(context, "Código Pix copiado com sucesso!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("COPIAR CÓDIGO PIX", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                                else -> {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                FormaPagamento.CARTAO_CREDITO -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("Parcelamento", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = menuParcelasExpandido,
                            onExpandedChange = { menuParcelasExpandido = it }
                        ) {
                            OutlinedTextField(
                                value = "${parcelaSelecionada}x de ${String.format(localeBr, "R$ %.2f", totalComEncargos / parcelaSelecionada)}",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuParcelasExpandido) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = menuParcelasExpandido,
                                onDismissRequest = { menuParcelasExpandido = false }
                            ) {
                                for (i in 1..12) {
                                    val valorOpcao = if (i >= 6) (subtotal + valorFrete) * (1 + (0.02 * (i - 5))) else (subtotal + valorFrete)
                                    DropdownMenuItem(
                                        text = { Text("${i}x de ${String.format(localeBr, "R$ %.2f", valorOpcao / i)} ${if(i < 6) "sem juros" else "com juros"}") },
                                        onClick = { parcelaSelecionada = i; menuParcelasExpandido = false }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Dados do Cartão", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))

                        val infoCartao = identificarBandeira(numeroCartao)
                        val numeroValido = remember(numeroCartao) {
                            numeroCartao.isEmpty() || CardValidator.isNumeroValido(numeroCartao)
                        }

                        OutlinedTextField(
                            value = numeroCartao,
                            onValueChange = { input ->
                                val apenasDigitos = input.filter { it.isDigit() }
                                if (apenasDigitos.length <= 16) numeroCartao = apenasDigitos
                            },
                            label = { Text("Número do Cartão") },
                            leadingIcon = { Icon(infoCartao.second, null, tint = MaterialTheme.colorScheme.primary) },
                            isError = !numeroValido && numeroCartao.length >= 13,
                            supportingText = {
                                if (!numeroValido && numeroCartao.length >= 13) {
                                    Text("Número de cartão inválido", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            visualTransformation = CardNumberVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = nomeTitular,
                            onValueChange = { nomeTitular = it.uppercase() },
                            label = { Text("Nome do Titular (como no cartão)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            singleLine = true
                        )

                        val validadeValida = remember(validade) {
                            validade.isEmpty() || CardValidator.isValidadeValida(validade)
                        }

                        val cvvValido = remember(cvv) {
                            cvv.isEmpty() || CardValidator.isCvvValido(cvv)
                        }

                        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = validade,
                                onValueChange = { input ->
                                    val apenasDigitos = input.filter { it.isDigit() }
                                    if (apenasDigitos.length <= 4) validade = apenasDigitos
                                },
                                label = { Text("Validade (MM/AA)") },
                                isError = !validadeValida && validade.length == 4,
                                supportingText = {
                                    if (!validadeValida && validade.length == 4) {
                                        Text("Inválido/Vencido", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                visualTransformation = ExpiryDateVisualTransformation(),
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { input ->
                                    val apenasDigitos = input.filter { it.isDigit() }
                                    if (apenasDigitos.length <= 4) cvv = apenasDigitos
                                },
                                label = { Text("CVV") },
                                isError = !cvvValido && cvv.length in 1..2,
                                supportingText = {
                                    if (!cvvValido && cvv.isNotEmpty() && cvv.length < 3) {
                                        Text("Mín. 3 dígitos", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                }

                FormaPagamento.BOLETO -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Boleto Bancário", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Vencimento em 3 dias úteis. A confirmação de pagamento pode levar até 48h.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = "23793.38128 60087.003463 05000.633017 9 95110000019000",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Linha Digitável") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 6. Botão de Confirmação Final com Verificação de Segurança
            Button(
                onClick = {
                    if (formaSelecionada == FormaPagamento.CARTAO_CREDITO) {
                        if (!CardValidator.isNumeroValido(numeroCartao)) {
                            Toast.makeText(context, "Digite um número de cartão válido!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (nomeTitular.trim().length < 3) {
                            Toast.makeText(context, "Preencha o nome do titular completo!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!CardValidator.isValidadeValida(validade)) {
                            Toast.makeText(context, "A data de validade é inválida ou expirou!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!CardValidator.isCvvValido(cvv)) {
                            Toast.makeText(context, "Código de segurança (CVV) inválido!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }

                    onConfirmarPagamento(
                        valorDesconto,
                        valorFrete,
                        freteSelecionado?.diasUteis ?: 5
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "CONFIRMAR COMPRA",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



private fun identificarBandeira(numero: String): Pair<String, ImageVector> {
    return when {
        numero.startsWith("4") -> "VISA" to Icons.Default.CreditCard
        numero.startsWith("5") -> "MASTERCARD" to Icons.Default.CreditCard
        numero.startsWith("3") -> "AMEX" to Icons.Default.CreditCard
        else -> "CARTÃO" to Icons.Default.CreditCard
    }
}

@Composable
private fun PaymentSelector(
    selectedForma: FormaPagamento,
    onFormaSelected: (FormaPagamento) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Forma de Pagamento", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(12.dp))
        FormaPagamento.entries.forEach { forma ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFormaSelected(forma) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = forma.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = forma.displayName,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                RadioButton(
                    selected = (forma == selectedForma),
                    onClick = { onFormaSelected(forma) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}