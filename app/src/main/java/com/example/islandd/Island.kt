package com.example.islandd

import com.example.islandd.model.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Island(
    private val width: Int,
    private val height: Int,
    private val simulationDelay: Long = 1000L // задержка между тактами в миллисекундах
) {
    private val locations: Array<Array<Location>> = Array(height) { y ->
        Array(width) { x -> Location(x, y) }
    }
    
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(3)
    private val animalTaskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    
    private val interactionHistory = mutableListOf<String>() // Список для хранения истории взаимодействий
    private val maxInteractionsHistory = 30 // Максимальное количество хранимых взаимодействий
    private val maxAnimalsPerLocation = 100 // Максимальное количество животных в одной локации
    
    fun getLocation(x: Int, y: Int): Location {
        return locations[y][x]
    }
    
    fun getStatistics(): Map<Class<*>, Int> {
        val stats = mutableMapOf<Class<*>, Int>()
        
        locations.forEach { row ->
            row.forEach { location ->
                location.getAnimals().forEach { (type, animals) ->
                    stats[type] = (stats[type] ?: 0) + animals.size
                }
                location.getPlants().forEach { (type, plants) ->
                    stats[type] = (stats[type] ?: 0) + plants.size
                }
            }
        }
        
        return stats
    }
    
    fun start() {
        // Запускаем жизненный цикл животных
        scheduler.scheduleAtFixedRate({
            processAnimals()
        }, 0, simulationDelay, TimeUnit.MILLISECONDS)
        
        // Запускаем рост растений
        scheduler.scheduleAtFixedRate({
            growPlants()
        }, 0, simulationDelay, TimeUnit.MILLISECONDS)
        
        // Запускаем вывод статистики
        scheduler.scheduleAtFixedRate({
            printStatistics()
        }, 0, simulationDelay, TimeUnit.MILLISECONDS)
        
        // Запускаем периодическую очистку для контроля численности
        scheduler.scheduleAtFixedRate({
            cleanupAllLocations()
        }, 5000, 10000, TimeUnit.MILLISECONDS) // Очистка каждые 10 секунд
    }
    
    private fun processAnimals() {
        try {
            // Ограничиваем количество задач, создаваемых за один такт
            val animalTasks = mutableListOf<Runnable>()
            
            locations.forEach { row ->
                row.forEach { location ->
                    // Проверка на слишком большое количество животных в локации
                    val animals = location.getAnimals().values.flatten()
                    val totalAnimals = animals.size
                    
                    if (totalAnimals > maxAnimalsPerLocation) {
                        // Если животных слишком много, оставляем только maxAnimalsPerLocation
                        val animalsToRemove = totalAnimals - maxAnimalsPerLocation
                        val animalsToProcess = animals.shuffled().takeLast(maxAnimalsPerLocation)
                        
                        // Удаляем излишек животных
                        animals.take(animalsToRemove).forEach { animal ->
                            location.removeAnimal(animal)
                        }
                        
                        // Обрабатываем оставшихся животных
                        animalsToProcess.forEach { animal ->
                            animalTasks.add(Runnable { processAnimal(animal, location) })
                        }
                    } else {
                        // Если количество животных приемлемое, обрабатываем всех
                        animals.forEach { animal ->
                            animalTasks.add(Runnable { processAnimal(animal, location) })
                        }
                    }
                }
            }
            
            // Запускаем задачи порциями, чтобы не перегружать пул потоков
            val batchSize = 100 // Размер пакета задач
            animalTasks.chunked(batchSize).forEach { batch ->
                batch.forEach { task ->
                    animalTaskExecutor.submit(task)
                }
                // Даем время на выполнение текущей партии задач
                Thread.sleep(10)
            }
        } catch (e: Exception) {
            // Обработка ошибок для предотвращения падения основного потока
            e.printStackTrace()
        }
    }
    
    private fun processAnimal(animal: Animal, location: Location) {
        try {
            // Проверяем голод
            if (animal.isHungry()) {
                if (!animal.eat(location)) {
                    animal.updateFood()
                    if (animal.die()) {
                        location.removeAnimal(animal)
                        return
                    }
                }
            }
            
            // Передвижение
            val direction = animal.move()
            val newLocation = getNewLocation(location, direction)
            if (newLocation != null) {
                location.removeAnimal(animal)
                newLocation.addAnimal(animal)
                // Продолжаем обработку в новой локации
                return
            }
            
            // Размножение - с ограничением на вероятность и общее количество
            val animalsOfSameType = location.getAnimalsByType(animal::class.java)
            
            // Если животных этого типа уже слишком много, не размножаем
            if (animalsOfSameType.size >= animal.maxCount) {
                return
            }
            
            // Ограничиваем количество попыток размножения
            val maxReproductionAttempts = 3
            var attempts = 0
            
            // Перемешиваем список животных для случайного выбора партнера
            val potentialPartners = animalsOfSameType.shuffled()
                .filter { it != animal && it.gender != animal.gender }
            
            for (partner in potentialPartners) {
                if (attempts >= maxReproductionAttempts) break
                
                attempts++
                val offspring = animal.reproduce(partner)
                if (offspring != null) {
                    // Ограничиваем добавление потомства на основе текущей популяции
                    if (animalsOfSameType.size < animal.maxCount * 0.8) {
                        location.addAnimal(offspring)
                        break // Только одно потомство за такт
                    }
                }
            }
        } catch (e: Exception) {
            // Обработка ошибок для предотвращения падения задачи
            e.printStackTrace()
        }
    }
    
    private fun getNewLocation(current: Location, direction: Direction): Location? {
        return when (direction) {
            Direction.NORTH -> if (current.y > 0) locations[current.y - 1][current.x] else null
            Direction.SOUTH -> if (current.y < height - 1) locations[current.y + 1][current.x] else null
            Direction.WEST -> if (current.x > 0) locations[current.y][current.x - 1] else null
            Direction.EAST -> if (current.x < width - 1) locations[current.y][current.x + 1] else null
        }
    }
    
    private fun growPlants() {
        locations.forEach { row ->
            row.forEach { location ->
                location.getPlants().values.flatten().forEach { plant ->
                    plant.grow(location)
                }
            }
        }
    }
    
    private fun printStatistics() {
        val stats = mutableMapOf<Class<*>, Int>()
        
        locations.forEach { row ->
            row.forEach { location ->
                location.getAnimals().forEach { (type, animals) ->
                    stats[type] = (stats[type] ?: 0) + animals.size
                }
                location.getPlants().forEach { (type, plants) ->
                    stats[type] = (stats[type] ?: 0) + plants.size
                }
            }
        }
        
        println("\nСтатистика острова:")
        stats.forEach { (type, count) ->
            println("${type.simpleName}: $count")
        }
        println("------------------------")
    }
    
    fun stop() {
        try {
            // Правильное завершение работы планировщика
            scheduler.shutdown()
            try {
                // Ждем завершения текущих задач, но не более 5 секунд
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    // Если задачи не завершились за 5 секунд, принудительно останавливаем
                    scheduler.shutdownNow()
                }
            } catch (e: InterruptedException) {
                // Если произошло прерывание, принудительно останавливаем
                scheduler.shutdownNow()
                Thread.currentThread().interrupt()
            }
            
            // Правильное завершение работы пула потоков для задач животных
            animalTaskExecutor.shutdown()
            try {
                // Ждем завершения текущих задач, но не более 5 секунд
                if (!animalTaskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    // Если задачи не завершились за 5 секунд, принудительно останавливаем
                    animalTaskExecutor.shutdownNow()
                }
            } catch (e: InterruptedException) {
                // Если произошло прерывание, принудительно останавливаем
                animalTaskExecutor.shutdownNow()
                Thread.currentThread().interrupt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getInteractions(): List<String> {
        val newInteractions = mutableListOf<String>()
        locations.forEach { row ->
            row.forEach { location ->
                location.getAnimals().values.flatten().forEach { animal ->
                    if (animal is Predator) {
                        val prey = location.getAnimals().values.flatten().firstOrNull { it != animal && animal.getHuntingProbability(it) > 0 }
                        if (prey != null) {
                            newInteractions.add("${animal::class.simpleName} съел ${prey::class.simpleName}")
                        }
                    } else if (animal is Herbivore) {
                        val plant = location.getPlants().values.flatten().firstOrNull()
                        if (plant != null) {
                            newInteractions.add("${animal::class.simpleName} съел растение")
                        }
                    }
                }
            }
        }
        
        // Добавляем новые взаимодействия в историю
        synchronized(interactionHistory) {
            interactionHistory.addAll(newInteractions)
            // Если история превышает максимальный размер, удаляем старые взаимодействия
            if (interactionHistory.size > maxInteractionsHistory) {
                val countToRemove = interactionHistory.size - maxInteractionsHistory
                interactionHistory.subList(0, countToRemove).clear()
            }
            return interactionHistory.toList() // Возвращаем копию списка
        }
    }
    
    // Метод для очистки всех локаций
    private fun cleanupAllLocations() {
        try {
            locations.forEach { row ->
                row.forEach { location ->
                    location.cleanupExcessPopulation()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 