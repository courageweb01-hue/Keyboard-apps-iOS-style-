package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyboardDao {

    // Themes
    @Query("SELECT * FROM keyboard_themes ORDER BY id ASC")
    fun getAllThemes(): Flow<List<KeyboardTheme>>

    @Query("SELECT * FROM keyboard_themes WHERE id = :id LIMIT 1")
    suspend fun getThemeById(id: Long): KeyboardTheme?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: KeyboardTheme): Long

    @Delete
    suspend fun deleteTheme(theme: KeyboardTheme)

    // Quick Phrases
    @Query("SELECT * FROM quick_phrases ORDER BY usageCount DESC, id ASC")
    fun getAllPhrases(): Flow<List<QuickPhrase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrase(phrase: QuickPhrase): Long

    @Query("UPDATE quick_phrases SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementPhraseUsage(id: Long)

    @Delete
    suspend fun deletePhrase(phrase: QuickPhrase)

    // Typing Stats
    @Query("SELECT * FROM typing_stats ORDER BY timestamp DESC LIMIT 50")
    fun getRecentStats(): Flow<List<TypingStat>>

    @Query("SELECT SUM(characterCount) FROM typing_stats")
    fun getTotalKeystrokes(): Flow<Int?>

    @Query("SELECT AVG(wpm) FROM typing_stats WHERE wpm > 0")
    fun getAverageWpm(): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: TypingStat)
}
