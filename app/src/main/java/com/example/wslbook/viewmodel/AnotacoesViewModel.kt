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
// UiState da tela de Anotações (e do Detalhe da anotação):
// a lista, os livros para escolher, o FORMULÁRIO e o filtro.
// ---------------------------------------------------------------------
data class AnotacoesUiState(
    val anotacoes: List<Anotacao> = emptyList(),
    val livros: List<Livro> = emptyList(),      // para escolher o livro e mostrar o título
    val carregando: Boolean = true,
    // formulário "Nova anotação"
    val formularioAberto: Boolean = false,
    val livroEscolhidoId: Int? = null,
    val trecho: String = "",
    val paginaTexto: String = "",
    val erro: String? = null,
    // filtro da lista
    val soFavoritas: Boolean = false
)

// =====================================================================
// AnotacoesViewModel: usado pelas telas de Anotações e Detalhe da anotação
// =====================================================================
class AnotacoesViewModel(
    private val anotacaoRepository: AnotacaoRepository,
    private val livroRepository: LivroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnotacoesUiState())
    val uiState: StateFlow<AnotacoesUiState> = _uiState.asStateFlow()

    init {
        // Escuta as duas tabelas. Só troca as LISTAS: o que está
        // digitado no formulário continua como está.
        viewModelScope.launch {
            combine(
                anotacaoRepository.buscarTodos(),
                livroRepository.buscarTodos()
            ) { anotacoes, livros -> anotacoes to livros }
                .collect { (anotacoes, livros) ->
                    _uiState.value = _uiState.value.copy(
                        anotacoes = anotacoes,
                        livros = livros,
                        carregando = false
                    )
                }
        }
    }

    // ---------- FORMULÁRIO ----------
    fun alternarFormulario() {
        _uiState.value = _uiState.value.copy(formularioAberto = !_uiState.value.formularioAberto)
    }

    fun escolherLivro(livroId: Int) {
        _uiState.value = _uiState.value.copy(livroEscolhidoId = livroId)
    }

    fun mudarTrecho(texto: String) {
        _uiState.value = _uiState.value.copy(trecho = texto)
    }

    fun mudarPagina(texto: String) {
        _uiState.value = _uiState.value.copy(paginaTexto = texto.filter { it.isDigit() })
    }

    fun mostrarSoFavoritas(soFavoritas: Boolean) {
        _uiState.value = _uiState.value.copy(soFavoritas = soFavoritas)
    }

    // Valida o formulário e, se estiver tudo certo, salva no banco
    fun adicionarAnotacao() {
        val estado = _uiState.value
        val pagina = estado.paginaTexto.toIntOrNull()
        val livro = estado.livros.find { it.id == estado.livroEscolhidoId }

        val erro = when {
            livro == null -> "Escolha o livro."
            estado.trecho.isBlank() -> "Escreva o trecho."
            pagina == null || pagina <= 0 -> "Informe a página."
            pagina > livro.totalPaginas -> "Esse livro só tem ${livro.totalPaginas} páginas."
            else -> null
        }

        if (erro != null || livro == null || pagina == null) {
            _uiState.value = estado.copy(erro = erro)
            return
        }

        viewModelScope.launch {
            try {
                anotacaoRepository.inserir(
                    Anotacao(livroId = livro.id, trecho = estado.trecho.trim(), pagina = pagina)
                )
                // Limpa o formulário para a próxima
                _uiState.value = _uiState.value.copy(
                    trecho = "", paginaTexto = "", erro = null, formularioAberto = false
                )
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao inserir anotação: ${e.message}")
            }
        }
    }

    // ---------- AÇÕES NA LISTA / NO DETALHE ----------
    fun marcarFavorita(anotacao: Anotacao, favorita: Boolean) {
        viewModelScope.launch {
            try {
                anotacaoRepository.atualizar(anotacao.copy(favorita = favorita))
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao atualizar anotação: ${e.message}")
            }
        }
    }

    fun deletarAnotacao(anotacao: Anotacao) {
        viewModelScope.launch {
            try {
                anotacaoRepository.deletar(anotacao)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao deletar anotação: ${e.message}")
            }
        }
    }
}
