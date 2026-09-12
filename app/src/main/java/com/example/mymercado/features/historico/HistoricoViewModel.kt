package com.example.mymercado.features.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.common.AppConstants
import com.example.mymercado.core.datastore.SessionManager
import com.example.mymercado.data.datasource.local.entity.PedidoEntity
import com.example.mymercado.domain.repository.PedidoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HistoricoViewModel(
    private val repository: PedidoRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val emailUsuario: StateFlow<String> = sessionManager.usuarioEmailFlow
        .map { it ?: AppConstants.DEFAULT_USER_EMAIL }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppConstants.DEFAULT_USER_EMAIL)

    val pedidos: StateFlow<List<PedidoEntity>> = emailUsuario
        .flatMapLatest { email -> repository.obterPedidosPorUsuario(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun confirmarEntregaItem(pedidoId: Int, produtoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.confirmarEntregaItem(pedidoId, produtoId)
            }
        }
    }
}