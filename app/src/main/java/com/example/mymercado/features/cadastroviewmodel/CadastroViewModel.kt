package com.example.mymercado.features.cadastroviewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.core.data.remoteviacep.EnderecoDto
import com.example.mymercado.core.data.remoteviacep.toUsuarioEntity
import com.example.mymercado.domain.repository.VendasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroViewModel(private val repository: VendasRepository) : ViewModel() {

    private var enderecoOriginal: EnderecoDto? = null

    private val _estaCarregando = MutableStateFlow(false)
    val estaCarregando = _estaCarregando.asStateFlow()

    // Estados para UI
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
    var formaPagamentoSelecionada by mutableStateOf(FormaPagamento.PIX)

    init {
        carregarDadosParaEdicao()
    }

    private fun carregarDadosParaEdicao() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.obterUsuarioPorEmail("user@galga.com").collect { usuario ->
                    usuario?.let {
                        withContext(Dispatchers.Main) {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onCepChange(novoCep: String) {
        val apenasNumeros = novoCep.filter { it.isDigit() }
        if (apenasNumeros.length <= 8) {
            cep = apenasNumeros
            if (apenasNumeros.length == 8) {
                buscarEndereco(apenasNumeros)
            }
        }
    }

    private fun buscarEndereco(cepValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _estaCarregando.value = true
            try {
                val dto = repository.buscarEnderecoRemoto(cepValue)
                if (dto.erro != true) {
                    enderecoOriginal = dto
                    withContext(Dispatchers.Main) {
                        logradouro = dto.logradouro
                        bairro = dto.bairro
                        cidade = dto.localidade
                        estado = dto.uf
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _estaCarregando.value = false
            }
        }
    }

    fun atualizarPagamento(novaForma: FormaPagamento) {
        formaPagamentoSelecionada = novaForma
    }

    fun salvarCadastro(onSucesso: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dtoParaConverter = enderecoOriginal ?: EnderecoDto(
                    cep = cep,
                    logradouro = logradouro,
                    complemento = "",
                    bairro = bairro,
                    localidade = cidade,
                    uf = estado
                )

                val usuarioFinal = dtoParaConverter.toUsuarioEntity(
                    nome = nome,
                    email = "user@galga.com",
                    cpf = cpf,
                    telefone = telefone,
                    numero = numero
                )

                repository.salvarUsuario(usuarioFinal)
                withContext(Dispatchers.Main) { onSucesso() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}