package com.example.mymercado.domain.repository

import com.example.mymercado.core.dao.CarrinhoDao
import com.example.mymercado.core.dao.ProdutoDao
import com.example.mymercado.core.dao.UsuarioDao
import com.example.mymercado.core.dao.VendedorDao
import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.core.data.UsuarioEntity
import com.example.mymercado.core.data.VendedorEntity
import com.example.mymercado.core.data.remotestorefake.FakeStoreService
import com.example.mymercado.core.data.remotestorefake.toProdutoEntity
import com.example.mymercado.core.data.remoteviacep.EnderecoDto
import com.example.mymercado.core.data.remoteviacep.ViaCepService // Import da Interface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VendasRepositoryImpl(
    private val produtoDao: ProdutoDao,
    private val carrinhoDao: CarrinhoDao,
    private val usuarioDao: UsuarioDao,
    private val vendedorDao: VendedorDao,
    private val api: FakeStoreService,
    private val viaCepService: ViaCepService // CORREÇÃO: Injetando o serviço ViaCEP
) : VendasRepository {

    // PRODUTOS
    override fun listarProdutos(): Flow<List<ProdutoEntity>> = produtoDao.listarTodos()

    override fun buscarProdutosPorCategoria(categoria: String): Flow<List<ProdutoEntity>> =
        produtoDao.buscarPorCategoria(categoria)

    override suspend fun buscarProdutoPorId(id: Int): ProdutoEntity? = produtoDao.buscarPorId(id)

    override suspend fun inserirProduto(produto: ProdutoEntity) = produtoDao.inserir(produto)

    override suspend fun sincronizarProdutos() {
        withContext(Dispatchers.IO) {
            try {
                val produtosDto = api.getProdutos()
                produtosDto.forEach { dto ->
                    produtoDao.inserir(dto.toProdutoEntity())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun buscarEnderecoRemoto(cep: String): EnderecoDto {
        return withContext(Dispatchers.IO) {
            // Usa a instância injetada 'viaCepService' para chamar a função correta
            viaCepService.buscarEndereco(cep)
        }
    }

    // CARRINHO
    override fun verCarrinho(usuarioEmail: String): Flow<List<CarrinhoEntity>> =
        carrinhoDao.obterCarrinhoPorUsuario(usuarioEmail)

    override suspend fun adicionarProdutoAoCarrinho(item: CarrinhoEntity) =
        carrinhoDao.inserirOuAtualizar(item)

    override suspend fun removerProdutoDoCarrinho(item: CarrinhoEntity) =
        carrinhoDao.deletarItem(item)

    override suspend fun esvaziarCarrinho(usuarioEmail: String) =
        carrinhoDao.esvaziarCarrinho(usuarioEmail)

    // USUÁRIO
    override fun obterUsuarioPorEmail(email: String): Flow<UsuarioEntity?> =
        usuarioDao.obterUsuarioPorEmail(email)

    override suspend fun deletarContaUsuario(email: String) =
        usuarioDao.deletarUsuario(email)

    override suspend fun salvarUsuario(usuario: UsuarioEntity) =
        usuarioDao.salvarOuAtualizarUsuario(usuario)

    // VENDEDOR
    override fun listarVendedores(): Flow<List<VendedorEntity>> = vendedorDao.listarVendedores()

    override suspend fun cadastrarVendedor(vendedor: VendedorEntity) =
        vendedorDao.cadastrarVendedor(vendedor)
}