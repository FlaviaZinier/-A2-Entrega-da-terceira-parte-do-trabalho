package com.example.wslbook.dados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.wslbook.Anotacao
import com.example.wslbook.Livro

// =====================================================================
// Daos.kt
// DAO = "Data Access Object": a lista de PEDIDOS que o app pode fazer
// ao banco. Todas as funções são "suspend": elas podem demorar, então
// só podem ser chamadas dentro de uma Coroutine (fora da thread principal).
// =====================================================================

@Dao
interface LivroDAO {

    // ---------- OS 4 BÁSICOS ----------
    @Insert
    suspend fun inserir(livro: Livro)

    @Query("SELECT * FROM livros ORDER BY id")
    suspend fun buscarTodos(): List<Livro>

    @Update
    suspend fun atualizar(livro: Livro)

    @Delete
    suspend fun excluir(livro: Livro)

    // ---------- CONSULTAS EXTRAS ----------
    // Consulta FILTRADA por campo: busca um livro só, pelo id.
    // ":id" é trocado pelo valor do parâmetro id. Devolve null se não achar.
    @Query("SELECT * FROM livros WHERE id = :id")
    suspend fun buscarPorId(id: Int): Livro?

    // Quantos livros existem (para saber se o banco está vazio na 1ª vez)
    @Query("SELECT COUNT(*) FROM livros")
    suspend fun contar(): Int

    // Insere vários de uma vez (usado para os dados de exemplo)
    @Insert
    suspend fun inserirTodos(livros: List<Livro>)
}

@Dao
interface AnotacaoDAO {

    // ---------- OS 4 BÁSICOS ----------
    @Insert
    suspend fun inserir(anotacao: Anotacao)

    @Query("SELECT * FROM anotacoes ORDER BY id")
    suspend fun buscarTodas(): List<Anotacao>

    @Update
    suspend fun atualizar(anotacao: Anotacao)

    @Delete
    suspend fun excluir(anotacao: Anotacao)

    // ---------- CONSULTAS EXTRAS ----------
    // Filtrada por id: usada no Detalhe da anotação
    @Query("SELECT * FROM anotacoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Anotacao?

    // Filtrada por livro: só as anotações de UM livro, em ordem de página.
    // Usada no Detalhe do livro.
    @Query("SELECT * FROM anotacoes WHERE livroId = :livroId ORDER BY pagina")
    suspend fun buscarPorLivro(livroId: Int): List<Anotacao>

    @Insert
    suspend fun inserirTodas(anotacoes: List<Anotacao>)
}
