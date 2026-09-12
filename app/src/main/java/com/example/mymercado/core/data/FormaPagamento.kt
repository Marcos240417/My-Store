package com.example.mymercado.core.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector
enum class FormaPagamento(
    val displayName: String,
    val icon: ImageVector
) {
    PIX("Pix (Aprovação imediata)", Icons.Default.QrCodeScanner),
    CARTAO_CREDITO("Cartão de Crédito", Icons.Default.CreditCard),
    BOLETO("Boleto Bancário", Icons.Default.Style)
}