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

    // Itens brutos do banco
    private val _itensRaw = repository.verCarrinho(userEmail)
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Itens selecionados para o checkout (IDs)
    private val _selecionados = MutableStateFlow<Set<Int>>(emptySet())
    val selecionados = _selecionados.asStateFlow()

    // Agrupamento por vendedor (Padrão Shopee)
    val itensAgrupados = _itensRaw.map { lista ->
        lista.groupBy { it.vendedorNome }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Valor total apenas dos itens marcados com Checkbox
    val valorTotal: StateFlow<Double> = combine(_itensRaw, _selecionados) { itens, sel ->
        itens.filter { it.produtoId in sel }.sumOf { it.precoNoMomento * it.quantidade }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun alternarSelecao(id: Int) {
        _selecionados.update { atual ->
            if (id in atual) atual - id else atual + id
        }
    }

    fun selecionarTudo(selecionar: Boolean) {
        _selecionados.value = if (selecionar) _itensRaw.value.map { it.produtoId }.toSet() else emptySet()
    }

    fun aumentarQuantidade(item: CarrinhoEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.aumentarQuantidade(item) }
    }

    fun diminuirQuantidade(item: CarrinhoEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.diminuirQuantidade(item) }
    }

    fun removerItem(item: CarrinhoEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.removerProdutoDoCarrinho(item) }
    }
}