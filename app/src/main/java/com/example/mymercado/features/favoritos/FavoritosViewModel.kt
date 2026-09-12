package com.example.mymercado.features.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.common.AppConstants
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.data.datasource.local.entity.ProdutoEntity
import com.example.mymercado.domain.repository.CarrinhoRepository
import com.example.mymercado.domain.repository.ProdutoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritosViewModel(
    private val produtoRepository: ProdutoRepository,
    private val carrinhoRepository: CarrinhoRepository
) : ViewModel() {

    val produtosFavoritos: StateFlow<List<ProdutoEntity>> = produtoRepository.listarFavoritos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun adicionarAoCarrinho(produto: ProdutoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val nomeLoja = "${produto.categoria.replaceFirstChar { it.uppercase() }} Store"
                val itemCarrinho = CarrinhoEntity(
                    produtoId = produto.id, // Corrigido de produto.produtoId para produto.id
                    usuarioEmail = AppConstants.DEFAULT_USER_EMAIL,
                    titulo = produto.titulo,
                    precoNoMomento = produto.preco,
                    urlImagem = produto.urlImagem,
                    quantidade = 1,
                    vendedorNome = nomeLoja
                )
                carrinhoRepository.adicionarProdutoAoCarrinho(itemCarrinho)
            }
        }
    }

    fun removerDosFavoritos(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            produtoRepository.alternarFavorito(id, false)
        }
    }
}