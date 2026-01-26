package com.example.mymercado.core.data

enum class FormaPagamento(val nome: String) {
    BOLETO("Boleto Bancário"),
    QR_CODE("QR Code"),
    CARTAO_CREDITO("Cartão de Crédito"),
    PIX("PIX")
}