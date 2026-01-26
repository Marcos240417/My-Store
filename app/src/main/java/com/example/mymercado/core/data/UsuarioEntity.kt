package com.example.mymercado.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String, // Usaremos email como ID único
    val nome: String,
    val cpf: String,
    val telefone: String,
    // Dados de Entrega
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val numero: String,
    val cidade: String,
    val estado: String,
    val formaPagamentoPadrao: String = "PIX"
)