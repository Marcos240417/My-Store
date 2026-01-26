package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymercado.features.checkout.CheckoutViewModel

@Composable
fun SecaoCupom(
    viewModel: CheckoutViewModel,
    subtotal: Double
) {
    var textoCupom by remember { mutableStateOf("") }
    val cupomAtivo by viewModel.cupomAplicado.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Possui um cupom?", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoCupom,
                    onValueChange = { textoCupom = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("GALGA10") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.aplicarCupom(textoCupom, subtotal) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aplicar")
                }
            }

            if (cupomAtivo != null) {
                Text(
                    text = "Cupom $cupomAtivo ativado!",
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}