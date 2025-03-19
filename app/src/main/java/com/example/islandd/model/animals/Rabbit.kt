package com.example.islandd.model.animals

import com.example.islandd.model.*
import com.example.islandd.utils.RandomGenerator

class Rabbit(gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(2.0, 150, 2, 0.45, gender) {
    
    override fun createOffspring(): Herbivore {
        return Rabbit()
    }
} 