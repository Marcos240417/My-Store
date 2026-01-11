package com.example.mymercado.features.cadastroviewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.remoteviacep.EnderecoDto
import com.example.mymercado.core.data.remoteviacep.toUsuarioEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.launch

class CadastroViewModel(private val repository: VendasRepository) : ViewModel() {

    private var enderecoOriginal: EnderecoDto? = null

    // Estados observáveis pela UI
    var nome by mutableStateOf("")
    var email by mutableStateOf("")
    var cpf by mutableStateOf("")
    var telefone by mutableStateOf("")
    var cep by mutableStateOf("")
    var logradouro by mutableStateOf("")
    var bairro by mutableStateOf("")
    var cidade by mutableStateOf("")
    var estado by mutableStateOf("")
    var numero by mutableStateOf("")

    init {
        // Ação disparada assim que a ViewModel é criada (ao clicar em Editar ou Cadastrar)
        carregarDadosParaEdicao()
    }

    private fun carregarDadosParaEdicao() {
        viewModelScope.launch {
            // Buscamos o usuário pela chave padrão
            repository.obterUsuarioPorEmail("user@galga.com").collect { usuario ->
                usuario?.let {
                    // Preenchemos os campos com o que já existe no banco
                    nome = it.nome
                    email = it.email
                    cpf = it.cpf
                    telefone = it.telefone
                    cep = it.cep
                    logradouro = it.logradouro
                    bairro = it.bairro
                    cidade = it.cidade
                    estado = it.estado
                    numero = it.numero
                }
            }
        }
    }

    fun onCepChange(novoCep: String) {
        if (novoCep.length <= 8) {
            cep = novoCep
            if (cep.length == 8) buscarEndereco(cep)
        }
    }

    private fun buscarEndereco(cepValue: String) {
        viewModelScope.launch {
            try {
                val dto = repository.buscarEnderecoRemoto(cepValue)
                if (dto.erro != true) {
                    enderecoOriginal = dto
                    logradouro = dto.logradouro
                    bairro = dto.bairro
                    cidade = dto.localidade
                    estado = dto.uf
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun salvarCadastro(onSucesso: () -> Unit) {
        viewModelScope.launch {
            val dtoParaConverter = enderecoOriginal ?: EnderecoDto(
                cep = cep, logradouro = logradouro, complemento = "",
                bairro = bairro, localidade = cidade, uf = estado
            )

            val usuarioFinal = dtoParaConverter.toUsuarioEntity(
                nome = nome,
                email = "user@galga.com",
                cpf = cpf,
                telefone = telefone,
                numero = numero
            )

            repository.salvarUsuario(usuarioFinal)
            onSucesso()
        }
    }
}