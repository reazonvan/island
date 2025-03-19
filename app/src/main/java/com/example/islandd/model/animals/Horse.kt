package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Horse(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(400.0, 20, 4, 60.0, gender) {
    
    override fun createOffspring(): Herbivore {
        return Horse()
    }
} 