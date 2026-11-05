package com.example.wslbook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// =====================================================================
// MODEL: tabela "livros" do banco (Room)
// @Entity = "esta data class vira uma tabela chamada livros"
// =====================================================================
@Entity(tableName = "livros")
data class Livro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // o banco gera o id sozinho (1, 2, 3...)
    val titulo: String,
    val autor: String,
    val genero: String,
    val totalPaginas: Int,
    val paginasLidas: Int = 0,
    // NOME da imagem em res/drawable (ex: "img_10"), e não o número R.drawable.
    // Os números R.drawable mudam quando o app é recompilado; o nome não muda.
    val capa: String = "capa"
) {
    // Valores CALCULADOS (não são salvos no banco)
    fun porcentagemLida(): Int =
        if (totalPaginas <= 0) 0 else (paginasLidas * 100) / totalPaginas

    fun paginasRestantes(): Int = totalPaginas - paginasLidas

    fun status(): String = when {
        paginasLidas <= 0 -> "Quero ler"
        paginasLidas >= totalPaginas -> "Lido"
        else -> "Lendo"
    }
}
