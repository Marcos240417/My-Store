package com.example.mymercado.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.mymercado.core.data.ProdutoEntity
import java.util.Locale // CERTIFIQUE-SE DE USAR ESTE IMPORT PARA O LOCALE

@Composable
fun ProdutoCard(
    produto: ProdutoEntity,
    onProdutoClick: () -> Unit,
    onAdicionarCarrinho: () -> Unit,
    onFavoritarClick: () -> Unit
) {
    // Correção do Locale e Unresolved Reference 'Builder'
    val localeBr = remember {
        Locale.Builder().setLanguage("pt").setRegion("BR").build()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable { onProdutoClick() },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box {
                AsyncImage(
                    model = produto.urlImagem,
                    contentDescription = produto.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = onFavoritarClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (produto.isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (produto.isFavorito) Color(0xFFEE4D2D) else Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = produto.titulo,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.height(40.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = String.format(localeBr, "R$ %.2f", produto.preco),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFEE4D2D), // Laranja Shopee
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.weight(1f))

                // Correção Unresolved reference 'ButtonDefaults' (Geralmente falta de import ou erro de digitação)
                Button(
                    onClick = onAdicionarCarrinho,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE4D2D))
                ) {
                    Text("Adicionar", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}