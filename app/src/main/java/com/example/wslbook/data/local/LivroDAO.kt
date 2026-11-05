package com.example.wslbook.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// =====================================================================
// LivroDAO: os "pedidos" que podem ser feitos à tabela livros.
// Só o LivroRepository conversa com este DAO.
// =====================================================================
@Dao
interface LivroDAO {

    // Flow = uma lista "ao vivo". O Room avisa sozinho sempre que a tabela
    // muda (inserir, atualizar, deletar), sem precisar buscar de novo.
    // Não é "suspend": o Flow já é assíncrono por natureza.
    @Query("SELECT * FROM livros ORDER BY id")
    fun buscarTodos(): Flow<List<Livro>>

    // Um livro só, também "ao vivo" (usado pelo Detalhe: o +10 pág aparece na hora)
    @Query("SELECT * FROM livros WHERE id = :id")
    fun observarPorId(id: Int): Flow<Livro?>

    // Um livro só, uma vez (usado para preencher o formulário de Editar)
    @Query("SELECT * FROM livros WHERE id = :id")
    suspend fun buscarPorId(id: Int): Livro?

    @Query("SELECT COUNT(*) FROM livros")
    suspend fun contar(): Int

    @Insert
    suspend fun inserir(livro: Livro)

    @Insert
    suspend fun inserirTodos(livros: List<Livro>)

    @Update
    suspend fun atualizar(livro: Livro)

    @Delete
    suspend fun deletar(livro: Livro)
}
