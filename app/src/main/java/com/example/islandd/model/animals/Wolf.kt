package com.example.islandd.model.animals

import com.example.islandd.model.*
import com.example.islandd.utils.RandomGenerator

class Wolf(gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Predator(50.0, 30, 3, 8.0, gender) {
    
    override fun getHuntingProbability(prey: Animal): Int {
        return when (prey) {
            is Horse -> 10
            is Deer -> 15
            is Rabbit -> 60
            is Mouse -> 80
            is Goat -> 60
            is Sheep -> 70
            is Boar -> 15
            is Buffalo -> 10
            is Duck -> 40
            else -> 0
        }
    }
    
    override fun createOffspring(): Predator {
        return Wolf()
    }
} 