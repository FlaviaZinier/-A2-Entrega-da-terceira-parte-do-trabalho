package com.example.wslbook.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wslbook.data.local.Livro
import com.example.wslbook.data.local.generos
import com.example.wslbook.data.repository.LivroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------
// UiState do formulário Novo/Editar livro: o texto de cada campo,
// a mensagem de erro e se já foi salvo (para a tela voltar).
// ---------------------------------------------------------------------
data class LivroFormUiState(
    val livroId: Int = -1,                 // -1 = livro novo
    val titulo: String = "",
    val autor: String = "",
    val genero: String = generos.first(),
    val totalPaginasTexto: String = "",
    val paginasLidasTexto: String = "",
    val capa: String = "capa",
    val erro: String? = null,
    val salvo: Boolean = false
) {
    val editando: Boolean get() = livroId > 0
}

// =====================================================================
// LivroFormViewModel: usado pela tela de Novo livro e Editar livro.
// O texto digitado fica AQUI, então não some ao girar o celular.
// =====================================================================
class LivroFormViewModel(
    private val livroRepository: LivroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LivroFormUiState())
    val uiState: StateFlow<LivroFormUiState> = _uiState.asStateFlow()

    private var jaCarregou = false

    // Se for edição, busca o livro no banco e preenche os campos (só uma vez)
    fun carregar(livroId: Int) {
        if (jaCarregou) return
        jaCarregou = true
        if (livroId <= 0) return   // livro novo: campos vazios

        viewModelScope.launch {
            try {
                val livro = livroRepository.buscarPorId(livroId) ?: return@launch
                _uiState.value = _uiState.value.copy(
                    livroId = livro.id,
                    titulo = livro.titulo,
                    autor = livro.autor,
                    genero = livro.genero,
                    totalPaginasTexto = livro.totalPaginas.toString(),
                    paginasLidasTexto = livro.paginasLidas.toString(),
                    capa = livro.capa
                )
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao buscar livro $livroId: ${e.message}")
            }
        }
    }

    // ---------- campos do formulário ----------
    fun mudarTitulo(texto: String) { _uiState.value = _uiState.value.copy(titulo = texto) }
    fun mudarAutor(texto: String) { _uiState.value = _uiState.value.copy(autor = texto) }
    fun mudarGenero(genero: String) { _uiState.value = _uiState.value.copy(genero = genero) }
    fun mudarTotalPaginas(texto: String) {
        _uiState.value = _uiState.value.copy(totalPaginasTexto = texto.filter { it.isDigit() })
    }
    fun mudarPaginasLidas(texto: String) {
        _uiState.value = _uiState.value.copy(paginasLidasTexto = texto.filter { it.isDigit() })
    }

    // Valida e salva: INSERT se for novo, UPDATE se for edição
    fun salvar() {
        val estado = _uiState.value
        val total = estado.totalPaginasTexto.toIntOrNull()
        val lidas = estado.paginasLidasTexto.toIntOrNull() ?: 0

        val erro = when {
            estado.titulo.isBlank() -> "Informe o título do livro."
            total == null || total <= 0 -> "Informe o total de páginas."
            lidas > total -> "Páginas lidas não pode ser maior que o total."
            else -> null
        }
        if (erro != null || total == null) {
            _uiState.value = estado.copy(erro = erro)
            return
        }

        val livro = Livro(
            id = if (estado.editando) estado.livroId else 0,   // 0 = o banco gera o id
            titulo = estado.titulo.trim(),
            autor = estado.autor.trim().ifBlank { "Autor desconhecido" },
            genero = estado.genero,
            totalPaginas = total,
            paginasLidas = lidas,
            capa = estado.capa
        )

        viewModelScope.launch {
            try {
                if (estado.editando) livroRepository.atualizar(livro) else livroRepository.inserir(livro)
                _uiState.value = _uiState.value.copy(erro = null, salvo = true)   // a tela volta
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao salvar livro: ${e.message}")
                _uiState.value = _uiState.value.copy(erro = "Não foi possível salvar. Tente de novo.")
            }
        }
    }
}
