package com.example.wslbook

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wslbook.componentes.BarraDeNavegacao
import com.example.wslbook.telas.TelaAnotacoes
import com.example.wslbook.telas.TelaBiblioteca
import com.example.wslbook.telas.TelaDetalheAnotacao
import com.example.wslbook.telas.TelaDetalheLivro
import com.example.wslbook.telas.TelaInicio
import com.example.wslbook.telas.TelaLeitura
import com.example.wslbook.telas.TelaNovoLivro
import com.example.wslbook.telas.TelaPerfil

// =====================================================================
// AppNavigation.kt
// O "mapa da casa": diz qual tela mora em qual endereço (rota).
// As listas agora moram no ViewModel, que conversa com o banco Room.
// =====================================================================
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // viewModel(): cria o ViewModel UMA vez. Ao girar o celular, devolve o
    // MESMO ViewModel, com as listas já carregadas (a tela não "recomeça").
    val viewModel: LivrosViewModel = viewModel()

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route
    val rotasComBarra = listOf(Rotas.INICIO, Rotas.BIBLIOTECA, Rotas.ANOTACOES, Rotas.PERFIL)

    Scaffold(
        containerColor = Cores.Fundo,
        bottomBar = {
            if (rotaAtual in rotasComBarra) {
                BarraDeNavegacao(navController = navController, rotaAtual = rotaAtual)
            }
        }
    ) { paddingInterno ->

        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(paddingInterno)
        ) {

            // ---------- 1. INÍCIO ----------
            composable(Rotas.INICIO) {
                LaunchedEffect(Unit) { viewModel.carregarLivros() }   // busca no banco ao abrir
                TelaInicio(
                    livros = viewModel.livros,
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) }
                )
            }

            // ---------- 2. BIBLIOTECA (lista de livros) ----------
            composable(Rotas.BIBLIOTECA) {
                LaunchedEffect(Unit) { viewModel.carregarLivros() }   // busca no banco ao abrir
                TelaBiblioteca(
                    livros = viewModel.livros,
                    onExcluirLivro = { livro -> viewModel.excluirLivro(livro) },
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) },
                    onNovoLivro = { navController.navigate(Rotas.NOVO_LIVRO) }
                )
            }

            // ---------- 3. NOVO LIVRO ----------
            composable(Rotas.NOVO_LIVRO) {
                TelaNovoLivro(
                    viewModel = viewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }

            // ---------- 4. EDITAR LIVRO (mesma tela, recebendo o id) ----------
            composable(
                route = Rotas.EDITAR_LIVRO,
                arguments = listOf(navArgument(Rotas.ARG_LIVRO_ID) { type = NavType.IntType })
            ) { entrada ->
                val livroId = entrada.arguments?.getInt(Rotas.ARG_LIVRO_ID) ?: -1
                TelaNovoLivro(
                    viewModel = viewModel,
                    onVoltar = { navController.popBackStack() },
                    livroId = livroId
                )
            }

            // ---------- 5. DETALHE DO LIVRO (busca no banco pelo id) ----------
            composable(
                route = Rotas.DETALHE_LIVRO,
                arguments = listOf(navArgument(Rotas.ARG_LIVRO_ID) { type = NavType.IntType })
            ) { entrada ->
                val livroId = entrada.arguments?.getInt(Rotas.ARG_LIVRO_ID) ?: -1
                TelaDetalheLivro(
                    livroId = livroId,
                    viewModel = viewModel,
                    onVoltar = { navController.popBackStack() },
                    onEditar = { id -> navController.navigate(Rotas.editarLivro(id)) },
                    onLer = { id -> navController.navigate(Rotas.leitura(id)) },
                    onAbrirAnotacao = { id -> navController.navigate(Rotas.detalheAnotacao(id)) }
                )
            }

            // ---------- 6. LEITURA ----------
            composable(
                route = Rotas.LEITURA,
                arguments = listOf(navArgument(Rotas.ARG_LIVRO_ID) { type = NavType.IntType })
            ) { entrada ->
                val livroId = entrada.arguments?.getInt(Rotas.ARG_LIVRO_ID) ?: -1
                TelaLeitura(
                    livroId = livroId,
                    livros = viewModel.livros,
                    onAtualizarLivro = { livro -> viewModel.atualizarLivro(livro) },
                    onVoltar = { navController.popBackStack() }
                )
            }

            // ---------- 7. ANOTAÇÕES (lista) ----------
            composable(Rotas.ANOTACOES) {
                LaunchedEffect(Unit) {                                 // busca no banco ao abrir
                    viewModel.carregarLivros()
                    viewModel.carregarAnotacoes()
                }
                TelaAnotacoes(
                    livros = viewModel.livros,
                    anotacoes = viewModel.anotacoes,
                    onAdicionarAnotacao = { a -> viewModel.inserirAnotacao(a) },
                    onAtualizarAnotacao = { a -> viewModel.atualizarAnotacao(a) },
                    onExcluirAnotacao = { a -> viewModel.excluirAnotacao(a) },
                    onAbrirAnotacao = { id -> navController.navigate(Rotas.detalheAnotacao(id)) }
                )
            }

            // ---------- 8. DETALHE DA ANOTAÇÃO ----------
            composable(
                route = Rotas.DETALHE_ANOTACAO,
                arguments = listOf(navArgument(Rotas.ARG_ANOTACAO_ID) { type = NavType.IntType })
            ) { entrada ->
                val anotacaoId = entrada.arguments?.getInt(Rotas.ARG_ANOTACAO_ID) ?: -1
                TelaDetalheAnotacao(
                    anotacaoId = anotacaoId,
                    livros = viewModel.livros,
                    anotacoes = viewModel.anotacoes,
                    onAtualizarAnotacao = { a -> viewModel.atualizarAnotacao(a) },
                    onExcluirAnotacao = { a -> viewModel.excluirAnotacao(a) },
                    onVoltar = { navController.popBackStack() },
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) }
                )
            }

            // ---------- 9. PERFIL ----------
            composable(Rotas.PERFIL) {
                LaunchedEffect(Unit) {
                    viewModel.carregarLivros()
                    viewModel.carregarAnotacoes()
                }
                TelaPerfil(
                    livros = viewModel.livros,
                    anotacoes = viewModel.anotacoes,
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) }
                )
            }
        }
    }
}
