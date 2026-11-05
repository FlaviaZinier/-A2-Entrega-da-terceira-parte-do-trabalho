package com.example.wslbook.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wslbook.data.local.AppDatabase
import com.example.wslbook.data.repository.AnotacaoRepository
import com.example.wslbook.data.repository.LivroRepository

// =====================================================================
// AppViewModelFactory: o Android não sabe criar sozinho um ViewModel que
// recebe parâmetros no construtor. A Factory é a "fábrica" que monta cada
// ViewModel já entregando o Repository certo para ele.
// Uma Factory só, reaproveitada pelos 4 ViewModels.
// =====================================================================
class AppViewModelFactory(
    private val livroRepository: LivroRepository,
    private val anotacaoRepository: AnotacaoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LivrosViewModel::class.java) ->
                LivrosViewModel(livroRepository, anotacaoRepository) as T

            modelClass.isAssignableFrom(AnotacoesViewModel::class.java) ->
                AnotacoesViewModel(anotacaoRepository, livroRepository) as T

            modelClass.isAssignableFrom(DetalheLivroViewModel::class.java) ->
                DetalheLivroViewModel(livroRepository, anotacaoRepository) as T

            modelClass.isAssignableFrom(LivroFormViewModel::class.java) ->
                LivroFormViewModel(livroRepository) as T

            else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
        }
    }

    companion object {
        // Monta tudo a partir do banco: AppDatabase -> DAOs -> Repositories -> Factory
        fun criar(context: Context): AppViewModelFactory {
            val db = AppDatabase.getDatabase(context)
            return AppViewModelFactory(
                LivroRepository(db.livroDAO()),
                AnotacaoRepository(db.anotacaoDAO())
            )
        }
    }
}
