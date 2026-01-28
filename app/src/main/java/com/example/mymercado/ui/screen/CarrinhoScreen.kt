package com.example.mymercado.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.features.carrinho.CarrinhoViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

val ShopeeOrange = Color(0xFFEE4D2D)
val ShopeeGrayBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(
    viewModel: CarrinhoViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToCheckout: (FormaPagamento) -> Unit
) {
    val grupos by viewModel.itensAgrupados.collectAsState()
    val total by viewModel.valorTotal.collectAsState()
    val selecionados by viewModel.selecionados.collectAsState()
    val localeBr = remember { Locale.forLanguageTag("pt-BR") }

    Scaffold(
        containerColor = ShopeeGrayBackground,
        topBar = {
            TopAppBar(
                title = { Text("Carrinho de Compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = { TextButton(onClick = { }) { Text("Editar", color = ShopeeOrange) } }
            )
        },
        bottomBar = {
            if (grupos.isNotEmpty()) {
                BottomCheckoutBar(total, selecionados.size, onNavigateToCheckout, localeBr)
            }
        }
    ) { padding ->
        if (grupos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Carrinho vazio", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                grupos.forEach { (vendedor, itens) ->
                    item {
                        StoreHeader(vendedor)
                    }
                    items(itens) { item ->
                        ShopeeItemRow(
                            item = item,
                            isSelected = item.produtoId in selecionados,
                            onToggle = { viewModel.alternarSelecao(item.produtoId) },
                            onIncrease = { viewModel.aumentarQuantidade(item) },
                            onDecrease = { viewModel.diminuirQuantidade(item) }
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun StoreHeader(vendedor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = false, onCheckedChange = { }, colors = CheckboxDefaults.colors(ShopeeOrange))
        Icon(Icons.Default.Storefront, null, modifier = Modifier.size(18.dp))
        Text(vendedor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp), fontSize = 14.sp)
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}

@Composable
fun ShopeeItemRow(
    item: CarrinhoEntity,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isSelected, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(ShopeeOrange))

        AsyncImage(
            model = item.urlImagem,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Fit
        )

        Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
            Text(item.titulo, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text("Variação: Padrão", fontSize = 11.sp, color = Color.Gray)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("R$ ${String.format("%.2f", item.precoNoMomento)}", color = ShopeeOrange, fontWeight = FontWeight.Bold)

                // Seletor de quantidade compacto
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color.White)) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(24.dp)) { Text("-") }
                    Text("${item.quantidade}", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 13.sp)
                    IconButton(onClick = onIncrease, modifier = Modifier.size(24.dp)) { Text("+") }
                }
            }
        }
    }
}

@Composable
fun BottomCheckoutBar(total: Double, count: Int, onNavigate: (FormaPagamento) -> Unit, locale: Locale) {
    Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = false, onCheckedChange = { }, colors = CheckboxDefaults.colors(ShopeeOrange))
                Text("Tudo", fontSize = 12.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Total", fontSize = 12.sp)
                    Text(String.format(locale, "R$ %.2f", total), color = ShopeeOrange, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onNavigate(FormaPagamento.PIX) },
                    colors = ButtonDefaults.buttonColors(ShopeeOrange),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Continuar ($count)")
                }
            }
        }
    }
}