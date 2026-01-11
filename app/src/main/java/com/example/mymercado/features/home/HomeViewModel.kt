package com.example.mymercado.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: VendasRepository) : ViewModel() {

    private val _produtos = MutableStateFlow<List<ProdutoEntity>>(emptyList())
    val produtos: StateFlow<List<ProdutoEntity>> = _produtos.asStateFlow()

    private val _estaCarregando = MutableStateFlow(false)
    val estaCarregando: StateFlow<Boolean> = _estaCarregando.asStateFlow()

    // Observa o carrinho do usuário padrão
    val itensCarrinho: StateFlow<List<CarrinhoEntity>> = repository.verCarrinho("user@galga.com")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observarProdutos()
        sincronizarDados()
    }

    private fun observarProdutos() {
        viewModelScope.launch {
            repository.listarProdutos().collectLatest { _produtos.value = it }
        }
    }

    private fun sincronizarDados() {
        viewModelScope.launch {
            _estaCarregando.value = true
            try { repository.sincronizarProdutos() } catch (e: Exception) { e.printStackTrace() }
            finally { _estaCarregando.value = false }
        }
    }

    fun filtrarPorCategoria(categoria: String) {
        viewModelScope.launch {
            val fluxo = if (categoria == "Todos") repository.listarProdutos()
            else repository.buscarProdutosPorCategoria(categoria)
            fluxo.collectLatest { _produtos.value = it }
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