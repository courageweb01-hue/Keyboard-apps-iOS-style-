package com.example

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.data.AppDatabase
import com.example.data.KeyboardTheme
import com.example.data.QuickPhrase
import com.example.ui.keyboard.GlassKeyboardView
import com.example.ui.theme.GlassKeyboardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class GlassKeyboardService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var activeTheme: KeyboardTheme = KeyboardTheme.PRESETS[0]
    private var quickPhrases: List<QuickPhrase> = QuickPhrase.DEFAULT_PHRASES

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        // Load active theme and quick phrases from database
        serviceScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.keyboardDao()
                val themes = dao.getAllThemes().firstOrNull()
                if (!themes.isNullOrEmpty()) {
                    activeTheme = themes.first()
                }
                val phrases = dao.getAllPhrases().firstOrNull()
                if (!phrases.isNullOrEmpty()) {
                    quickPhrases = phrases
                }
            } catch (_: Exception) {
                // Fallback to defaults
            }
        }
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@GlassKeyboardService)
            setViewTreeViewModelStoreOwner(this@GlassKeyboardService)
            setViewTreeSavedStateRegistryOwner(this@GlassKeyboardService)
            setContent {
                GlassKeyboardTheme {
                    GlassKeyboardView(
                        theme = activeTheme,
                        quickPhrases = quickPhrases,
                        onKeyPress = { text ->
                            currentInputConnection?.commitText(text, 1)
                        },
                        onBackspace = {
                            val selectedText = currentInputConnection?.getSelectedText(0)
                            if (!selectedText.isNullOrEmpty()) {
                                currentInputConnection?.commitText("", 1)
                            } else {
                                currentInputConnection?.deleteSurroundingText(1, 0)
                            }
                        },
                        onEnter = {
                            val action = currentInputEditorInfo?.actionId ?: 0
                            if (action != 0) {
                                currentInputConnection?.performEditorAction(action)
                            } else {
                                currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                                currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                            }
                        },
                        onSpace = {
                            currentInputConnection?.commitText(" ", 1)
                        },
                        onPhraseSelect = { phrase ->
                            serviceScope.launch(Dispatchers.IO) {
                                try {
                                    AppDatabase.getDatabase(applicationContext).keyboardDao().incrementPhraseUsage(phrase.id)
                                } catch (_: Exception) {}
                            }
                        }
                    )
                }
            }
        }
        return composeView
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }
}
