package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.KeyboardRepository
import com.example.data.KeyboardTheme
import com.example.data.QuickPhrase
import com.example.data.TypingStat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KeyboardRepository

    val allThemes: StateFlow<List<KeyboardTheme>>
    val allPhrases: StateFlow<List<QuickPhrase>>
    val recentStats: StateFlow<List<TypingStat>>
    val totalKeystrokes: StateFlow<Int?>
    val averageWpm: StateFlow<Float?>

    private val _activeTheme = MutableStateFlow<KeyboardTheme>(KeyboardTheme.PRESETS[0])
    val activeTheme: StateFlow<KeyboardTheme> = _activeTheme.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).keyboardDao()
        repository = KeyboardRepository(dao)

        allThemes = repository.allThemes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = KeyboardTheme.PRESETS
        )

        allPhrases = repository.allPhrases.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuickPhrase.DEFAULT_PHRASES
        )

        recentStats = repository.recentStats.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        totalKeystrokes = repository.totalKeystrokes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        averageWpm = repository.averageWpm.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )
    }

    fun selectTheme(theme: KeyboardTheme) {
        _activeTheme.value = theme
    }

    fun updateActiveTheme(updated: KeyboardTheme) {
        _activeTheme.value = updated
    }

    fun saveCustomTheme(theme: KeyboardTheme) {
        viewModelScope.launch {
            val id = repository.saveTheme(theme)
            _activeTheme.value = theme.copy(id = id)
        }
    }

    fun deleteTheme(theme: KeyboardTheme) {
        viewModelScope.launch {
            repository.deleteTheme(theme)
            if (_activeTheme.value.id == theme.id) {
                _activeTheme.value = KeyboardTheme.PRESETS[0]
            }
        }
    }

    fun addQuickPhrase(title: String, text: String, category: String) {
        if (title.isNotBlank() && text.isNotBlank()) {
            viewModelScope.launch {
                repository.savePhrase(QuickPhrase(title = title, text = text, category = category))
            }
        }
    }

    fun deleteQuickPhrase(phrase: QuickPhrase) {
        viewModelScope.launch {
            repository.deletePhrase(phrase)
        }
    }

    fun logTypingSession(wpm: Int, characterCount: Int, durationSeconds: Int, topKey: String) {
        viewModelScope.launch {
            repository.logTypingSession(wpm, characterCount, durationSeconds, topKey)
        }
    }
}
