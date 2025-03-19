package com.example.islandd.model

object SimulationConfig {
    // Размеры острова
    var islandWidth = 100
    var islandHeight = 20
    
    // Длительность такта симуляции (в миллисекундах)
    var simulationDelay = 1000L
    
    // Начальные вероятности появления животных
    var initialProbabilities = mapOf(
        "Wolf" to 0.15,
        "Bear" to 0.15,
        "Fox" to 0.15,
        "Snake" to 0.15,
        "Eagle" to 0.15,
        "Horse" to 0.2,
        "Deer" to 0.2,
        "Rabbit" to 0.4,
        "Mouse" to 0.3,
        "Goat" to 0.2,
        "Sheep" to 0.2,
        "Boar" to 0.2,
        "Buffalo" to 0.15,
        "Duck" to 0.2,
        "Caterpillar" to 0.5
    )
    
    // Эмодзи для животных
    val animalEmoji = mapOf(
        "Wolf" to "🐺",
        "Bear" to "🐻",
        "Fox" to "🦊",
        "Snake" to "🐍",
        "Eagle" to "🦅",
        "Horse" to "🐎",
        "Deer" to "🦌",
        "Rabbit" to "🐇",
        "Mouse" to "🐁",
        "Goat" to "🐐",
        "Sheep" to "🐑",
        "Boar" to "🐗",
        "Buffalo" to "🐃",
        "Duck" to "🦆",
        "Caterpillar" to "🐛",
        "Grass" to "🌿"
    )
    
    // Количество детенышей у каждого вида
    var offspringCount = mapOf(
        "Wolf" to 3,
        "Bear" to 1,
        "Fox" to 3,
        "Snake" to 5,
        "Eagle" to 2,
        "Horse" to 1,
        "Deer" to 1,
        "Rabbit" to 5,
        "Mouse" to 8,
        "Goat" to 1,
        "Sheep" to 1,
        "Boar" to 4,
        "Buffalo" to 1,
        "Duck" to 5,
        "Caterpillar" to 10
    )
    
    // Условие остановки симуляции
    var stopCondition: (Map<Class<*>, Int>) -> Boolean = { stats ->
        stats.filter { it.key != Plant::class.java }.all { it.value == 0 }
    }
} 