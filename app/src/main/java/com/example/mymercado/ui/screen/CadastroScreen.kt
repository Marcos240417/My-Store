package com.example.mymercado.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mymercado.core.data.FormaPagamento
import com.example.mymercado.features.cadastroviewmodel.CadastroViewModel
import com.example.mymercado.ui.mask.MaskVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(
    viewModel: CadastroViewModel,
    onBack: () -> Unit,
    onSucesso: () -> Unit
) {
    val estaCarregando by viewModel.estaCarregando.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Cadastro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Dados Pessoais", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = viewModel.nome,
                    onValueChange = { viewModel.nome = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.cpf,
                        onValueChange = { if (it.length <= 11) viewModel.cpf = it },
                        label = { Text("CPF") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = MaskVisualTransformation("000.000.000-00"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = viewModel.telefone,
                        onValueChange = { if (it.length <= 11) viewModel.telefone = it },
                        label = { Text("Telefone") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        visualTransformation = MaskVisualTransformation("(00) 00000-0000"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Endereço de Entrega", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = viewModel.cep,
                    onValueChange = { viewModel.onCepChange(it) },
                    label = { Text("CEP") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        if (estaCarregando) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.logradouro,
                    onValueChange = { viewModel.logradouro = it },
                    label = { Text("Rua / Logradouro") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.numero,
                        onValueChange = { viewModel.numero = it },
                        label = { Text("Nº") },
                        modifier = Modifier.weight(0.4f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = viewModel.bairro,
                        onValueChange = { viewModel.bairro = it },
                        label = { Text("Bairro") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.cidade,
                        onValueChange = { viewModel.cidade = it },
                        label = { Text("Cidade") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = viewModel.estado,
                        onValueChange = { viewModel.estado = it },
                        label = { Text("UF") },
                        modifier = Modifier.weight(0.3f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // CHAMADA ÚNICA DO COMPONENTE
                SelecaoPagamentoComponent(
                    formaSelecionada = viewModel.formaPagamentoSelecionada,
                    onFormaSelected = { novaForma: FormaPagamento ->
                        viewModel.atualizarPagamento(novaForma)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.salvarCadastro(onSucesso) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !estaCarregando && viewModel.nome.isNotBlank() && viewModel.cep.length == 8,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SALVAR CADASTRO", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (estaCarregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }
}
