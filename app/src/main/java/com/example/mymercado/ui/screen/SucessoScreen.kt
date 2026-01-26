package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SucessoScreen(onVoltarParaHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Se você não tiver o Lottie configurado, use um texto simples por enquanto:
        Text("🎉", style = MaterialTheme.typography.displayLarge)
        Text(
            "Compra Confirmada!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVoltarParaHome, modifier = Modifier.fillMaxWidth()) {
            Text("VOLTAR AO INÍCIO")
        }
    }
}