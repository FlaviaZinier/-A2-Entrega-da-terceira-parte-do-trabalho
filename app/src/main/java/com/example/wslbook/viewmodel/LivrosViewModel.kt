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

data class LivrosUiState(
    val livros: List<Livro> = emptyList(),   
    val totalAnotacoes: Int = 0,         
    val carregando: Boolean = true     
)

class LivrosViewModel(
    private val livroRepository: LivroRepository,
    private val anotacaoRepository: AnotacaoRepository
) : ViewModel() {

    
    private val _uiState = MutableStateFlow(LivrosUiState())
    val uiState: StateFlow<LivrosUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                if (livroRepository.inserirExemplosSeVazio()) {
                    anotacaoRepository.inserirExemplos()
                }
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao inserir exemplos: ${e.message}")
            }
        }

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

    fun atualizarLivro(livro: Livro) {
        viewModelScope.launch {
            try {
                livroRepository.atualizar(livro)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao atualizar livro: ${e.message}")
            }
        }
    }

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
