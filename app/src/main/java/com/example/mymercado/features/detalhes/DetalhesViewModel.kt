package com.example.mymercado.features.detalhes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalhesViewModel(private val repository: VendasRepository) : ViewModel() {

    private val _produto = MutableStateFlow<ProdutoEntity?>(null)
    val produto: StateFlow<ProdutoEntity?> = _produto.asStateFlow()

    fun carregarProduto(id: Int) {
        viewModelScope.launch {
            // Garanta que o nome da função na interface seja buscarProdutoPorId
            _produto.value = repository.buscarProdutoPorId(id)
        }
    }

    fun adicionarAoCarrinho(produto: ProdutoEntity) {
        viewModelScope.launch {
            repository.adicionarProdutoAoCarrinho(
                CarrinhoEntity(
                    produtoId = produto.produtoId,
                    usuarioEmail = "user@galga.com",
                    quantidade = 1,
                    precoNoMomento = produto.preco,
                    titulo = produto.titulo,
                    urlImagem = produto.urlImagem
                )
            )
        }
    }
}