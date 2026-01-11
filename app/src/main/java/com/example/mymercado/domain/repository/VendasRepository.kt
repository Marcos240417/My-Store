package com.example.mymercado.domain.repository

import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.core.data.UsuarioEntity
import com.example.mymercado.core.data.VendedorEntity
import com.example.mymercado.core.data.remoteviacep.EnderecoDto
import kotlinx.coroutines.flow.Flow

interface VendasRepository {
    // Produtos
    fun listarProdutos(): Flow<List<ProdutoEntity>>
    fun buscarProdutosPorCategoria(categoria: String): Flow<List<ProdutoEntity>>
    suspend fun buscarProdutoPorId(id: Int): ProdutoEntity?
    suspend fun inserirProduto(produto: ProdutoEntity)
    suspend fun sincronizarProdutos()

    // VIA CEP (Adicionado ao contrato)
    suspend fun buscarEnderecoRemoto(cep: String): EnderecoDto

    // Carrinho
    fun verCarrinho(usuarioEmail: String): Flow<List<CarrinhoEntity>>
    suspend fun adicionarProdutoAoCarrinho(item: CarrinhoEntity)
    suspend fun removerProdutoDoCarrinho(item: CarrinhoEntity)
    suspend fun esvaziarCarrinho(usuarioEmail: String)

    // Usuário
    fun obterUsuarioPorEmail(email: String): Flow<UsuarioEntity?>
    suspend fun deletarContaUsuario(email: String)
    suspend fun salvarUsuario(usuario: UsuarioEntity)

    // Vendedor
    fun listarVendedores(): Flow<List<VendedorEntity>>
    suspend fun cadastrarVendedor(vendedor: VendedorEntity)
}