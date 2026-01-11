package com.example.mymercado.core.data.remotestorefake

import retrofit2.http.GET

interface FakeStoreService {
    @GET("products")
    suspend fun getProdutos(): List<ProdutoDto>
}