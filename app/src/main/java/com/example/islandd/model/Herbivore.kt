package com.example.islandd.model

import com.example.islandd.utils.RandomGenerator

abstract class Herbivore(
    weight: Double,
    maxCount: Int,
    speed: Int,
    foodRequired: Double,
    gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE
) : Animal(weight, maxCount, speed, foodRequired, gender) {
    
    override fun eat(location: Location): Boolean {
        val plants = location.getPlants().values.flatten()
        if (plants.isNotEmpty()) {
            val plant = plants.random()
            location.removePlant(plant)
            currentFood = foodRequired
            return true
        }
        return false
    }
    
    override fun reproduce(partner: Animal): Animal? {
        if (partner::class.java != this::class.java) return null
        if (partner.gender == this.gender) return null
        if (RandomGenerator.nextDouble() > 0.5) return null
        
        return createOffspring()
    }
    
    protected abstract fun createOffspring(): Herbivore
} 