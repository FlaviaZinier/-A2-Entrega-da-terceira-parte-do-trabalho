package com.example.wslbook.telas

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.wslbook.LivrosViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wslbook.Anotacao
import com.example.wslbook.Cores
import com.example.wslbook.Livro
import com.example.wslbook.componentes.BarraDeProgresso
import com.example.wslbook.componentes.BarraDoTopo
import com.example.wslbook.componentes.CapaDoLivro

private const val PAGINAS_POR_DIA = 20

// =====================================================================
// DETALHE DO LIVRO (Trabalho 3, seção 3.3)
// Esta tela recebe SÓ o id pela navegação e BUSCA o livro direto no banco
// (livroDAO.buscarPorId), em vez de receber o objeto pronto.
// As anotações também vêm do banco, já filtradas (anotacaoDAO.buscarPorLivro).
// =====================================================================
@Composable
fun TelaDetalheLivro(
    livroId: Int,
    viewModel: LivrosViewModel,
    onVoltar: () -> Unit,
    onEditar: (Int) -> Unit,
    onLer: (Int) -> Unit,
    onAbrirAnotacao: (Int) -> Unit
) {
    // O que veio do banco (começa vazio até a busca terminar)
    var livroDoBanco by remember { mutableStateOf<Livro?>(null) }
    var anotacoesDoLivro by remember { mutableStateOf<List<Anotacao>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    // LaunchedEffect: roda a busca assim que a tela abre.
    // As "chaves" (livroId e as listas do ViewModel) fazem a busca rodar de
    // novo quando algo muda no banco (ex: depois de editar ou de +10 pág).
    LaunchedEffect(livroId, viewModel.livros, viewModel.anotacoes) {
        livroDoBanco = viewModel.buscarLivroPorId(livroId)          // SELECT * FROM livros WHERE id = :id
        anotacoesDoLivro = viewModel.buscarAnotacoesDoLivro(livroId) // SELECT ... WHERE livroId = :livroId
        carregando = false
    }

    val livro = livroDoBanco

    if (livro == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            BarraDoTopo(titulo = "Livro", onVoltar = onVoltar)
            Text(
                text = if (carregando) "Carregando..." else "Livro não encontrado.",
                color = Cores.Cinza,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    // EDITAR (atualizar) no banco: mostra o valor novo na hora e salva com dao.atualizar
    fun atualizarPaginas(novoValor: Int) {
        val atualizado = livro.copy(paginasLidas = novoValor.coerceIn(0, livro.totalPaginas))
        livroDoBanco = atualizado
        viewModel.atualizarLivro(atualizado)
    }

    val porcentagem = livro.porcentagemLida()
    val restantes = livro.paginasRestantes()
    // Arredonda para cima: 25 páginas a 20/dia = 2 dias
    val diasParaTerminar = (restantes + PAGINAS_POR_DIA - 1) / PAGINAS_POR_DIA

    Column(modifier = Modifier.fillMaxSize()) {

        BarraDoTopo(
            titulo = livro.titulo,
            onVoltar = onVoltar,
            acoes = {
                // Lápis: abre o formulário de EDIÇÃO deste livro
                IconButton(onClick = { onEditar(livro.id) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar livro")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Row {
                CapaDoLivro(titulo = livro.titulo, capa = livro.capa, modifier = Modifier.size(110.dp, 165.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(livro.titulo, color = Cores.Creme, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(livro.autor, color = Cores.Cinza, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Gênero: ${livro.genero}", color = Cores.Creme, fontSize = 14.sp)
                    Text("${livro.totalPaginas} páginas", color = Cores.Creme, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                    ) {
                        Text(
                            text = livro.status(),
                            color = Cores.Vermelho,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Progresso de leitura", color = Cores.Creme, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            BarraDeProgresso(porcentagem = porcentagem)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${livro.paginasLidas} de ${livro.totalPaginas} páginas ($porcentagem%)",
                color = Cores.Creme,
                fontSize = 14.sp
            )
            Text(
                text = if (restantes == 0) "Leitura concluída! 🎉"
                else "Faltam $restantes páginas • cerca de $diasParaTerminar dia(s) lendo $PAGINAS_POR_DIA pág/dia",
                color = Cores.Cinza,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { atualizarPaginas(livro.paginasLidas - 10) }) {
                    Text("-10 pág", color = Cores.Creme)
                }
                OutlinedButton(onClick = { atualizarPaginas(livro.paginasLidas + 10) }) {
                    Text("+10 pág", color = Cores.Creme)
                }
                OutlinedButton(onClick = { atualizarPaginas(livro.totalPaginas) }) {
                    Text("Concluir", color = Cores.Creme)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onLer(livro.id) },
                colors = ButtonDefaults.buttonColors(containerColor = Cores.Vermelho),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Cores.Creme)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (livro.paginasLidas == 0) "Começar a ler" else "Continuar leitura",
                    color = Cores.Creme,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Anotações deste livro (${anotacoesDoLivro.size})",
                color = Cores.Creme,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (anotacoesDoLivro.isEmpty()) {
                Text(
                    text = "Nenhuma anotação ainda. Adicione pela aba Anotações.",
                    color = Cores.Cinza,
                    fontSize = 14.sp
                )
            } else {
                anotacoesDoLivro.forEach { anotacao ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Cores.FundoCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { onAbrirAnotacao(anotacao.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "“${anotacao.trecho}”",
                                    color = Cores.Creme,
                                    fontStyle = FontStyle.Italic,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text("pág. ${anotacao.pagina}", color = Cores.Cinza, fontSize = 12.sp)
                            }
                            if (anotacao.favorita) {
                                Icon(Icons.Filled.Favorite, contentDescription = "Favorita", tint = Cores.Vermelho)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
