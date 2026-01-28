package com.example.mymercado.features.detalhes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalhesViewModel(private val repository: VendasRepository) : ViewModel() {

    private val _produto = MutableStateFlow<ProdutoEntity?>(null)
    val produto: StateFlow<ProdutoEntity?> = _produto.asStateFlow()

    fun carregarProduto(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _produto.value = repository.buscarProdutoPorId(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun adicionarAoCarrinho(produto: ProdutoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // CORREÇÃO: Passando o vendedorNome para bater com a nova Entity
                // Usamos a categoria como nome da loja ou um nome fixo para simular a Shopee
                val nomeDaLoja = produto.categoria.replaceFirstChar { it.uppercase() } + " Official"

                repository.adicionarProdutoAoCarrinho(
                    CarrinhoEntity(
                        produtoId = produto.produtoId,
                        usuarioEmail = "user@galga.com",
                        quantidade = 1,
                        precoNoMomento = produto.preco,
                        titulo = produto.titulo,
                        urlImagem = produto.urlImagem,
                        vendedorNome = nomeDaLoja // Agora o parâmetro está aqui!
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}