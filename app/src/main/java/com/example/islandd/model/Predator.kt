package com.example.islandd.model

import com.example.islandd.utils.RandomGenerator

abstract class Predator(
    weight: Double,
    maxCount: Int,
    speed: Int,
    foodRequired: Double,
    gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE
) : Animal(weight, maxCount, speed, foodRequired, gender) {
    
    abstract fun getHuntingProbability(prey: Animal): Int
    
    override fun eat(location: Location): Boolean {
        val preyAnimals = location.getAnimals().values.flatten()
            .filter { it != this && getHuntingProbability(it) > 0 }
        
        for (prey in preyAnimals) {
            val probability = getHuntingProbability(prey)
            if (RandomGenerator.nextInt(100) < probability) {
                location.removeAnimal(prey)
                currentFood = foodRequired
                return true
            }
        }
        return false
    }
    
    override fun reproduce(partner: Animal): Animal? {
        if (partner::class.java != this::class.java) return null
        if (partner.gender == this.gender) return null
        if (RandomGenerator.nextDouble() > 0.5) return null
        
        return createOffspring()
    }
    
    protected abstract fun createOffspring(): Predator
} 