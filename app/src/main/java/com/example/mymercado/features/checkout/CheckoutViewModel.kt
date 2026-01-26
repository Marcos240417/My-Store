package com.example.mymercado.features.checkout

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.core.data.PedidoEntity
import com.example.mymercado.domain.repository.VendasRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

sealed class PagamentoStatus {
    object Idle : PagamentoStatus()
    object Carregando : PagamentoStatus()
    data class PixGerado(val chave: String, val qrCode: Bitmap?) : PagamentoStatus()
    data class BoletoGerado(val codigo: String) : PagamentoStatus()
    data class CartaoOpcoes(val parcelas: List<String>) : PagamentoStatus()
}

class CheckoutViewModel(private val repository: VendasRepository) : ViewModel() {

    private val _status = MutableStateFlow<PagamentoStatus>(PagamentoStatus.Idle)
    val status = _status.asStateFlow()

    private val _cupomAplicado = MutableStateFlow<String?>(null)
    val cupomAplicado = _cupomAplicado.asStateFlow()

    private val _desconto = MutableStateFlow(0.0)
    val desconto = _desconto.asStateFlow()

    private val localeBr = Locale.Builder().setLanguage("pt").setRegion("BR").build()

    // Mock de cupons válidos
    private val cuponsValidos = mapOf(
        "GALGA10" to 0.10,
        "BEMVINDO" to 0.15
    )

    fun aplicarCupom(codigo: String, subtotal: Double) {
        val codigoUpper = codigo.uppercase().trim()
        val percentual = cuponsValidos[codigoUpper]

        if (percentual != null) {
            _cupomAplicado.value = codigoUpper
            _desconto.value = subtotal * percentual
        } else {
            _cupomAplicado.value = null
            _desconto.value = 0.0
        }
    }

    fun carregarDetalhesPagamento(forma: FormaPagamento, totalComDesconto: Double) {
        viewModelScope.launch(Dispatchers.Default) {
            _status.value = PagamentoStatus.Carregando
            delay(600)

            _status.value = when (forma) {
                FormaPagamento.PIX -> {
                    val chavePix = "00020126330014BR.GOV.BCB.PIX011112345678901..."
                    val bitmap = gerarBitmapQrCode(chavePix)
                    PagamentoStatus.PixGerado(chavePix, bitmap)
                }
                FormaPagamento.BOLETO -> {
                    PagamentoStatus.BoletoGerado("23793.38128 60087.003463 05000.633017 9 95110000019000")
                }
                FormaPagamento.CARTAO_CREDITO -> {
                    val parcelas = (1..12).map { i ->
                        "$i x R$ ${String.format(localeBr, "%.2f", totalComDesconto / i)}"
                    }
                    PagamentoStatus.CartaoOpcoes(parcelas)
                }
                else -> { PagamentoStatus.Idle }
            }
        }
    }

    private fun gerarBitmapQrCode(conteudo: String): Bitmap? {
        if (conteudo.isEmpty()) { return null }
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(conteudo, BarcodeFormat.QR_CODE, 512, 512)
            val bitmap = createBitmap(512, 512, Bitmap.Config.RGB_565)
            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }
            bitmap
        } catch (e: Exception) { null }
    }

    fun confirmarPagamento(
        usuarioEmail: String,
        formaPagamento: String,
        itens: List<CarrinhoEntity>,
        valorDesconto: Double,
        onSucesso: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val subtotal = itens.sumOf { it.precoNoMomento * it.quantidade }
                val totalFinal = subtotal - valorDesconto

                val novoPedido = PedidoEntity(
                    usuarioEmail = usuarioEmail,
                    total = totalFinal,
                    formaPagamento = formaPagamento,
                    itensResumo = itens.joinToString { "${it.quantidade}x ${it.titulo}" }
                )

                repository.finalizarPedido(novoPedido)
                repository.esvaziarCarrinho(usuarioEmail)

                withContext(Dispatchers.Main) {
                    onSucesso()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}