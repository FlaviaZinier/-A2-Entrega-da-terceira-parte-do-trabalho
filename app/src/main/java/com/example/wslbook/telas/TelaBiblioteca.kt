package com.example.wslbook.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.wslbook.componentes.BarraDeProgresso
import com.example.wslbook.componentes.CapaDoLivro
import com.example.wslbook.componentes.Filtro

@Composable
fun TelaBiblioteca(
    livros: List<Livro>,
    onExcluirLivro: (Livro) -> Unit,
    onAbrirLivro: (Int) -> Unit,
    onNovoLivro: () -> Unit
) {
    var statusSelecionado by rememberSaveable { mutableStateOf<String?>(null) }
    val listaFiltrada = livros.filter { statusSelecionado == null || it.status() == statusSelecionado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Biblioteca", color = Cores.Creme, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("${livros.size} livros", color = Cores.Cinza, fontSize = 13.sp)
            }
            Button(
                onClick = onNovoLivro,
                colors = ButtonDefaults.buttonColors(containerColor = Cores.Vermelho),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Cores.Creme)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adicionar", color = Cores.Creme, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Filtro("Todos", statusSelecionado == null) { statusSelecionado = null }
            }
            items(listOf("Quero ler", "Lendo", "Lido")) { status ->
                Filtro(status, statusSelecionado == status) {
                    statusSelecionado = if (statusSelecionado == status) null else status
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (listaFiltrada.isEmpty()) {
            Text(
                text = "Nenhum livro aqui ainda.\nToque em \"Adicionar\" para cadastrar um.",
                color = Cores.Cinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(listaFiltrada, key = { it.id }) { livro ->
                    CardLivro(
                        livro = livro,
                        onClick = { onAbrirLivro(livro.id) },
                        onRemover = {
                            // Apaga do banco. As anotações dele somem juntas (ForeignKey CASCADE)
                            onExcluirLivro(livro)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CardLivro(
    livro: Livro,
    onClick: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Cores.FundoCard),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            CapaDoLivro(titulo = livro.titulo, capa = livro.capa, modifier = Modifier.size(60.dp, 90.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = livro.titulo,
                    color = Cores.Creme,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(livro.autor, color = Cores.Cinza, fontSize = 13.sp)
                Text(
                    text = "${livro.genero} • ${livro.status()}",
                    color = Cores.Vermelho,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                BarraDeProgresso(porcentagem = livro.porcentagemLida())
                Text("${livro.porcentagemLida()}%", color = Cores.Cinza, fontSize = 11.sp)
            }

            IconButton(onClick = onRemover) {
                Icon(Icons.Filled.Delete, contentDescription = "Remover livro", tint = Cores.Vermelho)
            }
        }
    }
}
