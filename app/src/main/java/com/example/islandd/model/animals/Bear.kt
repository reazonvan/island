package com.example.islandd.model.animals

import com.example.islandd.model.*
import com.example.islandd.utils.RandomGenerator

class Bear(gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Predator(500.0, 5, 2, 80.0, gender) {
    
    override fun getHuntingProbability(prey: Animal): Int {
        return when (prey) {
            is Horse -> 40
            is Deer -> 80
            is Rabbit -> 80
            is Mouse -> 90
            is Goat -> 70
            is Sheep -> 70
            is Boar -> 50
            is Buffalo -> 20
            is Duck -> 10
            else -> 0
        }
    }
    
    override fun createOffspring(): Predator {
        return Bear()
    }
} 