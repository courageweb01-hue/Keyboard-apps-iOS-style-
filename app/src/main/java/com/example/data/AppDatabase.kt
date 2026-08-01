package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [KeyboardTheme::class, QuickPhrase::class, TypingStat::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keyboardDao(): KeyboardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "glass_keyboard_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Populate default themes and quick phrases on first launch
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).keyboardDao()
                            KeyboardTheme.PRESETS.forEach { dao.insertTheme(it) }
                            QuickPhrase.DEFAULT_PHRASES.forEach { dao.insertPhrase(it) }
                            // Add initial sample stat
                            dao.insertStat(TypingStat(wpm = 52, characterCount = 280, durationSeconds = 60))
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
