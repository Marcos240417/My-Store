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
import com.google.gson.Gson
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

    private val _desconto = MutableStateFlow(0.0) // Tipo Double inferido automaticamente
    val desconto = _desconto.asStateFlow()

    private val localeBr = Locale.Builder().setLanguage("pt").setRegion("BR").build()

    fun aplicarCupom(codigo: String, subtotal: Double) {
        val cuponsValidos = mapOf("GALGA10" to 0.10, "BEMVINDO" to 0.15)
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

    fun carregarDetalhesPagamento(forma: FormaPagamento, totalFinal: Double) {
        viewModelScope.launch(Dispatchers.Default) {
            _status.value = PagamentoStatus.Carregando
            delay(600)
            _status.value = when (forma) {
                FormaPagamento.PIX -> {
                    val chave = "00020126330014BR.GOV.BCB.PIX011112345678901..."
                    PagamentoStatus.PixGerado(chave, gerarBitmapQrCode(chave))
                }
                FormaPagamento.BOLETO -> {
                    PagamentoStatus.BoletoGerado("23793.38128 60087.003463 05000.633017 9 95110000019000")
                }
                FormaPagamento.CARTAO_CREDITO -> {
                    val parcelas = (1..12).map { "$it x R$ ${String.format(localeBr, "%.2f", totalFinal / it)}" }
                    PagamentoStatus.CartaoOpcoes(parcelas)
                }
                else -> PagamentoStatus.Idle
            }
        }
    }

    fun confirmarPagamento(
        usuarioEmail: String,
        formaPagamento: String,
        itensNoCarrinho: List<CarrinhoEntity>,
        valorDesconto: Double,
        onSucesso: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val totalFinal = itensNoCarrinho.sumOf { it.precoNoMomento * it.quantidade } - valorDesconto
                val jsonItens = Gson().toJson(itensNoCarrinho)

                val pedido = PedidoEntity(
                    usuarioEmail = usuarioEmail,
                    total = totalFinal,
                    formaPagamento = formaPagamento,
                    itensResumo = jsonItens
                )

                repository.finalizarPedido(pedido)
                repository.esvaziarCarrinho(usuarioEmail)

                withContext(Dispatchers.Main) { onSucesso() }
            } catch (e: Exception) {
                android.util.Log.e("CHECKOUT_ERROR", "Erro ao confirmar pagamento", e)
            }
        }
    }

    private fun gerarBitmapQrCode(conteudo: String): Bitmap? {
        return try {
            val bitMatrix = QRCodeWriter().encode(conteudo, BarcodeFormat.QR_CODE, 512, 512)
            val bitmap = createBitmap(512, 512, Bitmap.Config.RGB_565)
            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }
            bitmap
        } catch (_: Exception) { null }
    }
}