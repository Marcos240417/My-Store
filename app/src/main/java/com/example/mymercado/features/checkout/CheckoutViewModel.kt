package com.example.mymercado.features.checkout

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.core.util.FreteCalculadora
import com.example.mymercado.core.util.OpcaoFrete
import com.example.mymercado.core.util.PixPayloadGenerator
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.data.datasource.local.entity.PedidoEntity
import com.example.mymercado.domain.repository.CarrinhoRepository
import com.example.mymercado.domain.repository.PedidoRepository
import com.example.mymercado.domain.repository.ProdutoRepository
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

sealed class PagamentoStatus {
    data object Idle : PagamentoStatus()
    data object Carregando : PagamentoStatus()
    data class PixGerado(val chave: String, val qrCode: Bitmap?) : PagamentoStatus()
    data class BoletoGerado(val codigo: String) : PagamentoStatus()
    data class CartaoOpcoes(val parcelas: List<String>) : PagamentoStatus()
}

class CheckoutViewModel(
    private val pedidoRepository: PedidoRepository,
    private val carrinhoRepository: CarrinhoRepository,
    private val produtoRepository: ProdutoRepository
) : ViewModel() {

    private val _status: MutableStateFlow<PagamentoStatus> = MutableStateFlow(PagamentoStatus.Idle)
    val status: StateFlow<PagamentoStatus> = _status.asStateFlow()

    private val _cupomAplicado: MutableStateFlow<String?> = MutableStateFlow(null)
    val cupomAplicado: StateFlow<String?> = _cupomAplicado.asStateFlow()

    private val _desconto: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val desconto: StateFlow<Double> = _desconto.asStateFlow()

    // --- FRETE ---
    private val _cepDigitado = MutableStateFlow("54705000")
    val cepDigitado: StateFlow<String> = _cepDigitado.asStateFlow()

    private val _opcoesFrete = MutableStateFlow<List<OpcaoFrete>>(emptyList())
    val opcoesFrete: StateFlow<List<OpcaoFrete>> = _opcoesFrete.asStateFlow()

    private val _opcaoFreteSelecionada = MutableStateFlow<OpcaoFrete?>(null)
    val opcaoFreteSelecionada: StateFlow<OpcaoFrete?> = _opcaoFreteSelecionada.asStateFlow()

    private val localeBr = Locale.forLanguageTag("pt-BR")

    fun onCepAlterado(novoCep: String, subtotal: Double) {
        val apenasDigitos = novoCep.filter { it.isDigit() }.take(8)
        _cepDigitado.value = apenasDigitos

        if (apenasDigitos.length == 8) {
            val opcoes = FreteCalculadora.calcularOpcoes(apenasDigitos, subtotal)
            _opcoesFrete.value = opcoes
            _opcaoFreteSelecionada.value = opcoes.firstOrNull()
        } else {
            _opcoesFrete.value = emptyList()
            _opcaoFreteSelecionada.value = null
        }
    }

    fun selecionarOpcaoFrete(opcao: OpcaoFrete) {
        _opcaoFreteSelecionada.value = opcao
    }

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
            delay(400.milliseconds)
            _status.value = when (forma) {
                FormaPagamento.PIX -> {
                    val pixPayload = PixPayloadGenerator.gerarPayload(
                        chavePix = "suporte@galga.com",
                        beneficiario = "MY MERCADO",
                        cidade = "RECIFE",
                        valor = totalFinal
                    )
                    PagamentoStatus.PixGerado(
                        chave = pixPayload,
                        qrCode = gerarBitmapQrCode(pixPayload)
                    )
                }
                FormaPagamento.BOLETO -> {
                    PagamentoStatus.BoletoGerado("23793.38128 60087.003463 05000.633017 9 95110000019000")
                }
                FormaPagamento.CARTAO_CREDITO -> {
                    val parcelas = (1..12).map { i ->
                        "$i x R$ ${String.format(localeBr, "%.2f", totalFinal / i)}"
                    }
                    PagamentoStatus.CartaoOpcoes(parcelas)
                }
            }
        }
    }

    fun confirmarPagamento(
        usuarioEmail: String,
        formaPagamento: String,
        itensNoCarrinho: List<CarrinhoEntity>,
        valorDesconto: Double,
        valorFrete: Double,
        diasEntrega: Int,
        onSucesso: () -> Unit
    ) {
        if (itensNoCarrinho.isEmpty()) {
            Log.e("CHECKOUT_ERROR", "Lista de itens no carrinho está vazia! O pedido não pode ser criado.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val subtotal = itensNoCarrinho.sumOf { it.precoNoMomento * it.quantidade }
                val totalCalculado = (subtotal + valorFrete - valorDesconto).coerceAtLeast(0.0)
                val jsonItens = Gson().toJson(itensNoCarrinho)
                val dataEntrega = System.currentTimeMillis() + (diasEntrega.toLong() * 24 * 60 * 60 * 1000)

                val pedido = PedidoEntity(
                    usuarioEmail = usuarioEmail,
                    total = totalCalculado,
                    formaPagamento = formaPagamento,
                    itensResumo = jsonItens,
                    urlImagem = itensNoCarrinho.firstOrNull()?.urlImagem ?: "",
                    dataPedido = System.currentTimeMillis(),
                    quantidadeTotal = itensNoCarrinho.sumOf { it.quantidade },
                    dataEntregaEstimada = dataEntrega,
                    status = "A_CAMINHO"
                )

                pedidoRepository.finalizarPedido(pedido)
                carrinhoRepository.esvaziarCarrinho(usuarioEmail)

                itensNoCarrinho.forEach { item ->
                    produtoRepository.removerDosFavoritos(item.produtoId, usuarioEmail)
                }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    onSucesso()
                }
            }.onFailure { throwable ->
                Log.e("CHECKOUT_ERROR", "Falha ao gravar pedido no Room: ${throwable.message}", throwable)
            }
        }
    }

    private fun gerarBitmapQrCode(conteudo: String): Bitmap? {
        return runCatching {
            val bitMatrix = QRCodeWriter().encode(conteudo, BarcodeFormat.QR_CODE, 512, 512)
            createBitmap(512, 512, Bitmap.Config.RGB_565).apply {
                for (x in 0 until 512) {
                    for (y in 0 until 512) {
                        this[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                    }
                }
            }
        }.getOrNull()
    }
}