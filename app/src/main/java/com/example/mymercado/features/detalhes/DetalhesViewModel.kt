package com.example.mymercado.features.detalhes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.common.AppConstants
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.data.datasource.local.entity.ProdutoEntity
import com.example.mymercado.domain.repository.CarrinhoRepository
import com.example.mymercado.domain.repository.ProdutoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalhesViewModel(
    private val produtoRepository: ProdutoRepository,
    private val carrinhoRepository: CarrinhoRepository
) : ViewModel() {

    private val _produto = MutableStateFlow<ProdutoEntity?>(null)
    val produto: StateFlow<ProdutoEntity?> = _produto.asStateFlow()

    fun carregarProduto(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                _produto.value = produtoRepository.buscarProdutoPorId(id)
            }
        }
    }

    fun adicionarAoCarrinho(produto: ProdutoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val nomeDaLoja = "${produto.categoria.replaceFirstChar { it.uppercase() }} Official"
                val item = CarrinhoEntity(
                    produtoId = produto.id, // Corrigido de produto.produtoId para produto.id
                    usuarioEmail = AppConstants.DEFAULT_USER_EMAIL,
                    quantidade = 1,
                    precoNoMomento = produto.preco,
                    titulo = produto.titulo,
                    urlImagem = produto.urlImagem,
                    vendedorNome = nomeDaLoja
                )
                carrinhoRepository.adicionarProdutoAoCarrinho(item)
            }
        }
    }
}