package com.example.mymercado.core.data.remoteviacep

import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepService {
    @GET("{cep}/json/")
    suspend fun buscarEndereco(@Path("cep") cep: String): EnderecoDto
}