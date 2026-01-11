package com.example.mymercado.core.data.remoteviacep

import com.example.mymercado.core.data.UsuarioEntity // Ou sua entidade de Endereço

data class EnderecoDto(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val localidade: String, // Representa a Cidade
    val uf: String,
    val erro: Boolean? = null // ViaCEP envia true se o CEP não existir
)

// Função de Extensão para converter o DTO para a sua Entidade de Banco de Dados
// Ajuste os campos conforme sua UsuarioEntity ou EnderecoEntity
fun EnderecoDto.toUsuarioEntity(nome: String, email: String, cpf: String, telefone: String, numero: String): UsuarioEntity {
    return UsuarioEntity(
        nome = nome,
        email = email,
        cep = this.cep,
        logradouro = this.logradouro,
        bairro = this.bairro,
        cidade = this.localidade,
        estado = this.uf,
        cpf = cpf,
        telefone = telefone,
        numero = numero,
    )
}