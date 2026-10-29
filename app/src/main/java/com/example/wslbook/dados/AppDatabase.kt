package com.example.wslbook.dados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wslbook.Anotacao
import com.example.wslbook.Livro

@Database(
    entities = [Livro::class, Anotacao::class],   
    version = 1,
    exportSchema = false                          
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun livroDAO(): LivroDAO
    abstract fun anotacaoDAO(): AnotacaoDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wslbook_database"             
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
