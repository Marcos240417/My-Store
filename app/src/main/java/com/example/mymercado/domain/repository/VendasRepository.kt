package com.example.mymercado.domain.repository

import com.example.mymercado.core.data.CarrinhoEntity
import com.example.mymercado.core.data.PedidoEntity
import com.example.mymercado.core.data.ProdutoEntity
import com.example.mymercado.core.data.UsuarioEntity
import com.example.mymercado.core.data.VendedorEntity
import com.example.mymercado.core.data.remoteviacep.EnderecoDto
import kotlinx.coroutines.flow.Flow

interface VendasRepository {
    // --- PRODUTOS ---
    fun listarProdutos(): Flow<List<ProdutoEntity>>
    fun buscarProdutosPorCategoria(categoria: String): Flow<List<ProdutoEntity>>
    suspend fun buscarProdutoPorId(id: Int): ProdutoEntity?
    suspend fun inserirProduto(produto: ProdutoEntity)
    suspend fun sincronizarProdutos()

    // --- VIA CEP ---
    suspend fun buscarEnderecoRemoto(cep: String): EnderecoDto

    // --- CARRINHO ---
    fun verCarrinho(usuarioEmail: String): Flow<List<CarrinhoEntity>>
    suspend fun adicionarProdutoAoCarrinho(item: CarrinhoEntity)
    suspend fun removerProdutoDoCarrinho(item: CarrinhoEntity)
    suspend fun esvaziarCarrinho(usuarioEmail: String)

    // --- USUÁRIO ---
    fun obterUsuarioPorEmail(email: String): Flow<UsuarioEntity?>
    suspend fun deletarContaUsuario(email: String)
    suspend fun salvarUsuario(usuario: UsuarioEntity)

    // --- VENDEDOR ---
    fun listarVendedores(): Flow<List<VendedorEntity>>
    suspend fun cadastrarVendedor(vendedor: VendedorEntity)

    // --- PEDIDOS (HISTÓRICO) ---
    suspend fun finalizarPedido(pedido: PedidoEntity)
    fun obterPedidosPorUsuario(email: String): Flow<List<PedidoEntity>>

    // --- FAVORITOS ---
    fun listarFavoritos(): Flow<List<ProdutoEntity>>
    suspend fun alternarFavorito(id: Int, isFavorito: Boolean)

    suspend fun aumentarQuantidade(item: CarrinhoEntity)
    suspend fun diminuirQuantidade(item: CarrinhoEntity)
}