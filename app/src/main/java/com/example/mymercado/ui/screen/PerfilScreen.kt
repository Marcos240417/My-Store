package com.example.mymercado.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mymercado.data.datasource.local.entity.UsuarioEntity
import com.example.mymercado.features.perfil.PerfilViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToCadastro: () -> Unit,
    onNavigateToPedidos: () -> Unit,
    onNavigateToEnderecos: () -> Unit,
    onNavigateToConfiguracoes: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val usuario by viewModel.usuario.collectAsState()
    val dadosExtras by viewModel.obterDadosUsuario(usuario?.email ?: "").collectAsState(initial = null as UsuarioEntity?)

    var uriSelecionadaParaAjuste by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uriSelecionadaParaAjuste = it
        }
    }

    // Modal de ajuste e enquadramento de foto
    uriSelecionadaParaAjuste?.let { uri ->
        AjusteFotoDialog(
            imageUri = uri,
            onConfirmar = {
                viewModel.salvarFotoPerfil(context, uri) {
                    uriSelecionadaParaAjuste = null
                }
            },
            onCancelar = {
                uriSelecionadaParaAjuste = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                PerfilHeader(
                    usuario = usuario,
                    onEditPhoto = { launcher.launch("image/*") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                MenuOptionItem(
                    icon = Icons.AutoMirrored.Filled.ListAlt,
                    title = "Meus Pedidos",
                    subtitle = "Histórico de compras e rastreio",
                    onClick = onNavigateToPedidos
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    MenuOptionItem(
                        icon = Icons.Default.Person,
                        title = "Editar Cadastro",
                        subtitle = "Nome, CPF e Telefone",
                        onClick = onNavigateToCadastro
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    MenuOptionItem(
                        icon = Icons.Default.LocationOn,
                        title = "Meus Endereços",
                        subtitle = "Gerenciar locais de entrega",
                        onClick = onNavigateToEnderecos
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    MenuOptionItem(
                        icon = Icons.Default.Settings,
                        title = "Configurações",
                        subtitle = if (dadosExtras != null) "Perfil Sincronizado" else "Ajustes e notificações",
                        onClick = onNavigateToConfiguracoes
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(1.dp)
                ) {
                    Text("Sair da Conta", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }

                TextButton(
                    onClick = { viewModel.excluirConta() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        "Excluir conta permanentemente",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PerfilHeader(usuario: UsuarioEntity?, onEditPhoto: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 3.dp
            ) {
                if (usuario?.urlFoto.isNullOrBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(usuario.urlFoto)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            SmallFloatingActionButton(
                onClick = onEditPhoto,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Alterar foto", modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = usuario?.nome?.ifBlank { "Usuário MyMercado" } ?: "Usuário MyMercado",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = usuario?.email?.ifBlank { "usuario@galga.com" } ?: "usuario@galga.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MenuOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant
        )
    }
}