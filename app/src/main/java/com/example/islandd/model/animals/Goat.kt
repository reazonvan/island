package com.example.islandd.model.animals

import com.example.islandd.model.*
import com.example.islandd.utils.RandomGenerator

class Goat(gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(60.0, 140, 3, 10.0, gender) {
    
    override fun createOffspring(): Herbivore {
        return Goat()
    }
} 