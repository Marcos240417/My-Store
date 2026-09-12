package com.example.mymercado.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.common.AppConstants
import com.example.mymercado.data.datasource.local.entity.CarrinhoEntity
import com.example.mymercado.data.datasource.local.entity.ProdutoEntity
import com.example.mymercado.domain.repository.CarrinhoRepository
import com.example.mymercado.domain.repository.ProdutoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val produtoRepository: ProdutoRepository,
    private val carrinhoRepository: CarrinhoRepository
) : ViewModel() {

    private val _textoBusca = MutableStateFlow("")
    val textoBusca = _textoBusca.asStateFlow()

    private val _categoriaAtiva = MutableStateFlow("Todos")
    val categoriaAtiva = _categoriaAtiva.asStateFlow()

    private val _apenasFreteGratis = MutableStateFlow(false)
    val apenasFreteGratis = _apenasFreteGratis.asStateFlow()

    private val _estaCarregando = MutableStateFlow(false)
    val estaCarregando = _estaCarregando.asStateFlow()

    private val _mostrarAvisoOffline = MutableStateFlow(false)
    val mostrarAvisoOffline = _mostrarAvisoOffline.asStateFlow()

    init {
        sincronizar()
    }

    fun sincronizar() {
        viewModelScope.launch(Dispatchers.IO) {
            _estaCarregando.value = true
            _mostrarAvisoOffline.value = false
            runCatching {
                produtoRepository.sincronizarProdutos()
            }.onFailure {
                _mostrarAvisoOffline.value = true
            }.also {
                _estaCarregando.value = false
            }
        }
    }

    val itensCarrinho: StateFlow<List<CarrinhoEntity>> = carrinhoRepository.verCarrinho(AppConstants.DEFAULT_USER_EMAIL)
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val produtos: StateFlow<List<ProdutoEntity>> = combine(
        produtoRepository.listarProdutos(),
        _textoBusca,
        _categoriaAtiva,
        _apenasFreteGratis
    ) { lista, busca, categoria, apenasFrete ->
        lista
            .distinctBy { it.id }
            .filter { produto ->
                val matchCat = categoria == "Todos" || produto.categoria.equals(categoria, ignoreCase = true)
                // Se o campo de busca estiver vazio/em branco, não filtra por título
                val matchBusca = busca.isBlank() || produto.titulo.contains(busca.trim(), ignoreCase = true)
                val matchFrete = !apenasFrete || produto.freteGratis

                matchCat && matchBusca && matchFrete
            }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun atualizarBusca(t: String) {
        _apenasFreteGratis.value = false
        _textoBusca.value = t
    }

    fun atualizarCategoria(c: String) {
        _apenasFreteGratis.value = false
        _categoriaAtiva.value = c
    }

    fun filtrarFreteGratis() {
        _apenasFreteGratis.value = true
        _categoriaAtiva.value = "Todos"
        _textoBusca.value = ""
    }

    fun filtrarOfertasRelampago() {
        _apenasFreteGratis.value = false
        _categoriaAtiva.value = "Todos"
        _textoBusca.value = ""
    }

    fun adicionarAoCarrinho(produto: ProdutoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val nomeLoja = "${produto.categoria.replaceFirstChar { it.uppercase() }} Store"
                carrinhoRepository.adicionarProdutoAoCarrinho(
                    CarrinhoEntity(
                        produtoId = produto.id,
                        usuarioEmail = AppConstants.DEFAULT_USER_EMAIL,
                        titulo = produto.titulo,
                        precoNoMomento = produto.preco,
                        urlImagem = produto.urlImagem,
                        quantidade = 1,
                        vendedorNome = nomeLoja
                    )
                )
            }
        }
    }

    fun alternarFavorito(id: Int, isFav: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            produtoRepository.alternarFavorito(id, isFav)
        }
    }
}