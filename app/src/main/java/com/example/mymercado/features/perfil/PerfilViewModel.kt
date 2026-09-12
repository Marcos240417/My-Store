package com.example.mymercado.features.perfil

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymercado.core.common.AppConstants
import com.example.mymercado.data.datasource.local.entity.UsuarioEntity
import com.example.mymercado.domain.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PerfilViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    val usuario: StateFlow<UsuarioEntity?> = repository.obterUsuarioPorEmail(AppConstants.DEFAULT_USER_EMAIL)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun obterDadosUsuario(email: String): Flow<UsuarioEntity?> {
        return repository.obterUsuarioPorEmail(email)
    }

    fun salvarFotoPerfil(context: Context, uriOrigem: Uri, onFinalizado: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val inputStream = context.contentResolver.openInputStream(uriOrigem)
                val arquivoDestino = File(context.filesDir, "foto_perfil_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(arquivoDestino)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                val novoCaminhoUri = Uri.fromFile(arquivoDestino).toString()

                val usuarioAtual = repository.obterUsuarioPorEmailSimples(AppConstants.DEFAULT_USER_EMAIL)
                val atualizado = usuarioAtual?.copy(urlFoto = novoCaminhoUri) ?: UsuarioEntity(
                    email = AppConstants.DEFAULT_USER_EMAIL,
                    nome = "Usuário MyMercado",
                    cpf = "",
                    telefone = "",
                    cep = "",
                    logradouro = "",
                    bairro = "",
                    numero = "",
                    cidade = "",
                    estado = "",
                    localidade = "",
                    uf = "",
                    urlFoto = novoCaminhoUri
                )
                repository.salvarUsuario(atualizado)
            }.onSuccess {
                launch(Dispatchers.Main) { onFinalizado() }
            }
        }
    }

    fun excluirConta() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.deletarContaUsuario(AppConstants.DEFAULT_USER_EMAIL)
            }
        }
    }
}