package com.example.wslbook

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wslbook.dados.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LivrosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val livroDAO = db.livroDAO()
    private val anotacaoDAO = db.anotacaoDAO()

    var livros by mutableStateOf<List<Livro>>(emptyList())
        private set

    var anotacoes by mutableStateOf<List<Anotacao>>(emptyList())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (livroDAO.contar() == 0) {
                    livroDAO.inserirTodos(livrosIniciais())
                    anotacaoDAO.inserirTodas(anotacoesIniciais())
                }
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao inserir dados iniciais: ${e.message}")
            }
            recarregarLivros()
            recarregarAnotacoes()
        }
    }

    fun carregarLivros() {
        viewModelScope.launch(Dispatchers.IO) { recarregarLivros() }
    }

    fun carregarAnotacoes() {
        viewModelScope.launch(Dispatchers.IO) { recarregarAnotacoes() }
    }

    private suspend fun recarregarLivros() {
        try {
            val lista = livroDAO.buscarTodos()
            withContext(Dispatchers.Main) { livros = lista }
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar livros: ${e.message}")
        }
    }

    private suspend fun recarregarAnotacoes() {
        try {
            val lista = anotacaoDAO.buscarTodas()
            withContext(Dispatchers.Main) { anotacoes = lista }
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar anotações: ${e.message}")
        }
    }

    suspend fun buscarLivroPorId(id: Int): Livro? = withContext(Dispatchers.IO) {
        try {
            livroDAO.buscarPorId(id)
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar livro $id: ${e.message}")
            null
        }
    }

    suspend fun buscarAnotacoesDoLivro(livroId: Int): List<Anotacao> = withContext(Dispatchers.IO) {
        try {
            anotacaoDAO.buscarPorLivro(livroId)
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar anotações do livro $livroId: ${e.message}")
            emptyList()
        }
    }

    suspend fun buscarAnotacaoPorId(id: Int): Anotacao? = withContext(Dispatchers.IO) {
        try {
            anotacaoDAO.buscarPorId(id)
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar anotação $id: ${e.message}")
            null
        }
    }


    fun inserirLivro(livro: Livro) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                livroDAO.inserir(livro)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao inserir livro: ${e.message}")
            }
            recarregarLivros()
        }
    }

    fun atualizarLivro(livro: Livro) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                livroDAO.atualizar(livro)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao atualizar livro: ${e.message}")
            }
            recarregarLivros()
        }
    }

    fun excluirLivro(livro: Livro) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                livroDAO.excluir(livro)  
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao excluir livro: ${e.message}")
            }
            recarregarLivros()
            recarregarAnotacoes()
        }
    }

    fun inserirAnotacao(anotacao: Anotacao) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                anotacaoDAO.inserir(anotacao)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao inserir anotação: ${e.message}")
            }
            recarregarAnotacoes()
        }
    }

    fun atualizarAnotacao(anotacao: Anotacao) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                anotacaoDAO.atualizar(anotacao)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao atualizar anotação: ${e.message}")
            }
            recarregarAnotacoes()
        }
    }

    fun excluirAnotacao(anotacao: Anotacao) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                anotacaoDAO.excluir(anotacao)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao excluir anotação: ${e.message}")
            }
            recarregarAnotacoes()
        }
    }
}
