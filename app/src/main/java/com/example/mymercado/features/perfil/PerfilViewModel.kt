package com.example.mymercado.features.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.UsuarioEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: VendasRepository) : ViewModel() {

    // Sempre observa o e-mail fixo. Quando o banco mudar, o StateFlow atualiza a UI.
    val usuario: StateFlow<UsuarioEntity?> = repository.obterUsuarioPorEmail("user@galga.com")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun excluirConta() {
        viewModelScope.launch {
            repository.deletarContaUsuario("user@galga.com")
        }
    }
}