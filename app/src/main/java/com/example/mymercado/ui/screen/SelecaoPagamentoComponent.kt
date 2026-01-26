package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymercado.core.data.FormaPagamento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelecaoPagamentoComponent(
    formaSelecionada: FormaPagamento,
    onFormaSelected: (FormaPagamento) -> Unit
) {
    Column {
        Text(
            text = "Forma de Pagamento Preferida",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Iteramos sobre os enums (exceto cartão, se for o caso)
            FormaPagamento.entries.filter { it != FormaPagamento.CARTAO_CREDITO }.forEach { forma ->
                FilterChip(
                    selected = (forma == formaSelecionada),
                    onClick = { onFormaSelected(forma) },
                    label = { Text(forma.name) },
                    leadingIcon = if (forma == formaSelecionada) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}