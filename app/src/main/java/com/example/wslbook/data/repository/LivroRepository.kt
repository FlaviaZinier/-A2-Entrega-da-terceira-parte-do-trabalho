package com.example.wslbook.data.repository

import com.example.wslbook.data.local.Livro
import com.example.wslbook.data.local.LivroDAO
import com.example.wslbook.data.local.livrosIniciais
import kotlinx.coroutines.flow.Flow

// =====================================================================
// LivroRepository: a ÚNICA porta de entrada para os dados de livros.
// O ViewModel nunca fala com o DAO, só com o Repository.
// Hoje ele só repassa para o Room. Se um dia os livros vierem de uma
// API, só esta classe muda: ViewModel e telas continuam iguais.
// =====================================================================
class LivroRepository(private val livroDAO: LivroDAO) {

    fun buscarTodos(): Flow<List<Livro>> = livroDAO.buscarTodos()

    fun observarPorId(id: Int): Flow<Livro?> = livroDAO.observarPorId(id)

    suspend fun buscarPorId(id: Int): Livro? = livroDAO.buscarPorId(id)

    suspend fun inserir(livro: Livro) = livroDAO.inserir(livro)

    suspend fun atualizar(livro: Livro) = livroDAO.atualizar(livro)

    suspend fun deletar(livro: Livro) = livroDAO.deletar(livro)

    // Na primeira vez que o app abre, o banco está vazio: coloca os exemplos.
    // Devolve true se inseriu (para as anotações de exemplo virem logo depois).
    suspend fun inserirExemplosSeVazio(): Boolean {
        if (livroDAO.contar() > 0) return false
        livroDAO.inserirTodos(livrosIniciais())
        return true
    }
}
