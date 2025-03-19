package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Eagle(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Predator(6.0, 20, 3, 1.0, gender) {
    
    override fun getHuntingProbability(prey: Animal): Int {
        return when (prey) {
            is Rabbit -> 90
            is Mouse -> 90
            is Duck -> 80
            is Caterpillar -> 40
            else -> 0
        }
    }
    
    override fun createOffspring(): Predator {
        return Eagle()
    }
} 