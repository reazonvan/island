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
    }
    
    private fun processAnimals() {
        locations.forEach { row ->
            row.forEach { location ->
                location.getAnimals().values.flatten().forEach { animal ->
                    animalTaskExecutor.submit {
                        processAnimal(animal, location)
                    }
                }
            }
        }
    }
    
    private fun processAnimal(animal: Animal, location: Location) {
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
        }
        
        // Размножение
        location.getAnimals()[animal::class.java]?.forEach { partner ->
            if (partner != animal) {
                val offspring = animal.reproduce(partner)
                offspring?.let { location.addAnimal(it) }
            }
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
        scheduler.shutdown()
        animalTaskExecutor.shutdown()
    }
    
    fun getInteractions(): List<String> {
        val interactions = mutableListOf<String>()
        locations.forEach { row ->
            row.forEach { location ->
                location.getAnimals().values.flatten().forEach { animal ->
                    if (animal is Predator) {
                        val prey = location.getAnimals().values.flatten().firstOrNull { it != animal && animal.getHuntingProbability(it) > 0 }
                        if (prey != null) {
                            interactions.add("${animal::class.simpleName} съел ${prey::class.simpleName}")
                        }
                    } else if (animal is Herbivore) {
                        val plant = location.getPlants().values.flatten().firstOrNull()
                        if (plant != null) {
                            interactions.add("${animal::class.simpleName} съел растение")
                        }
                    }
                }
            }
        }
        return interactions
    }
} 