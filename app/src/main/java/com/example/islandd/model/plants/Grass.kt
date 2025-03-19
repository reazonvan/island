package com.example.islandd.model.plants

import com.example.islandd.model.*
import java.util.concurrent.ThreadLocalRandom

class Grass : Plant(1.0, 200) {
    override fun grow(location: Location) {
        // Растения размножаются с некоторой вероятностью
        if (ThreadLocalRandom.current().nextDouble() < 0.25) { // 25% шанс размножения
            location.addPlant(Grass())
        }
    }
} 