package com.example.mymercado.features.carrinho

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarrinhoViewModel(private val repository: VendasRepository) : ViewModel() {

    private val userEmail = "user@galga.com"

    // Usamos stateIn para transformar o Flow do Room em um estado estável para o Compose
    val itensCarrinho: StateFlow<List<CarrinhoEntity>> = repository.verCarrinho(userEmail)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val valorTotal: StateFlow<Double> = itensCarrinho.map { lista ->
        lista.sumOf { it.precoNoMomento * it.quantidade }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun removerItem(item: CarrinhoEntity) {
        viewModelScope.launch {
            repository.removerProdutoDoCarrinho(item)
        }
    }

    fun finalizarCompra() {
        viewModelScope.launch {
            repository.esvaziarCarrinho(userEmail)
        }
    }
}