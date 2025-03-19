package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Snake(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Predator(15.0, 30, 1, 3.0, gender) {
    
    override fun getHuntingProbability(prey: Animal): Int {
        return when (prey) {
            is Rabbit -> 20
            is Mouse -> 40
            is Duck -> 10
            is Caterpillar -> 90
            else -> 0
        }
    }
    
    override fun createOffspring(): Predator {
        return Snake()
    }
} 