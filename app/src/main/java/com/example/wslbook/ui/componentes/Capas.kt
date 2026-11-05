package com.example.wslbook.ui.componentes

import com.example.wslbook.R

// ---------- CAPAS ----------
// Converte o NOME guardado no banco no número da imagem (R.drawable).
// Fica na camada de tela (ui) porque só serve para DESENHAR.
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
