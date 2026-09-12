package com.example.mymercado.features.carrinho

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.common.AppConstants
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.domain.repository.CarrinhoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CarrinhoViewModel(
    private val repository: CarrinhoRepository
) : ViewModel() {

    private val _itensRaw = repository.verCarrinho(AppConstants.DEFAULT_USER_EMAIL)
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selecionados = MutableStateFlow<Set<Int>>(emptySet())
    val selecionados = _selecionados.asStateFlow()

    val itensAgrupados = _itensRaw.map { lista ->
        lista.groupBy { it.vendedorNome }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val valorTotal: StateFlow<Double> = combine(_itensRaw, _selecionados) { itens, sel ->
        itens.filter { it.produtoId in sel }.sumOf { it.precoNoMomento * it.quantidade }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun alternarSelecao(id: Int) {
        _selecionados.update { atual ->
            if (id in atual) atual - id else atual + id
        }
    }

    fun selecionarTudo(selecionar: Boolean) {
        _selecionados.value = if (selecionar) {
            _itensRaw.value.map { it.produtoId }.toSet()
        } else {
            emptySet()
        }
    }

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
}