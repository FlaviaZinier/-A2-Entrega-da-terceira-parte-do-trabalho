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

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

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

            composable(Rotas.INICIO) {
                LaunchedEffect(Unit) { viewModel.carregarLivros() }   
                TelaInicio(
                    livros = viewModel.livros,
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) }
                )
            }

            composable(Rotas.BIBLIOTECA) {
                LaunchedEffect(Unit) { viewModel.carregarLivros() }  
                TelaBiblioteca(
                    livros = viewModel.livros,
                    onExcluirLivro = { livro -> viewModel.excluirLivro(livro) },
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) },
                    onNovoLivro = { navController.navigate(Rotas.NOVO_LIVRO) }
                )
            }

            composable(Rotas.NOVO_LIVRO) {
                TelaNovoLivro(
                    viewModel = viewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }

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

            composable(Rotas.ANOTACOES) {
                LaunchedEffect(Unit) {                                 
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
