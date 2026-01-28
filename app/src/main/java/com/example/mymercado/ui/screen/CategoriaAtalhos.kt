package com.example.mymercado.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoriaAtalhos() {
    val itens = listOf("Ofertas", "Moedas", "Cupons", "Frete Grátis", "Eletrônicos")
    LazyRow(Modifier.padding(8.dp)) {
        items(itens) { item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                Box(Modifier.size(50.dp).background(Color(0xFFFFE0D8), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFEE4D2D))
                }
                Text(item, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}