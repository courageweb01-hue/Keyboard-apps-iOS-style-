package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "typing_stats")
data class TypingStat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val wpm: Int = 0,
    val characterCount: Int = 0,
    val durationSeconds: Int = 0,
    val topKeyPressed: String = "e"
)
