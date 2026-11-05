package com.example.wslbook.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wslbook.data.local.Anotacao
import com.example.wslbook.data.local.Livro
import com.example.wslbook.data.repository.AnotacaoRepository
import com.example.wslbook.data.repository.LivroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------
// UiState do Detalhe do livro: o livro, as anotações dele e se ainda
// está carregando.
// ---------------------------------------------------------------------
data class DetalheLivroUiState(
    val livro: Livro? = null,
    val anotacoes: List<Anotacao> = emptyList(),
    val carregando: Boolean = true
)

// =====================================================================
// DetalheLivroViewModel: busca UM livro direto do banco pelo id.
// Cada tela de Detalhe aberta ganha o seu próprio ViewModel.
// =====================================================================
class DetalheLivroViewModel(
    private val livroRepository: LivroRepository,
    private val anotacaoRepository: AnotacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalheLivroUiState())
    val uiState: StateFlow<DetalheLivroUiState> = _uiState.asStateFlow()

    private var idCarregado: Int? = null

    // Chamada pela tela com o id que veio da navegação
    fun carregar(livroId: Int) {
        if (idCarregado == livroId) return   // já está carregado (ex: depois de girar a tela)
        idCarregado = livroId

        viewModelScope.launch {
            // O livro e as anotações dele, "ao vivo": se o livro for editado
            // (+10 pág ou pelo formulário), a tela atualiza sozinha.
            combine(
                livroRepository.observarPorId(livroId),
                anotacaoRepository.buscarPorLivro(livroId)
            ) { livro, anotacoes ->
                DetalheLivroUiState(livro = livro, anotacoes = anotacoes, carregando = false)
            }.collect { novoEstado ->
                _uiState.value = novoEstado
            }
        }
    }

    // Botões -10 / +10 / Concluir
    fun atualizarPaginas(novoValor: Int) {
        val livro = _uiState.value.livro ?: return
        viewModelScope.launch {
            try {
                livroRepository.atualizar(
                    livro.copy(paginasLidas = novoValor.coerceIn(0, livro.totalPaginas))
                )
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao atualizar páginas: ${e.message}")
            }
        }
    }
}
