package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        // RESOLVE: Superfície variante que se ajusta ao tema Light/Dark
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // RESOLVE: Tipografia padronizada
            Text(
                text = "Possui um cupom?",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoCupom,
                    onValueChange = { textoCupom = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("GALGA10", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.aplicarCupom(textoCupom, subtotal) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Aplicar", style = MaterialTheme.typography.labelLarge)
                }
            }

            if (cupomAtivo != null) {
                // RESOLVE: Cor de sucesso baseada na paleta do Material 3
                Text(
                    text = "Cupom $cupomAtivo ativado!",
                    color = Color(0xFF2E7D32), // Mantido tom de sucesso Shopee
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}