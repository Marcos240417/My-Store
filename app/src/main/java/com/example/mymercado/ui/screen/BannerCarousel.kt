package com.example.mymercado.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mymercado.R
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel() {
    // Lista vinculada às imagens que você salvou na pasta drawable
    val banners = listOf(
        R.drawable.banner_tecnologia,
        R.drawable.banner_aniversario,
        R.drawable.banner_moedas,
        R.drawable.banner_frete,
        R.drawable.banner_oferta
    )

    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Auto-scroll: Muda de imagem a cada 4 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            if (pagerState.pageCount > 0) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Altura padrão Shopee
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = banners[page]),
                contentDescription = "Promoção Galga",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds // Garante que preencha todo o banner
            )
        }

        // Indicadores (Dots) na parte inferior centralizada
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFFEE4D2D) else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}