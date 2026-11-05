package com.example.wslbook.data.repository

import com.example.wslbook.data.local.Anotacao
import com.example.wslbook.data.local.AnotacaoDAO
import com.example.wslbook.data.local.anotacoesIniciais
import kotlinx.coroutines.flow.Flow

// =====================================================================
// AnotacaoRepository: a ÚNICA porta de entrada para os dados de anotações.
// =====================================================================
class AnotacaoRepository(private val anotacaoDAO: AnotacaoDAO) {

    fun buscarTodos(): Flow<List<Anotacao>> = anotacaoDAO.buscarTodos()

    fun buscarPorLivro(livroId: Int): Flow<List<Anotacao>> = anotacaoDAO.buscarPorLivro(livroId)

    suspend fun inserir(anotacao: Anotacao) = anotacaoDAO.inserir(anotacao)

    suspend fun atualizar(anotacao: Anotacao) = anotacaoDAO.atualizar(anotacao)

    suspend fun deletar(anotacao: Anotacao) = anotacaoDAO.deletar(anotacao)

    suspend fun inserirExemplos() = anotacaoDAO.inserirTodas(anotacoesIniciais())
}
