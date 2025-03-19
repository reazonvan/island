package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Sheep(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(70.0, 140, 3, 15.0, gender) {
    
    override fun createOffspring(): Herbivore {
        return Sheep()
    }
} 