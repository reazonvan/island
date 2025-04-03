package com.example.islandd.model

import java.util.concurrent.ConcurrentHashMap

class Location(val x: Int, val y: Int) {
    private val animals = ConcurrentHashMap<Class<out Animal>, MutableList<Animal>>()
    private val plants = ConcurrentHashMap<Class<out Plant>, MutableList<Plant>>()
    
    private val maxAnimalsPerSpecies = 200 // Максимальное количество животных одного вида
    private val maxTotalAnimals = 500 // Максимальное общее количество животных
    private val maxPlantsPerSpecies = 1000 // Максимальное количество растений одного вида

    @Synchronized
    fun addAnimal(animal: Animal) {
        // Проверка на максимальное общее количество животных
        val totalAnimals = animals.values.sumOf { it.size }
        if (totalAnimals >= maxTotalAnimals) {
            // Если животных слишком много, не добавляем новых
            return
        }
        
        // Проверка на максимальное количество животных одного вида
        val animalType = animal::class.java
        val speciesCount = animals[animalType]?.size ?: 0
        
        if (speciesCount < maxAnimalsPerSpecies) {
            animals.computeIfAbsent(animalType) { mutableListOf() }.add(animal)
        }
    }

    @Synchronized
    fun removeAnimal(animal: Animal) {
        animals[animal::class.java]?.remove(animal)
    }

    @Synchronized
    fun addPlant(plant: Plant) {
        val plantType = plant::class.java
        val speciesCount = plants[plantType]?.size ?: 0
        
        if (speciesCount < maxPlantsPerSpecies) {
            plants.computeIfAbsent(plantType) { mutableListOf() }.add(plant)
        }
    }

    @Synchronized
    fun removePlant(plant: Plant) {
        plants[plant::class.java]?.remove(plant)
    }

    fun getAnimals(): Map<Class<out Animal>, List<Animal>> = animals

    fun getPlants(): Map<Class<out Plant>, List<Plant>> = plants

    fun getAnimalsByType(type: Class<out Animal>): List<Animal> {
        return animals[type] ?: emptyList()
    }

    fun getPlantsByType(type: Class<out Plant>): List<Plant> {
        return plants[type] ?: emptyList()
    }
    
    // Метод для очистки излишков животных и растений
    @Synchronized
    fun cleanupExcessPopulation() {
        // Ограничиваем количество животных каждого вида
        animals.forEach { (type, list) ->
            if (list.size > maxAnimalsPerSpecies) {
                val toRemove = list.size - maxAnimalsPerSpecies
                repeat(toRemove) {
                    list.removeAt(0)
                }
            }
        }
        
        // Ограничиваем количество растений каждого вида
        plants.forEach { (type, list) ->
            if (list.size > maxPlantsPerSpecies) {
                val toRemove = list.size - maxPlantsPerSpecies
                repeat(toRemove) {
                    list.removeAt(0)
                }
            }
        }
    }
} 