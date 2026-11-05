package com.example.wslbook.data.local

// Gêneros disponíveis no app
val generos = listOf("Romance", "Fantasia", "Terror", "Suspense", "Ficção")

// Livros e anotações de exemplo (entram no banco só na primeira vez que o app abre)
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
