package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Mouse(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(0.05, 500, 1, 0.01, gender) {
    
    override fun createOffspring(): Herbivore {
        return Mouse()
    }
} 