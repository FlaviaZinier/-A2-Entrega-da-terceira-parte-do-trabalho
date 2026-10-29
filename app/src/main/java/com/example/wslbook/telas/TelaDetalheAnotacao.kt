package com.example.wslbook.telas

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.Anotacao
import com.example.wslbook.Cores
import com.example.wslbook.Livro
import com.example.wslbook.componentes.BarraDeProgresso
import com.example.wslbook.componentes.BarraDoTopo
import com.example.wslbook.componentes.CapaDoLivro

@Composable
fun TelaDetalheAnotacao(
    anotacaoId: Int,
    livros: List<Livro>,
    anotacoes: List<Anotacao>,
    onAtualizarAnotacao: (Anotacao) -> Unit,
    onExcluirAnotacao: (Anotacao) -> Unit,
    onVoltar: () -> Unit,
    onAbrirLivro: (Int) -> Unit
) {
    val anotacao = anotacoes.find { it.id == anotacaoId }

    if (anotacao == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarraDoTopo(titulo = "Anotação", onVoltar = onVoltar)
            Text("Anotação não encontrada.", color = Cores.Cinza, modifier = Modifier.padding(16.dp))
        }
        return
    }

    val livro = livros.find { it.id == anotacao.livroId }

    val posicaoNoLivro = if (livro != null && livro.totalPaginas > 0)
        (anotacao.pagina * 100) / livro.totalPaginas else 0

    val outrasDoMesmoLivro = anotacoes.count { it.livroId == anotacao.livroId && it.id != anotacao.id }

    Column(modifier = Modifier.fillMaxSize()) {

        BarraDoTopo(
            titulo = "Anotação",
            onVoltar = onVoltar,
            acoes = {
                IconButton(onClick = {
                    onAtualizarAnotacao(anotacao.copy(favorita = !anotacao.favorita))
                }) {
                    Icon(
                        imageVector = if (anotacao.favorita) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = Cores.Vermelho
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Cores.Vermelho, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "“${anotacao.trecho}”",
                    color = Cores.Creme,
                    fontSize = 22.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Página ${anotacao.pagina}", color = Cores.Vermelho, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (livro != null) {
                Text("Onde está este trecho", color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(8.dp))
                BarraDeProgresso(porcentagem = posicaoNoLivro)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Página ${anotacao.pagina} de ${livro.totalPaginas} • $posicaoNoLivro% do livro",
                    color = Cores.Cinza,
                    fontSize = 13.sp
                )
                Text(
                    text = if (anotacao.pagina <= livro.paginasLidas) "Você já passou deste trecho."
                    else "Você ainda não chegou neste trecho.",
                    color = Cores.Cinza,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("Do livro", color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Cores.FundoCard),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAbrirLivro(livro.id) }     
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        CapaDoLivro(titulo = livro.titulo, capa = livro.capa, modifier = Modifier.size(50.dp, 75.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(livro.titulo, color = Cores.Creme, fontWeight = FontWeight.Bold)
                            Text(livro.autor, color = Cores.Cinza, fontSize = 13.sp)
                            Text(
                                text = "+ $outrasDoMesmoLivro outra(s) anotação(ões) deste livro",
                                color = Cores.Vermelho,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Abrir livro",
                            tint = Cores.Creme
                        )
                    }
                }
            } else {
                Text("O livro desta anotação foi removido.", color = Cores.Cinza)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    onExcluirAnotacao(anotacao)
                    onVoltar()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = Cores.Vermelho)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Excluir anotação", color = Cores.Vermelho, fontWeight = FontWeight.Bold)
            }
        }
    }
}
