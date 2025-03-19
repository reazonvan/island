package com.example.islandd

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.islandd.model.*
import com.example.islandd.model.animals.*
import com.example.islandd.model.plants.Grass
import com.example.islandd.utils.RandomGenerator
import kotlinx.coroutines.*
import kotlin.reflect.full.isSubclassOf

class MainActivity : AppCompatActivity() {
    private lateinit var island: Island
    private lateinit var statsTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var interactionsTextView: TextView
    private var isSimulationRunning = false
    private val mainScope = MainScope()
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Starting")
        
        try {
            setContentView(R.layout.activity_main)
            Log.d(TAG, "onCreate: Layout set")
            
            // Инициализация UI компонентов
            statsTextView = findViewById(R.id.statsTextView)
            startButton = findViewById(R.id.startButton)
            stopButton = findViewById(R.id.stopButton)
            interactionsTextView = findViewById(R.id.interactionsTextView)
            Log.d(TAG, "onCreate: Views initialized")
            
            // Создаем остров с параметрами из конфигурации
            island = Island(
                SimulationConfig.islandWidth,
                SimulationConfig.islandHeight,
                SimulationConfig.simulationDelay
            )
            Log.d(TAG, "onCreate: Island created")
            
            // Настраиваем обработчики кнопок
            startButton.setOnClickListener {
                if (!isSimulationRunning) {
                    startSimulation()
                }
            }
            
            stopButton.setOnClickListener {
                if (isSimulationRunning) {
                    stopSimulation()
                }
            }

            // Устанавливаем начальное состояние кнопок
            startButton.isEnabled = true
            stopButton.isEnabled = false
            
            // Устанавливаем начальный текст
            statsTextView.text = "Нажмите 'Старт' для начала симуляции"
            Log.d(TAG, "onCreate: Setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Error", e)
        }
    }
    
    private fun initializeIsland() {
        Log.d(TAG, "initializeIsland: Starting initialization")
        // Создаем начальное население острова
        repeat(SimulationConfig.islandHeight) { y ->
            repeat(SimulationConfig.islandWidth) { x ->
                val location = island.getLocation(x, y)
                
                // Добавляем растения (уменьшенное количество)
                repeat(RandomGenerator.nextInt(1, 100)) {
                    location.addPlant(Grass())
                }
                
                // Добавляем животных согласно конфигурации
                SimulationConfig.initialProbabilities.forEach { (type, probability) ->
                    if (RandomGenerator.nextDouble() < probability) {
                        val animal = when (type) {
                            "Wolf" -> Wolf()
                            "Bear" -> Bear()
                            "Fox" -> Fox()
                            "Snake" -> Snake()
                            "Eagle" -> Eagle()
                            "Horse" -> Horse()
                            "Deer" -> Deer()
                            "Rabbit" -> Rabbit()
                            "Mouse" -> Mouse()
                            "Goat" -> Goat()
                            "Sheep" -> Sheep()
                            "Boar" -> Boar()
                            "Buffalo" -> Buffalo()
                            "Duck" -> Duck()
                            "Caterpillar" -> Caterpillar()
                            else -> null
                        }
                        animal?.let { location.addAnimal(it) }
                    }
                }
            }
        }
        Log.d(TAG, "initializeIsland: Initialization completed")
    }
    
    private fun startStatisticsUpdate() {
        Log.d(TAG, "startStatisticsUpdate: Starting statistics update")
        mainScope.launch {
            while (isSimulationRunning) {
                try {
                    updateStatistics()
                    updateInteractions()
                    delay(SimulationConfig.simulationDelay)
                } catch (e: Exception) {
                    Log.e(TAG, "startStatisticsUpdate: Error in update loop", e)
                }
            }
        }
    }
    
    private fun updateStatistics() {
        try {
            val stats = island.getStatistics()
            val statsText = StringBuilder()
            statsText.appendLine("Статистика острова:")
            
            // Сортируем статистику по типам (хищники, травоядные, растения)
            val sortedStats = stats.entries.sortedBy { entry ->
                when {
                    entry.key == Grass::class -> 3 // Растения последние
                    entry.key.kotlin.isSubclassOf(Predator::class) -> 1 // Хищники первые
                    else -> 2 // Травоядные посередине
                }
            }
            
            sortedStats.forEach { (type, count) ->
                val emoji = SimulationConfig.animalEmoji[type.simpleName] ?: "❓"
                statsText.appendLine("$emoji ${type.simpleName}: $count")
            }
            statsText.appendLine("------------------------")
            
            runOnUiThread {
                statsTextView.text = statsText.toString()
            }
            
            // Проверяем условие остановки
            if (SimulationConfig.stopCondition(stats)) {
                stopSimulation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateStatistics: Error updating statistics", e)
        }
    }
    
    private fun updateInteractions() {
        try {
            val interactions = island.getInteractions()
            val interactionsText = StringBuilder()
            interactionsText.appendLine("Взаимодействия:")
            interactions.forEach { interaction ->
                interactionsText.appendLine(interaction)
            }
            runOnUiThread {
                interactionsTextView.text = interactionsText.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateInteractions: Error updating interactions", e)
        }
    }
    
    private fun startSimulation() {
        Log.d(TAG, "startSimulation: Starting")
        isSimulationRunning = true
        startButton.isEnabled = false
        stopButton.isEnabled = true
        
        try {
            // Инициализируем остров начальными животными и растениями
            initializeIsland()
            Log.d(TAG, "startSimulation: Island initialized")
            
            // Запускаем симуляцию
            island.start()
            Log.d(TAG, "startSimulation: Simulation started")
            
            // Запускаем обновление статистики
            startStatisticsUpdate()
            Log.d(TAG, "startSimulation: Statistics update started")
        } catch (e: Exception) {
            Log.e(TAG, "startSimulation: Error", e)
            stopSimulation()
        }
    }
    
    private fun stopSimulation() {
        Log.d(TAG, "stopSimulation: Stopping simulation")
        isSimulationRunning = false
        startButton.isEnabled = true
        stopButton.isEnabled = false
        
        try {
            island.stop()
            Log.d(TAG, "stopSimulation: Simulation stopped")
        } catch (e: Exception) {
            Log.e(TAG, "stopSimulation: Error stopping simulation", e)
        }
    }
    
    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Starting cleanup")
        super.onDestroy()
        if (isSimulationRunning) {
            stopSimulation()
        }
        mainScope.cancel()
        Log.d(TAG, "onDestroy: Cleanup completed")
    }
}