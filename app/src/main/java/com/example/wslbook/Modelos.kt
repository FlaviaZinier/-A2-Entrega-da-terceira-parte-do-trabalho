package com.example.wslbook

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// =====================================================================
// FICHA 1: LIVRO, agora também uma TABELA do banco de dados (Room)
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
)  {

    fun porcentagemLida(): Int =
        if (totalPaginas <= 0) 0 else (paginasLidas * 100) / totalPaginas

    fun paginasRestantes(): Int = totalPaginas - paginasLidas

    fun status(): String = when {
        paginasLidas <= 0 -> "Quero ler"
        paginasLidas >= totalPaginas -> "Lido"
        else -> "Lendo"
    }
}

// =====================================================================
// FICHA 2: ANOTAÇÃO, tabela "anotacoes"
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

object Cores {
    val Fundo = Color(0xFF121212)
    val FundoCard = Color(0xFF1E1E1E)
    val Vermelho = Color(0xFFD9364F)
    val Creme = Color(0xFFFFF8E7)
    val Cinza = Color(0xFF9E9E9E)
}

// ---------- CAPAS ----------
// Converte o NOME guardado no banco no número da imagem (R.drawable).
// Se o nome não existir, usa a capa padrão.
fun capaPorNome(nome: String): Int = when (nome) {
    "img" -> R.drawable.img
    "img_1" -> R.drawable.img_1
    "img_2" -> R.drawable.img_2
    "img_3" -> R.drawable.img_3
    "img_4" -> R.drawable.img_4
    "img_5" -> R.drawable.img_5
    "img_6" -> R.drawable.img_6
    "img_7" -> R.drawable.img_7
    "img_8" -> R.drawable.img_8
    "img_9" -> R.drawable.img_9
    "img_10" -> R.drawable.img_10
    else -> R.drawable.capa
}

val generos = listOf("Romance", "Fantasia", "Terror", "Suspense", "Ficção")

fun livrosIniciais(): List<Livro> = listOf(
    //    id  título                                   autor(a)              gênero      págs  lidas  capa
    Livro(1,  "Harry Potter e a Pedra Filosofal",      "J.K. Rowling",       "Fantasia", 264,  264,   "img_10"),
    Livro(2,  "O Príncipe Cruel",                      "Holly Black",        "Fantasia", 312,  150,   "img_3"),
    Livro(3,  "Noiva",                                 "Ali Hazelwood",      "Romance",  416,  0,     "img_4"),
    Livro(4,  "Amor Bobo",                             "Thaiza Diniz",       "Romance",  304,  304,   "img_8"),
    Livro(5,  "Nos Constellations",                    "Florence Quentin",   "Romance",  288,  0,     "img_9"),
    Livro(6,  "Saboroso Cadáver",                      "Agustina Bazterrica","Terror",   256,  90,    "img_6"),
    Livro(7,  "Não Pisque",                            "Stephen King",       "Terror",   320,  0,     "img_7"),
    Livro(8,  "Lady Killers: Assassinas em Série",     "Tori Telfer",        "Suspense", 336,  336,   "img_1"),
    Livro(9,  "1984",                                  "George Orwell",      "Ficção",   416,  208,   "img_5"),
    Livro(10, "A Biblioteca da Meia-Noite",            "Matt Haig",          "Ficção",   308,  0,     "img_2"),
    Livro(11, "A Princesa Salva a Si Mesma Neste Livro","Amanda Lovelace",   "Ficção",   208,  208,   "img")
)

fun anotacoesIniciais(): List<Anotacao> = listOf(
    Anotacao(1, 1, "Primeira vez que entrei em Hogwarts. Reler nas férias!", 80, true),
    Anotacao(2, 2, "A Jude é muito mais esperta do que todo mundo pensa.", 120, true),
    Anotacao(3, 9, "Guerra é paz. Liberdade é escravidão. Ignorância é força.", 26, true),
    Anotacao(4, 6, "Não consegui dormir depois deste capítulo...", 60),
    Anotacao(5, 8, "Inacreditável que essas histórias são reais.", 150)
)
