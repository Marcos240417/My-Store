package com.example.mymercado.domain.repository

import com.example.mymercado.core.dao.CarrinhoDao
import com.example.mymercado.core.dao.PedidoDao
import com.example.mymercado.core.dao.ProdutoDao
import com.example.mymercado.core.dao.UsuarioDao
import com.example.mymercado.core.dao.VendedorDao
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.PedidoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.core.data.UsuarioEntity
import com.example.mymercado.core.data.VendedorEntity
import com.example.mymercado.core.data.remotestorefake.FakeStoreService
import com.example.mymercado.core.data.remotestorefake.toProdutoEntity
import com.example.mymercado.core.data.remoteviacep.EnderecoDto
import com.example.mymercado.core.data.remoteviacep.ViaCepService
import kotlinx.coroutines.flow.Flow

class VendasRepositoryImpl(
    private val produtoDao: ProdutoDao,
    private val carrinhoDao: CarrinhoDao,
    private val usuarioDao: UsuarioDao,
    private val vendedorDao: VendedorDao,
    private val pedidoDao: PedidoDao,
    private val api: FakeStoreService,
    private val viaCepService: ViaCepService
) : VendasRepository {

    // --- PRODUTOS ---
    override fun listarProdutos(): Flow<List<ProdutoEntity>> {
        return produtoDao.listarTodos()
    }

    override fun buscarProdutosPorCategoria(categoria: String): Flow<List<ProdutoEntity>> {
        return produtoDao.buscarPorCategoria(categoria)
    }

    override suspend fun buscarProdutoPorId(id: Int): ProdutoEntity? {
        return produtoDao.buscarPorId(id)
    }

    override suspend fun inserirProduto(produto: ProdutoEntity) {
        produtoDao.inserir(produto)
    }

    override suspend fun sincronizarProdutos() {
        try {
            val produtosDto = api.getProdutos()
            // Filtra IDs duplicados antes de inserir no banco para evitar crash na UI
            produtosDto.distinctBy { it.id }.forEach { dto ->
                produtoDao.inserir(dto.toProdutoEntity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- FAVORITOS ---
    override fun listarFavoritos(): Flow<List<ProdutoEntity>> {
        return produtoDao.listarFavoritos()
    }

    override suspend fun alternarFavorito(id: Int, isFavorito: Boolean) {
        produtoDao.atualizarFavorito(id, isFavorito)
    }

    // --- CARRINHO (LÓGICA DE SOMA DE QUANTIDADE) ---
    override fun verCarrinho(usuarioEmail: String): Flow<List<CarrinhoEntity>> {
        return carrinhoDao.obterCarrinhoPorUsuario(usuarioEmail)
    }

    override suspend fun adicionarProdutoAoCarrinho(item: CarrinhoEntity) {
        try {
            val itemExistente = carrinhoDao.buscarItemNoCarrinho(item.produtoId, item.usuarioEmail)
            if (itemExistente != null) {
                // Incrementa a quantidade se o produto já estiver no carrinho em vez de duplicar linha
                val itemAtualizado = itemExistente.copy(quantidade = itemExistente.quantidade + 1)
                carrinhoDao.inserirOuAtualizar(itemAtualizado)
            } else {
                // Adiciona novo item se não existir
                carrinhoDao.inserirOuAtualizar(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun removerProdutoDoCarrinho(item: CarrinhoEntity) {
        carrinhoDao.deletarItem(item)
    }

    override suspend fun esvaziarCarrinho(usuarioEmail: String) {
        carrinhoDao.esvaziarCarrinho(usuarioEmail)
    }

    // --- USUÁRIO ---
    override fun obterUsuarioPorEmail(email: String): Flow<UsuarioEntity?> {
        return usuarioDao.obterUsuarioPorEmail(email)
    }

    override suspend fun deletarContaUsuario(email: String) {
        usuarioDao.deletarUsuario(email)
    }

    override suspend fun salvarUsuario(usuario: UsuarioEntity) {
        usuarioDao.salvarOuAtualizarUsuario(usuario)
    }

    // --- VENDEDOR ---
    override fun listarVendedores(): Flow<List<VendedorEntity>> {
        return vendedorDao.listarVendedores()
    }

    override suspend fun cadastrarVendedor(vendedor: VendedorEntity) {
        vendedorDao.cadastrarVendedor(vendedor)
    }

    // --- PEDIDOS / HISTÓRICO ---
    override suspend fun finalizarPedido(pedido: PedidoEntity) {
        pedidoDao.salvarPedido(pedido)
    }

    override fun obterPedidosPorUsuario(email: String): Flow<List<PedidoEntity>> {
        return pedidoDao.listarPedidosPorUsuario(email)
    }

    // --- VIA CEP ---
    override suspend fun buscarEnderecoRemoto(cep: String): EnderecoDto {
        return try {
            viaCepService.buscarEndereco(cep)
        } catch (e: Exception) {
            EnderecoDto(
                cep = cep,
                logradouro = "",
                complemento = "",
                bairro = "",
                localidade = "",
                uf = "",
                erro = true
            )
        }
    }

    override suspend fun aumentarQuantidade(item: CarrinhoEntity) {
        val novoItem = item.copy(quantidade = item.quantidade + 1)
        carrinhoDao.inserirOuAtualizar(novoItem)
    }

    override suspend fun diminuirQuantidade(item: CarrinhoEntity) {
        if (item.quantidade > 1) {
            val novoItem = item.copy(quantidade = item.quantidade - 1)
            carrinhoDao.inserirOuAtualizar(novoItem)
        } else {
            carrinhoDao.deletarItem(item)
        }
    }
}