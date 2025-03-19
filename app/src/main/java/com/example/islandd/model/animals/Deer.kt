package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Deer(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(300.0, 20, 4, 50.0, gender) {
    
    override fun createOffspring(): Herbivore {
        return Deer()
    }
} 