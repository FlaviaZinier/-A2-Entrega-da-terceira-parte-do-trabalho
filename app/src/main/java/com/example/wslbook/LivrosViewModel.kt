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

// =====================================================================
// LivrosViewModel.kt
// O ViewModel guarda as listas do app e conversa com o banco.
//
// Por que ele existe? Quando o celular GIRA, o Android destrói e recria
// a tela (a Activity). Tudo que estava em "remember" é perdido e a tela
// "recomeça". O ViewModel NÃO é destruído na rotação: as listas continuam
// aqui, prontas, e a tela só redesenha com elas.
//
// AndroidViewModel (e não só ViewModel) porque precisamos do "application"
// para abrir o banco de dados.
// =====================================================================
class LivrosViewModel(application: Application) : AndroidViewModel(application) {

    // Abre o banco (sempre o mesmo, padrão Singleton) e pega os DAOs
    private val db = AppDatabase.getDatabase(application)
    private val livroDAO = db.livroDAO()
    private val anotacaoDAO = db.anotacaoDAO()

    // ---------- ESTADO (o que as telas leem) ----------
    // mutableStateOf: quando muda, o Compose redesenha as telas
    // private set: só o ViewModel altera; as telas só leem
    var livros by mutableStateOf<List<Livro>>(emptyList())
        private set

    var anotacoes by mutableStateOf<List<Anotacao>>(emptyList())
        private set

    // init roda UMA vez, quando o ViewModel é criado (não roda de novo ao girar)
    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Na primeira vez que o app abre, o banco está vazio:
            // colocamos os livros e anotações de exemplo
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

    // =================================================================
    // BUSCAR (chamadas pelas telas de lista, no LaunchedEffect(Unit))
    // =================================================================
    fun carregarLivros() {
        viewModelScope.launch(Dispatchers.IO) { recarregarLivros() }
    }

    fun carregarAnotacoes() {
        viewModelScope.launch(Dispatchers.IO) { recarregarAnotacoes() }
    }

    // Busca no banco (thread IO) e entrega o resultado para a tela (thread Main)
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

    // Busca UM livro pelo id, direto no banco (usada pelo Detalhe e pela Edição)
    suspend fun buscarLivroPorId(id: Int): Livro? = withContext(Dispatchers.IO) {
        try {
            livroDAO.buscarPorId(id)
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar livro $id: ${e.message}")
            null
        }
    }

    // Busca as anotações de UM livro, direto no banco (usada pelo Detalhe do livro)
    suspend fun buscarAnotacoesDoLivro(livroId: Int): List<Anotacao> = withContext(Dispatchers.IO) {
        try {
            anotacaoDAO.buscarPorLivro(livroId)
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar anotações do livro $livroId: ${e.message}")
            emptyList()
        }
    }

    // Busca UMA anotação pelo id (usada pelo Detalhe da anotação)
    suspend fun buscarAnotacaoPorId(id: Int): Anotacao? = withContext(Dispatchers.IO) {
        try {
            anotacaoDAO.buscarPorId(id)
        } catch (e: Exception) {
            Log.e("WSLBOOK", "Erro ao buscar anotação $id: ${e.message}")
            null
        }
    }

    // =================================================================
    // LIVROS: inserir, atualizar, excluir
    // Cada operação roda no Dispatchers.IO e depois recarrega a lista.
    // =================================================================
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
                livroDAO.excluir(livro)   // as anotações dele somem junto (CASCADE)
            } catch (e: Exception) {
                Log.e("WSLBOOK", "Erro ao excluir livro: ${e.message}")
            }
            recarregarLivros()
            recarregarAnotacoes()
        }
    }

    // =================================================================
    // ANOTAÇÕES: inserir, atualizar, excluir
    // =================================================================
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
