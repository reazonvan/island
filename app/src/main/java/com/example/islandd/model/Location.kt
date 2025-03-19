package com.example.islandd.model

import java.util.concurrent.ConcurrentHashMap

class Location(val x: Int, val y: Int) {
    private val animals = ConcurrentHashMap<Class<out Animal>, MutableList<Animal>>()
    private val plants = ConcurrentHashMap<Class<out Plant>, MutableList<Plant>>()

    @Synchronized
    fun addAnimal(animal: Animal) {
        animals.computeIfAbsent(animal::class.java) { mutableListOf() }.add(animal)
    }

    @Synchronized
    fun removeAnimal(animal: Animal) {
        animals[animal::class.java]?.remove(animal)
    }

    @Synchronized
    fun addPlant(plant: Plant) {
        plants.computeIfAbsent(plant::class.java) { mutableListOf() }.add(plant)
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
} 