package com.example.wslbook.dados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.wslbook.Anotacao
import com.example.wslbook.Livro

@Dao
interface LivroDAO {

    @Insert
    suspend fun inserir(livro: Livro)

    @Query("SELECT * FROM livros ORDER BY id")
    suspend fun buscarTodos(): List<Livro>

    @Update
    suspend fun atualizar(livro: Livro)

    @Delete
    suspend fun excluir(livro: Livro)

    @Query("SELECT * FROM livros WHERE id = :id")
    suspend fun buscarPorId(id: Int): Livro?

    @Query("SELECT COUNT(*) FROM livros")
    suspend fun contar(): Int

    @Insert
    suspend fun inserirTodos(livros: List<Livro>)
}

@Dao
interface AnotacaoDAO {

    @Insert
    suspend fun inserir(anotacao: Anotacao)

    @Query("SELECT * FROM anotacoes ORDER BY id")
    suspend fun buscarTodas(): List<Anotacao>

    @Update
    suspend fun atualizar(anotacao: Anotacao)

    @Delete
    suspend fun excluir(anotacao: Anotacao)

    @Query("SELECT * FROM anotacoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Anotacao?

    @Query("SELECT * FROM anotacoes WHERE livroId = :livroId ORDER BY pagina")
    suspend fun buscarPorLivro(livroId: Int): List<Anotacao>

    @Insert
    suspend fun inserirTodas(anotacoes: List<Anotacao>)
}
