package com.example.wslbook.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// =====================================================================
// AnotacaoDAO: os "pedidos" que podem ser feitos à tabela anotacoes.
// Só o AnotacaoRepository conversa com este DAO.
// =====================================================================
@Dao
interface AnotacaoDAO {

    // Lista "ao vivo" de todas as anotações
    @Query("SELECT * FROM anotacoes ORDER BY id")
    fun buscarTodos(): Flow<List<Anotacao>>

    // Só as anotações de UM livro, em ordem de página (consulta filtrada)
    @Query("SELECT * FROM anotacoes WHERE livroId = :livroId ORDER BY pagina")
    fun buscarPorLivro(livroId: Int): Flow<List<Anotacao>>

    @Insert
    suspend fun inserir(anotacao: Anotacao)

    @Insert
    suspend fun inserirTodas(anotacoes: List<Anotacao>)

    @Update
    suspend fun atualizar(anotacao: Anotacao)

    @Delete
    suspend fun deletar(anotacao: Anotacao)
}
