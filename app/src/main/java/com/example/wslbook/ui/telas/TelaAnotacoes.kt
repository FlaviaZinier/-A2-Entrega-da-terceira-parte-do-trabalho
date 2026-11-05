package com.example.wslbook.ui.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.wslbook.viewmodel.AnotacoesViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.data.local.Anotacao
import com.example.wslbook.ui.theme.Cores
import com.example.wslbook.data.local.Livro
import com.example.wslbook.ui.componentes.Filtro
import com.example.wslbook.ui.componentes.coresDoCampo

@Composable
fun TelaAnotacoes(
    viewModel: AnotacoesViewModel,
    onAbrirAnotacao: (Int) -> Unit
) {
    // Tudo (listas, campos do formulário, filtro) vem do AnotacoesUiState.
    // A tela só lê o estado e chama as funções do AnotacoesViewModel.
    val uiState by viewModel.uiState.collectAsState()

    val livros = uiState.livros
    val anotacoes = uiState.anotacoes
    val formularioAberto = uiState.formularioAberto
    val livroEscolhidoId = uiState.livroEscolhidoId
    val trecho = uiState.trecho
    val paginaTexto = uiState.paginaTexto
    val erro = uiState.erro
    val soFavoritas = uiState.soFavoritas

    val listaFiltrada = anotacoes.filter { !soFavoritas || it.favorita }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text("Anotações", color = Cores.Creme, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "${anotacoes.size} trechos marcados • ${anotacoes.count { it.favorita }} favoritos",
            color = Cores.Cinza,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Cores.FundoCard),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.alternarFormulario() }
                ) {
                    Text(
                        text = "Nova anotação",
                        color = Cores.Creme,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (formularioAberto) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (formularioAberto) "Fechar formulário" else "Abrir formulário",
                        tint = Cores.Creme
                    )
                }

                if (formularioAberto) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("De qual livro?", color = Cores.Cinza, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(livros, key = { it.id }) { livro ->
                            Filtro(
                                texto = livro.titulo,
                                selecionado = livroEscolhidoId == livro.id,
                                onClick = { viewModel.escolherLivro(livro.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = trecho,
                        onValueChange = { viewModel.mudarTrecho(it) },
                        label = { Text("Trecho marcado") },
                        minLines = 3,
                        colors = coresDoCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = paginaTexto,
                        onValueChange = { viewModel.mudarPagina(it) },
                        label = { Text("Página") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = coresDoCampo(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    erro?.let { Text(it, color = Cores.Vermelho, fontSize = 13.sp) }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.adicionarAnotacao() },   // validação e INSERT ficam no ViewModel
                        colors = ButtonDefaults.buttonColors(containerColor = Cores.Vermelho),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Text("Adicionar anotação", color = Cores.Creme, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Filtro("Todas", !soFavoritas) { viewModel.mostrarSoFavoritas(false) }
            Filtro("Favoritas", soFavoritas) { viewModel.mostrarSoFavoritas(true) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (listaFiltrada.isEmpty()) {
            Text(
                text = "Nenhuma anotação por aqui.",
                color = Cores.Cinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(listaFiltrada, key = { it.id }) { anotacao ->
                    val livro = livros.find { it.id == anotacao.livroId }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Cores.FundoCard),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAbrirAnotacao(anotacao.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
                        ) {
                            Checkbox(
                                checked = anotacao.favorita,
                                onCheckedChange = { marcado ->
                                    viewModel.marcarFavorita(anotacao, marcado)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Cores.Vermelho,
                                    uncheckedColor = Cores.Cinza,
                                    checkmarkColor = Cores.Creme
                                )
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "“${anotacao.trecho}”",
                                    color = Cores.Creme,
                                    fontStyle = FontStyle.Italic,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${livro?.titulo ?: "Livro removido"} • pág. ${anotacao.pagina}",
                                    color = Cores.Vermelho,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(onClick = { viewModel.deletarAnotacao(anotacao) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remover anotação", tint = Cores.Vermelho)
                            }
                        }
                    }
                }
            }
        }
    }
}