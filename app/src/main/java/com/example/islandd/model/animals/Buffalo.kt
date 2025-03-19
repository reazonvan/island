package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Buffalo(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(700.0, 10, 3, 100.0, gender) {
    
    override fun createOffspring(): Herbivore {
        return Buffalo()
    }
} 