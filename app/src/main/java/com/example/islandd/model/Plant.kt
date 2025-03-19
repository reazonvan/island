package com.example.islandd.model

open class Plant(
    val weight: Double,
    val maxCount: Int
) {
    open fun grow(location: Location) {
        // Базовая логика роста растений
    }
} 