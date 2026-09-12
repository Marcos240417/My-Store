package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
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
            // RESOLVE: Tipografia padronizada em vez de peso manual isolado
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Filtramos as formas de pagamento (exceto cartão, conforme sua lógica)
            FormaPagamento.entries.filter { it != FormaPagamento.CARTAO_CREDITO }.forEach { forma ->
                val isSelected = forma == formaSelecionada

                FilterChip(
                    selected = isSelected,
                    onClick = { onFormaSelected(forma) },
                    // RESOLVE: Nome amigável e tipografia de label
                    label = {
                        Text(
                            text = forma.displayName, // Assumindo que você tem um displayName no Enum
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null,
                    // RESOLVE: Cores baseadas na cor primária (Laranja) do MyMercado
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}