package com.example.mymercado.features.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.PedidoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoricoViewModel(private val repository: VendasRepository) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<PedidoEntity>>(emptyList())
    val pedidos = _pedidos.asStateFlow()

    init {
        carregarHistorico()
    }

    private fun carregarHistorico() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.obterPedidosPorUsuario("user@galga.com")
                    .collect { lista ->
                        _pedidos.value = lista
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}