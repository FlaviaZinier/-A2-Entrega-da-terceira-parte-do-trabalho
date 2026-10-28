package com.example.wslbook.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.Cores
import com.example.wslbook.Livro
import com.example.wslbook.componentes.BarraDeProgresso
import com.example.wslbook.componentes.BarraDoTopo

private const val PAGINAS_POR_CAPITULO = 20

private const val TEXTO_EXEMPLO =
    "A chuva batia na janela enquanto ela virava mais uma página. " +
    "Cada palavra parecia abrir uma porta diferente, e cada porta levava a um " +
    "lugar que ela nunca tinha visitado.\n\n" +
    "Do lado de fora, a cidade seguia o seu ritmo, indiferente. Do lado de dentro, " +
    "o tempo tinha parado entre um parágrafo e outro.\n\n" +
    "Ela respirou fundo. Sabia que, quando chegasse ao fim do capítulo, nada " +
    "seria exatamente como antes. Era isso que ela mais amava nos livros: " +
    "a promessa silenciosa de que a próxima página poderia mudar tudo.\n\n" +
    "Marcou o trecho com cuidado, como quem guarda um segredo, e continuou lendo."

@Composable
fun TelaLeitura(
    livroId: Int,
    livros: List<Livro>,
    onAtualizarLivro: (Livro) -> Unit,
    onVoltar: () -> Unit
) {
    val livro = livros.find { it.id == livroId }

    if (livro == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarraDoTopo(titulo = "Leitura", onVoltar = onVoltar)
            Text("Livro não encontrado.", color = Cores.Cinza, modifier = Modifier.padding(16.dp))
        }
        return
    }

    var tamanhoFonte by rememberSaveable { mutableStateOf(17) }
    var favoritado by rememberSaveable { mutableStateOf(false) }

    val paginaAtual = (livro.paginasLidas + 1).coerceAtMost(livro.totalPaginas)
    val capitulo = (paginaAtual - 1) / PAGINAS_POR_CAPITULO + 1

    fun irParaPagina(paginasLidas: Int) {
        onAtualizarLivro(livro.copy(paginasLidas = paginasLidas.coerceIn(0, livro.totalPaginas)))
    }

    Column(modifier = Modifier.fillMaxSize()) {

        BarraDoTopo(
            titulo = livro.titulo,
            onVoltar = onVoltar,
            acoes = {
                IconButton(onClick = { favoritado = !favoritado }) {
                    Icon(
                        imageVector = if (favoritado) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = Cores.Vermelho
                    )
                }
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextButton(onClick = { if (tamanhoFonte > 12) tamanhoFonte-- }) {
                Text("A-", color = Cores.Creme, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text("$tamanhoFonte", color = Cores.Cinza, fontSize = 13.sp)
            TextButton(onClick = { if (tamanhoFonte < 28) tamanhoFonte++ }) {
                Text("A+", color = Cores.Creme, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Capítulo $capitulo",
                color = Cores.Vermelho,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = TEXTO_EXEMPLO,
                color = Cores.Creme,
                fontSize = tamanhoFonte.sp,
                lineHeight = (tamanhoFonte * 1.5).sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(modifier = Modifier.padding(16.dp)) {
            BarraDeProgresso(porcentagem = livro.porcentagemLida())
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Página $paginaAtual de ${livro.totalPaginas} • ${livro.porcentagemLida()}%",
                color = Cores.Cinza,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { irParaPagina(livro.paginasLidas - 1) },
                    enabled = livro.paginasLidas > 0
                ) {
                    Text("◀ Anterior", color = Cores.Creme)
                }
                OutlinedButton(
                    onClick = { irParaPagina(livro.paginasLidas + 1) },
                    enabled = livro.paginasLidas < livro.totalPaginas
                ) {
                    Text("Próxima ▶", color = Cores.Creme)
                }
            }
        }
    }
}
