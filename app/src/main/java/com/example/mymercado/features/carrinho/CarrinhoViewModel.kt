package com.example.mymercado.features.carrinho

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CarrinhoViewModel(private val repository: VendasRepository) : ViewModel() {

    private val userEmail = "user@galga.com"

    val itensCarrinho: StateFlow<List<CarrinhoEntity>> = repository.verCarrinho(userEmail)
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val valorTotal: StateFlow<Double> = itensCarrinho.map { lista ->
        lista.sumOf { item -> item.precoNoMomento * item.quantidade }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun aumentarQuantidade(item: CarrinhoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.aumentarQuantidade(item)
        }
    }

    fun diminuirQuantidade(item: CarrinhoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.diminuirQuantidade(item)
        }
    }

    fun removerItem(item: CarrinhoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removerProdutoDoCarrinho(item)
        }
    }

    fun finalizarCompra() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.esvaziarCarrinho(userEmail)
        }
    }
}