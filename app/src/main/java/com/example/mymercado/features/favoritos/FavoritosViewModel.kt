package com.example.mymercado.features.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class FavoritosViewModel(private val repository: VendasRepository) : ViewModel() {

    val produtosFavoritos: StateFlow<List<ProdutoEntity>> = repository.listarFavoritos()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removerDosFavoritos(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.alternarFavorito(id, false)
        }
    }
}