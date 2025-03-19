package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Fox(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Predator(8.0, 30, 2, 2.0, gender) {
    
    override fun getHuntingProbability(prey: Animal): Int {
        return when (prey) {
            is Rabbit -> 70
            is Mouse -> 90
            is Duck -> 60
            is Caterpillar -> 40
            else -> 0
        }
    }
    
    override fun createOffspring(): Predator {
        return Fox()
    }
} 