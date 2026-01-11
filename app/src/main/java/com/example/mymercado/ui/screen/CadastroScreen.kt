package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mymercado.features.cadastroviewmodel.CadastroViewModel
import org.koin.androidx.compose.koinViewModel // IMPORT ESSENCIAL DO KOIN

@Composable
fun CadastroScreen(
    viewModel: CadastroViewModel = koinViewModel(), // INJEÇÃO VIA KOIN
    onSucesso: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cadastro de Usuário", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = viewModel.nome,
            onValueChange = { viewModel.nome = it },
            label = { Text("Nome Completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = viewModel.cpf,
                onValueChange = { viewModel.cpf = it },
                label = { Text("CPF") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = viewModel.telefone,
                onValueChange = { viewModel.telefone = it },
                label = { Text("Telefone") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        OutlinedTextField(
            value = viewModel.cep,
            onValueChange = { viewModel.onCepChange(it) },
            label = { Text("CEP (Busca automática)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.logradouro,
            onValueChange = { viewModel.logradouro = it },
            label = { Text("Logradouro (Rua)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = viewModel.numero,
                onValueChange = { viewModel.numero = it },
                label = { Text("Nº") },
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = viewModel.bairro,
                onValueChange = { viewModel.bairro = it },
                label = { Text("Bairro") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                viewModel.salvarCadastro {
                    onSucesso()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 8.dp)
        ) {
            Text("FINALIZAR CADASTRO")
        }
    }
}