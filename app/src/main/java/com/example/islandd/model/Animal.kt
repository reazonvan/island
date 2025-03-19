package com.example.islandd.model

import com.example.islandd.utils.RandomGenerator
import kotlin.math.max

enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

enum class Gender {
    MALE, FEMALE
}

abstract class Animal(
    var weight: Double,
    var maxCount: Int,
    var speed: Int,
    var foodRequired: Double,
    val gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE
) {
    var currentFood: Double = foodRequired
    
    abstract fun eat(location: Location): Boolean
    
    abstract fun reproduce(partner: Animal): Animal?
    
    open fun move(): Direction {
        return Direction.values()[RandomGenerator.nextInt(Direction.values().size)]
    }
    
    fun isHungry(): Boolean {
        return currentFood < foodRequired
    }
    
    fun die(): Boolean {
        return currentFood <= 0
    }
    
    fun updateFood() {
        currentFood = max(0.0, currentFood - (foodRequired * 0.1))
    }
} 