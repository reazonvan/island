package com.example.islandd.model.animals

import com.example.islandd.model.*
import com.example.islandd.utils.RandomGenerator

class Caterpillar(gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(0.01, 1000, 0, 0.0001, gender) {
    
    override fun createOffspring(): Herbivore {
        return Caterpillar()
    }
} 