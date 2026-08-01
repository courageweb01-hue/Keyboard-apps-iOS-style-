package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_phrases")
data class QuickPhrase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val text: String,
    val category: String = "General", // General, Work, Emojis, Email, Social
    val usageCount: Int = 0
) {
    companion object {
        val DEFAULT_PHRASES = listOf(
            QuickPhrase(1, "On My Way", "On my way! 🏃‍♂️💨", "General", 5),
            QuickPhrase(2, "Thank You", "Thank you so much! 🙏✨", "General", 8),
            QuickPhrase(3, "BRB", "Be right back! ⏱️", "General", 3),
            QuickPhrase(4, "Sounds Good", "Sounds great, let's do it! 👍", "Work", 6),
            QuickPhrase(5, "Email Signature", "Best regards,\nSent from Glass Keyboard ⚡", "Email", 12),
            QuickPhrase(6, "Shrug Kaomoji", "¯\\_(ツ)_/¯", "Emojis", 15),
            QuickPhrase(7, "Table Flip", "(╯°□°)╯︵ ┻━┻", "Emojis", 10),
            QuickPhrase(8, "Call Me", "Can you give me a quick call when free?", "Work", 4)
        )
    }
}
