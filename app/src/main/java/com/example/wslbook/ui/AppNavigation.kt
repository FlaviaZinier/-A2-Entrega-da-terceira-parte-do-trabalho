package com.example.wslbook.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wslbook.ui.componentes.BarraDeNavegacao
import com.example.wslbook.ui.telas.TelaAnotacoes
import com.example.wslbook.ui.telas.TelaBiblioteca
import com.example.wslbook.ui.telas.TelaDetalheAnotacao
import com.example.wslbook.ui.telas.TelaDetalheLivro
import com.example.wslbook.ui.telas.TelaInicio
import com.example.wslbook.ui.telas.TelaLeitura
import com.example.wslbook.ui.telas.TelaNovoLivro
import com.example.wslbook.ui.telas.TelaPerfil
import com.example.wslbook.ui.theme.Cores
import com.example.wslbook.viewmodel.AnotacoesViewModel
import com.example.wslbook.viewmodel.AppViewModelFactory
import com.example.wslbook.viewmodel.DetalheLivroViewModel
import com.example.wslbook.viewmodel.LivroFormViewModel
import com.example.wslbook.viewmodel.LivrosViewModel

// =====================================================================
// AppNavigation.kt
// O "mapa da casa": diz qual tela mora em qual endereço (rota) e entrega
// a cada tela o ViewModel dela. As telas não sabem que o banco existe.
// =====================================================================
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // A Factory monta a "linha de montagem": banco -> DAOs -> Repositories.
    // remember: monta uma vez só, e não a cada redesenho.
    val context = LocalContext.current
    val factory = remember { AppViewModelFactory.criar(context) }

    // ViewModels COMPARTILHADOS entre várias telas (vivem enquanto o app estiver aberto)
    val livrosViewModel: LivrosViewModel = viewModel(factory = factory)
    val anotacoesViewModel: AnotacoesViewModel = viewModel(factory = factory)

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
                TelaInicio(
                    viewModel = livrosViewModel,
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) }
                )
            }

            // ---------- 2. BIBLIOTECA (lista de livros) ----------
            composable(Rotas.BIBLIOTECA) {
                TelaBiblioteca(
                    viewModel = livrosViewModel,
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) },
                    onNovoLivro = { navController.navigate(Rotas.NOVO_LIVRO) }
                )
            }

            // ---------- 3. NOVO LIVRO ----------
            // viewModel() DENTRO do composable: cada abertura do formulário
            // ganha um LivroFormViewModel novo (campos vazios)
            composable(Rotas.NOVO_LIVRO) {
                val formViewModel: LivroFormViewModel = viewModel(factory = factory)
                TelaNovoLivro(
                    viewModel = formViewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }

            // ---------- 4. EDITAR LIVRO (mesma tela, recebendo o id) ----------
            composable(
                route = Rotas.EDITAR_LIVRO,
                arguments = listOf(navArgument(Rotas.ARG_LIVRO_ID) { type = NavType.IntType })
            ) { entrada ->
                val livroId = entrada.arguments?.getInt(Rotas.ARG_LIVRO_ID) ?: -1
                val formViewModel: LivroFormViewModel = viewModel(factory = factory)
                TelaNovoLivro(
                    viewModel = formViewModel,
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
                val detalheViewModel: DetalheLivroViewModel = viewModel(factory = factory)
                TelaDetalheLivro(
                    livroId = livroId,
                    viewModel = detalheViewModel,
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
                    viewModel = livrosViewModel,
                    onVoltar = { navController.popBackStack() }
                )
            }

            // ---------- 7. ANOTAÇÕES (lista) ----------
            composable(Rotas.ANOTACOES) {
                TelaAnotacoes(
                    viewModel = anotacoesViewModel,
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
                    viewModel = anotacoesViewModel,
                    onVoltar = { navController.popBackStack() },
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) }
                )
            }

            // ---------- 9. PERFIL ----------
            composable(Rotas.PERFIL) {
                TelaPerfil(
                    viewModel = livrosViewModel,
                    onAbrirLivro = { id -> navController.navigate(Rotas.detalheLivro(id)) }
                )
            }
        }
    }
}
