package com.example.wslbook.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// =====================================================================
// MODEL: tabela "anotacoes"
// ForeignKey = cada anotação aponta para um livro que EXISTE na tabela livros.
// CASCADE = se o livro for apagado, as anotações dele são apagadas junto.
// =====================================================================
@Entity(
    tableName = "anotacoes",
    foreignKeys = [ForeignKey(
        entity = Livro::class,
        parentColumns = ["id"],
        childColumns = ["livroId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("livroId")]
)
data class Anotacao(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val livroId: Int,
    val trecho: String,
    val pagina: Int,
    val favorita: Boolean = false
)
