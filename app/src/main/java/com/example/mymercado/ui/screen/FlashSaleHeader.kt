package com.example.mymercado.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FlashSaleHeader() {
    Row(Modifier.fillMaxWidth().padding(12.dp).background(Color.White), verticalAlignment = Alignment.CenterVertically) {
        Text("OFERTA RELÂMPAGO", color = Color(0xFFEE4D2D), fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.width(8.dp))
        // Simulando o cronômetro
        Box(Modifier.background(Color.Black).padding(2.dp)) { Text("02", color = Color.White) }
        Text(" : ")
        Box(Modifier.background(Color.Black).padding(2.dp)) { Text("45", color = Color.White) }
    }
}