package com.example.wslbook.telas

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.Cores
import com.example.wslbook.Livro
import com.example.wslbook.componentes.CapaDoLivro
import com.example.wslbook.componentes.Filtro
import com.example.wslbook.componentes.coresDoCampo
import com.example.wslbook.generos

@Composable
fun TelaInicio(
    livros: List<Livro>,
    onAbrirLivro: (Int) -> Unit
) {

    var busca by rememberSaveable { mutableStateOf("") }
    var generoSelecionado by rememberSaveable { mutableStateOf<String?>(null) } 

    val lendoAgora = livros.filter { it.status() == "Lendo" }

    val catalogoFiltrado = livros.filter { livro ->
        val bateBusca = busca.isBlank() ||
                livro.titulo.contains(busca, ignoreCase = true) ||
                livro.autor.contains(busca, ignoreCase = true)
        val bateGenero = generoSelecionado == null || livro.genero == generoSelecionado
        bateBusca && bateGenero
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, Cores.Vermelho, RoundedCornerShape(25.dp))
                .padding(horizontal = 12.dp, vertical = 14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Continue lendo",
                    color = Cores.Creme,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (lendoAgora.isEmpty()) {
                    Text(
                        text = "Nenhum livro em andamento",
                        color = Cores.Cinza,
                        fontSize = 14.sp
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(lendoAgora, key = { it.id }) { livro ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(80.dp)
                                    .clickable { onAbrirLivro(livro.id) }  
                            ) {
                                CapaDoLivro(
                                    titulo = livro.titulo,
                                    capa = livro.capa,
                                    modifier = Modifier.size(80.dp, 120.dp)
                                )
                                Text(
                                    text = "${livro.porcentagemLida()}%",
                                    color = Cores.Vermelho,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busca,
            onValueChange = { busca = it },
            placeholder = { Text("Pesquisar livros ou autores...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Pesquisar") },
            singleLine = true,
            shape = RoundedCornerShape(35.dp),
            colors = coresDoCampo(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Filtro(
                    texto = "Todos",
                    selecionado = generoSelecionado == null,
                    onClick = { generoSelecionado = null }
                )
            }
            items(generos) { genero ->
                Filtro(
                    texto = genero,
                    selecionado = generoSelecionado == genero,
                    onClick = {
                        generoSelecionado = if (generoSelecionado == genero) null else genero
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(3.dp, Cores.Vermelho, RoundedCornerShape(25.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "Catálogo",
                    color = Cores.Creme,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (catalogoFiltrado.isEmpty()) {
                    Text(
                        text = "Nenhum livro encontrado",
                        color = Cores.Cinza,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(catalogoFiltrado, key = { it.id }) { livro ->
                            Column(
                                modifier = Modifier.clickable { onAbrirLivro(livro.id) }
                            ) {
                                CapaDoLivro(
                                    titulo = livro.titulo,
                                    capa = livro.capa,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f / 3f)
                                )
                                Text(
                                    text = livro.titulo,
                                    color = Cores.Creme,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
