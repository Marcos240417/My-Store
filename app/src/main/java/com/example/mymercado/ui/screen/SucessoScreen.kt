package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.outlinedButtonBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucessoScreen(
    onVoltarParaHome: () -> Unit,
    onIrParaPedidos: () -> Unit
) {
    Scaffold(containerColor = Color.White) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CheckCircle, "Sucesso", Modifier.size(120.dp), Color(0xFF2E7D32))
            Spacer(Modifier.height(24.dp))
            Text("Pedido Realizado!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Text(
                "Seu pagamento foi confirmado.\nA loja já está preparando seu envio!",
                style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp), lineHeight = 20.sp
            )
            Spacer(Modifier.height(64.dp))
            OutlinedButton(
                onClick = onIrParaPedidos,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true) // Versão correta
            ) {
                Text("VER MEUS PEDIDOS", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onVoltarParaHome,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE4D2D))
            ) {
                Text("CONTINUAR COMPRANDO", fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }
    }
}
