package com.example.wslbook.dados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wslbook.Anotacao
import com.example.wslbook.Livro

// =====================================================================
// AppDatabase.kt
// O "arquivo do banco": diz quais tabelas existem (entities) e entrega
// os DAOs. Segue o mesmo modelo da aula (getDatabase + INSTANCE).
// =====================================================================
@Database(
    entities = [Livro::class, Anotacao::class],   // as 2 tabelas do app
    version = 1,
    exportSchema = false                          // não gera o arquivo de histórico do banco (evita aviso)
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun livroDAO(): LivroDAO
    abstract fun anotacaoDAO(): AnotacaoDAO

    companion object {
        // @Volatile: todas as partes do app enxergam o valor mais recente
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Devolve SEMPRE o mesmo banco (só cria na primeira vez)
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wslbook_database"                // nome do arquivo do banco no celular
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
