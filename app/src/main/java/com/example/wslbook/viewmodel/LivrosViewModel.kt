package com.example.wslbook.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wslbook.data.local.Livro
import com.example.wslbook.data.repository.AnotacaoRepository
import com.example.wslbook.data.repository.LivroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------
// UiState: TUDO que as telas de livros precisam para se desenhar,
// agrupado num lugar só (em vez de várias variáveis soltas).
// ---------------------------------------------------------------------
data class LivrosUiState(
    val livros: List<Livro> = emptyList(),   // a lista do Início, Biblioteca, Leitura e Perfil
    val totalAnotacoes: Int = 0,             // contador do Perfil
    val carregando: Boolean = true           // true até o banco responder pela 1ª vez
)

// =====================================================================
// LivrosViewModel: usado pelo Início, Biblioteca, Leitura e Perfil.
// Recebe os Repositories pelo construtor (quem entrega é a Factory).
// =====================================================================
class LivrosViewModel(
    private val livroRepository: LivroRepository,
    private val anotacaoRepository: AnotacaoRepository
) : ViewModel() {

    // _uiState: privado, SÓ o ViewModel altera
    private val _uiState = MutableStateFlow(LivrosUiState())
    // uiState: público e só de leitura, a tela apenas OBSERVA
    val uiState: StateFlow<LivrosUiState> = _uiState.asStateFlow()

    init {
        // 1) Primeira vez que o app abre: coloca os livros e anotações de exemplo
        viewModelScope.launch {
            try {
                if (livroRepository.inserirExemplosSeVazio()) {
                    anotacaoRepository.inserirExemplos()
                }
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao inserir exemplos: ${e.message}")
            }
        }

        // 2) Fica "escutando" o banco. combine junta os dois Flows: sempre que
        //    livros OU anotações mudarem, o uiState é atualizado sozinho.
        viewModelScope.launch {
            combine(
                livroRepository.buscarTodos(),
                anotacaoRepository.buscarTodos()
            ) { livros, anotacoes ->
                LivrosUiState(livros = livros, totalAnotacoes = anotacoes.size, carregando = false)
            }.collect { novoEstado ->
                _uiState.value = novoEstado
            }
        }
    }

    // ---------- AÇÕES (as telas chamam estas funções) ----------

    // Usada pela Leitura (Próxima/Anterior página)
    fun atualizarLivro(livro: Livro) {
        viewModelScope.launch {
            try {
                livroRepository.atualizar(livro)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao atualizar livro: ${e.message}")
            }
        }
    }

    // Lixeira da Biblioteca. As anotações do livro somem juntas (CASCADE).
    fun deletarLivro(livro: Livro) {
        viewModelScope.launch {
            try {
                livroRepository.deletar(livro)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao deletar livro: ${e.message}")
            }
        }
    }
}
