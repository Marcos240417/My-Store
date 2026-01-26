package com.example.mymercado.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: VendasRepository) : ViewModel() {

    private val _textoBusca = MutableStateFlow("")
    val textoBusca = _textoBusca.asStateFlow()

    private val _categoriaAtiva = MutableStateFlow("Todos")
    val categoriaAtiva = _categoriaAtiva.asStateFlow()

    private val _estaCarregando = MutableStateFlow(false)
    val estaCarregando = _estaCarregando.asStateFlow()

    // Estado para controle do Modo Offline
    private val _mostrarAvisoOffline = MutableStateFlow(false)
    val mostrarAvisoOffline = _mostrarAvisoOffline.asStateFlow()

    init {
        sincronizar()
    }

    // Função atualizada para lidar com erros de conexão
    fun sincronizar() {
        viewModelScope.launch {
            _estaCarregando.value = true
            _mostrarAvisoOffline.value = false
            try {
                repository.sincronizarProdutos()
                _mostrarAvisoOffline.value = false
            } catch (e: Exception) {
                _mostrarAvisoOffline.value = true
                e.printStackTrace()
            } finally {
                _estaCarregando.value = false
            }
        }
    }

    val itensCarrinho: StateFlow<List<CarrinhoEntity>> = repository.verCarrinho("user@galga.com")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritos: StateFlow<List<ProdutoEntity>> = repository.listarFavoritos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val produtos: StateFlow<List<ProdutoEntity>> = combine(
        repository.listarProdutos(), _textoBusca, _categoriaAtiva
    ) { lista, busca, categoria ->
        lista
            .distinctBy { it.produtoId }
            .filter {
                val matchCat = categoria == "Todos" || it.categoria.equals(categoria, ignoreCase = true)
                val matchBusca = it.titulo.contains(busca, ignoreCase = true)
                matchCat && matchBusca
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun atualizarBusca(t: String) { _textoBusca.value = t }
    fun atualizarCategoria(c: String) { _categoriaAtiva.value = c }

    fun adicionarAoCarrinho(produto: ProdutoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.adicionarProdutoAoCarrinho(
                CarrinhoEntity(
                    produtoId = produto.produtoId,
                    usuarioEmail = "user@galga.com",
                    titulo = produto.titulo,
                    precoNoMomento = produto.preco,
                    urlImagem = produto.urlImagem,
                    quantidade = 1
                )
            )
        }
    }

    fun alternarFavorito(id: Int, isFav: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { repository.alternarFavorito(id, isFav) }
    }
}