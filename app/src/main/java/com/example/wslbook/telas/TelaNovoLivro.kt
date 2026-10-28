package com.example.wslbook.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.wslbook.LivrosViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.Cores
import com.example.wslbook.Livro
import com.example.wslbook.componentes.BarraDoTopo
import com.example.wslbook.componentes.Filtro
import com.example.wslbook.componentes.coresDoCampo
import com.example.wslbook.generos


// =====================================================================
// NOVO LIVRO / EDITAR LIVRO
// A mesma tela serve para as duas coisas:
//  - livroId = -1  -> cadastro NOVO   (salva com dao.inserir)
//  - livroId = 5   -> EDITA o livro 5 (busca no banco, preenche os campos
//                     e salva com dao.atualizar)
// =====================================================================
@Composable
fun TelaNovoLivro(
    viewModel: LivrosViewModel,
    onVoltar: () -> Unit,
    livroId: Int = -1
) {
    val editando = livroId > 0
    var titulo by rememberSaveable { mutableStateOf("") }
    var autor by rememberSaveable { mutableStateOf("") }
    var genero by rememberSaveable { mutableStateOf(generos.first()) }
    var totalPaginasTexto by rememberSaveable { mutableStateOf("") }
    var paginasLidasTexto by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }
    var capa by rememberSaveable { mutableStateOf("capa") }            // mantém a capa ao editar
    var dadosCarregados by rememberSaveable { mutableStateOf(false) }  // já preencheu os campos?

    // Se for EDIÇÃO: busca o livro no banco e preenche os campos (só uma vez,
    // para não apagar o que a pessoa digitou se o celular girar)
    LaunchedEffect(livroId) {
        if (editando && !dadosCarregados) {
            viewModel.buscarLivroPorId(livroId)?.let { livro ->
                titulo = livro.titulo
                autor = livro.autor
                genero = livro.genero
                totalPaginasTexto = livro.totalPaginas.toString()
                paginasLidasTexto = livro.paginasLidas.toString()
                capa = livro.capa
            }
            dadosCarregados = true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        BarraDoTopo(titulo = if (editando) "Editar livro" else "Novo livro", onVoltar = onVoltar)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título *") },
                singleLine = true,
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = autor,
                onValueChange = { autor = it },
                label = { Text("Autor(a)") },
                singleLine = true,
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Gênero", color = Cores.Creme, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(generos) { g ->
                    Filtro(texto = g, selecionado = genero == g, onClick = { genero = g })
                }
            }

            OutlinedTextField(
                value = totalPaginasTexto,
                onValueChange = { novo -> totalPaginasTexto = novo.filter { it.isDigit() } },
                label = { Text("Total de páginas *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = paginasLidasTexto,
                onValueChange = { novo -> paginasLidasTexto = novo.filter { it.isDigit() } },
                label = { Text("Páginas já lidas (opcional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = coresDoCampo(),
                modifier = Modifier.fillMaxWidth()
            )

            erro?.let { mensagem ->
                Text(mensagem, color = Cores.Vermelho, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val total = totalPaginasTexto.toIntOrNull()
                    val lidas = paginasLidasTexto.toIntOrNull() ?: 0

                    erro = when {
                        titulo.isBlank() -> "Informe o título do livro."
                        total == null || total <= 0 -> "Informe o total de páginas."
                        lidas > total -> "Páginas lidas não pode ser maior que o total."
                        else -> null
                    }

                    if (erro == null && total != null) {
                        val livro = Livro(
                            id = if (editando) livroId else 0,   // 0 = o banco gera um id novo
                            titulo = titulo.trim(),
                            autor = autor.trim().ifBlank { "Autor desconhecido" },
                            genero = genero,
                            totalPaginas = total,
                            paginasLidas = lidas,
                            capa = capa
                        )
                        if (editando) {
                            viewModel.atualizarLivro(livro)   // UPDATE no banco
                        } else {
                            viewModel.inserirLivro(livro)     // INSERT no banco
                        }
                        onVoltar()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Cores.Vermelho),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (editando) "Salvar alterações" else "Salvar livro", color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
