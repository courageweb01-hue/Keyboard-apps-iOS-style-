package com.example.data

import kotlinx.coroutines.flow.Flow

class KeyboardRepository(private val dao: KeyboardDao) {

    val allThemes: Flow<List<KeyboardTheme>> = dao.getAllThemes()
    val allPhrases: Flow<List<QuickPhrase>> = dao.getAllPhrases()
    val recentStats: Flow<List<TypingStat>> = dao.getRecentStats()
    val totalKeystrokes: Flow<Int?> = dao.getTotalKeystrokes()
    val averageWpm: Flow<Float?> = dao.getAverageWpm()

    suspend fun getThemeById(id: Long): KeyboardTheme? = dao.getThemeById(id)

    suspend fun saveTheme(theme: KeyboardTheme): Long = dao.insertTheme(theme)

    suspend fun deleteTheme(theme: KeyboardTheme) = dao.deleteTheme(theme)

    suspend fun savePhrase(phrase: QuickPhrase): Long = dao.insertPhrase(phrase)

    suspend fun incrementPhraseUsage(id: Long) = dao.incrementPhraseUsage(id)

    suspend fun deletePhrase(phrase: QuickPhrase) = dao.deletePhrase(phrase)

    suspend fun logTypingSession(wpm: Int, characterCount: Int, durationSeconds: Int, topKey: String = "e") {
        if (characterCount > 0) {
            dao.insertStat(
                TypingStat(
                    wpm = wpm,
                    characterCount = characterCount,
                    durationSeconds = durationSeconds,
                    topKeyPressed = topKey
                )
            )
        }
    }
}
