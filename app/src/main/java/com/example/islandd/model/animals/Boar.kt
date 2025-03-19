package com.example.islandd.model.animals

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Boar(gender: Gender = if (ThreadLocalRandom.current().nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(400.0, 50, 2, 50.0, gender) {
    
    override fun createOffspring(): Herbivore {
        return Boar()
    }
} 