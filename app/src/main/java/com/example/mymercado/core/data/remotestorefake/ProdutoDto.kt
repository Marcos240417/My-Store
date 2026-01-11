package com.example.mymercado.core.data.remotestorefake

import com.example.mymercado.core.data.ProdutoEntity
import com.google.gson.annotations.SerializedName

data class ProdutoDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    @SerializedName("image") val imageUrl: String
)

// Função de Extensão: Converte o dado da Internet para o dado do Banco de Dados
fun ProdutoDto.toProdutoEntity(): ProdutoEntity {
    return ProdutoEntity(
        produtoId = this.id, // Opcional: use o ID da API ou deixe o Room gerar
        titulo = this.title,
        preco = this.price,
        descricao = this.description,
        categoria = this.category,
        urlImagem = this.imageUrl,
        vendedorDonoId = 1, // Fixamos um ID de vendedor padrão
        estoque = 10
    )
}