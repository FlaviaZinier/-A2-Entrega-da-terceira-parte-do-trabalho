package com.example.wslbook.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.wslbook.Cores
import com.example.wslbook.R
import com.example.wslbook.Rotas
import com.example.wslbook.capaPorNome

// =====================================================================
// Componentes.kt
// "Peças de Lego" reaproveitadas em várias telas.
// =====================================================================


// ---------- CAPA DO LIVRO ----------
// Mostra a imagem da capa de CADA livro (o "endereço" da imagem vem do campo
// "capa" da data class Livro). Se nenhuma for informada, usa a capa padrão.
@Composable
fun CapaDoLivro(
    titulo: String,
    capa: String = "capa",   // NOME da imagem em res/drawable (vem do banco)
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = capaPorNome(capa)),
        contentDescription = "Capa do livro $titulo",
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(6.dp))
    )
}


// ---------- FILTRO (chip clicável) ----------
// Pílula vermelha do Trabalho 1, agora clicável e com estado "selecionado".
@Composable
fun Filtro(
    texto: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selecionado) Cores.Vermelho else Cores.FundoCard,
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(30.dp))
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(
                text = texto,
                color = Cores.Creme,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


// ---------- BARRA DE PROGRESSO ----------
// porcentagem vai de 0 a 100
@Composable
fun BarraDeProgresso(porcentagem: Int, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { porcentagem / 100f },     // o componente quer um número de 0.0 a 1.0
        color = Cores.Vermelho,
        trackColor = Cores.FundoCard,
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
    )
}


// ---------- BARRA DO TOPO (TopAppBar com botão de voltar) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraDoTopo(
    titulo: String,
    onVoltar: () -> Unit,
    acoes: @Composable () -> Unit = {}       // ícones extras do lado direito (opcional)
) {
    TopAppBar(
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onVoltar) {           // botão de VOLTAR funcional
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        actions = { acoes() },
        // O Scaffold do AppNavigation já cuida do espaço da barra de status,
        // então aqui zeramos para não sobrar um espaço duplo no topo.
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Cores.Fundo,
            titleContentColor = Cores.Creme,
            navigationIconContentColor = Cores.Creme,
            actionIconContentColor = Cores.Creme
        )
    )
}


// ---------- CORES PADRÃO DOS CAMPOS DE TEXTO ----------
@Composable
fun coresDoCampo(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Cores.Creme,
    unfocusedTextColor = Cores.Creme,
    focusedBorderColor = Cores.Vermelho,
    unfocusedBorderColor = Cores.Vermelho.copy(alpha = 0.6f),
    cursorColor = Cores.Vermelho,
    focusedLabelColor = Cores.Vermelho,
    unfocusedLabelColor = Cores.Cinza,
    focusedPlaceholderColor = Cores.Cinza,
    unfocusedPlaceholderColor = Cores.Cinza,
    focusedLeadingIconColor = Cores.Creme,
    unfocusedLeadingIconColor = Cores.Creme
)


// ---------- BARRA DE NAVEGAÇÃO DE BAIXO ----------
// Cada item da barra: rota de destino, ícone e nome
private data class ItemDaBarra(val rota: String, val icone: ImageVector, val nome: String)

private val itensDaBarra = listOf(
    ItemDaBarra(Rotas.INICIO, Icons.Filled.Home, "Início"),
    ItemDaBarra(Rotas.BIBLIOTECA, Icons.AutoMirrored.Filled.LibraryBooks, "Biblioteca"),
    ItemDaBarra(Rotas.ANOTACOES, Icons.Filled.EditNote, "Anotações"),
    ItemDaBarra(Rotas.PERFIL, Icons.Filled.Person, "Perfil")
)

@Composable
fun BarraDeNavegacao(
    navController: NavHostController,
    rotaAtual: String?
) {
    NavigationBar(containerColor = Cores.Fundo) {
        itensDaBarra.forEach { item ->
            NavigationBarItem(
                selected = rotaAtual == item.rota,       // pinta de vermelho o item da tela atual
                onClick = {
                    navController.navigate(item.rota) {
                        // Volta até a tela inicial antes de abrir a aba,
                        // para não empilhar telas infinitamente
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true   // não abre duas cópias da mesma tela
                        restoreState = true      // volta a aba do jeito que estava
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icone,
                        contentDescription = item.nome,
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = { Text(item.nome, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Cores.Vermelho,
                    selectedTextColor = Cores.Vermelho,
                    unselectedIconColor = Cores.Creme,
                    unselectedTextColor = Cores.Creme,
                    indicatorColor = Cores.Fundo
                )
            )
        }
    }
}
