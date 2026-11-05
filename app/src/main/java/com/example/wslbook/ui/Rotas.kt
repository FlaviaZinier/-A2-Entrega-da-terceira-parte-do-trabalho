package com.example.wslbook.ui

// Endereços (rotas) de cada tela do app
object Rotas {
    const val INICIO = "inicio"
    const val BIBLIOTECA = "biblioteca"
    const val ANOTACOES = "anotacoes"
    const val PERFIL = "perfil"

    const val NOVO_LIVRO = "novo_livro"
    const val EDITAR_LIVRO = "editar_livro/{livroId}"

    const val ARG_LIVRO_ID = "livroId"
    const val ARG_ANOTACAO_ID = "anotacaoId"
    const val DETALHE_LIVRO = "detalhe_livro/{livroId}"
    const val DETALHE_ANOTACAO = "detalhe_anotacao/{anotacaoId}"
    const val LEITURA = "leitura/{livroId}"

    fun detalheLivro(id: Int) = "detalhe_livro/$id"
    fun detalheAnotacao(id: Int) = "detalhe_anotacao/$id"
    fun leitura(id: Int) = "leitura/$id"
    fun editarLivro(id: Int) = "editar_livro/$id"
}
