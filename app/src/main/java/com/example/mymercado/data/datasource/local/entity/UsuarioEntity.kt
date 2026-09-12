package com.example.mymercado.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String, // Usaremos email como ID único
    val nome: String,
    val cpf: String,
    val telefone: String,
    val urlFoto: String? = null,
    // Dados de Entrega
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val numero: String,
    val cidade: String,
    val estado: String,
    val formaPagamentoPadrao: String = "PIX",
    val localidade: String,
    val uf: String
)