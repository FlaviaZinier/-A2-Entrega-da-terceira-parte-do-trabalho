package com.example.wslbook.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.data.local.generos
import com.example.wslbook.ui.componentes.BarraDoTopo
import com.example.wslbook.ui.componentes.Filtro
import com.example.wslbook.ui.componentes.coresDoCampo
import com.example.wslbook.ui.theme.Cores
import com.example.wslbook.viewmodel.LivroFormViewModel

// =====================================================================
// NOVO LIVRO / EDITAR LIVRO
// A tela só faz duas coisas: LÊ o uiState e CHAMA funções do ViewModel.
// O texto dos campos mora no LivroFormUiState (não some ao girar).
//  - livroId = -1 -> cadastro novo
//  - livroId = 5  -> edita o livro 5
// =====================================================================
@Composable
fun TelaNovoLivro(
    viewModel: LivroFormViewModel,
    onVoltar: () -> Unit,
    livroId: Int = -1
) {
    val uiState by viewModel.uiState.collectAsState()

    // Ao abrir: se for edição, o ViewModel busca o livro e preenche os campos
    LaunchedEffect(livroId) { viewModel.carregar(livroId) }

    // Quando o ViewModel avisar que salvou, volta para a tela anterior
    LaunchedEffect(uiState.salvo) {
        if (uiState.salvo) onVoltar()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        BarraDoTopo(
            titulo = if (uiState.editando) "Editar livro" else "Novo livro",
            onVoltar = onVoltar
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = { viewModel.mudarTitulo(it) },
                label = { Text("Título *") },
                singleLine = true,
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.autor,
                onValueChange = { viewModel.mudarAutor(it) },
                label = { Text("Autor(a)") },
                singleLine = true,
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Gênero", color = Cores.Creme, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(generos) { g ->
                    Filtro(texto = g, selecionado = uiState.genero == g, onClick = { viewModel.mudarGenero(g) })
                }
            }

            OutlinedTextField(
                value = uiState.totalPaginasTexto,
                onValueChange = { viewModel.mudarTotalPaginas(it) },
                label = { Text("Total de páginas *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.paginasLidasTexto,
                onValueChange = { viewModel.mudarPaginasLidas(it) },
                label = { Text("Páginas já lidas (opcional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            uiState.erro?.let { mensagem ->
                Text(mensagem, color = Cores.Vermelho, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { viewModel.salvar() },   // validação e INSERT/UPDATE ficam no ViewModel
                colors = ButtonDefaults.buttonColors(containerColor = Cores.Vermelho),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (uiState.editando) "Salvar alterações" else "Salvar livro",
                    color = Cores.Creme,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
