package com.example.islandd.model.animals

import com.example.islandd.model.*
import com.example.islandd.utils.RandomGenerator

class Duck(gender: Gender = if (RandomGenerator.nextBoolean()) Gender.MALE else Gender.FEMALE) 
    : Herbivore(1.0, 200, 4, 0.15, gender) {
    
    override fun eat(location: Location): Boolean {
        // Сначала пытаемся съесть гусеницу
        location.getAnimals()[Caterpillar::class.java]?.firstOrNull()?.let { caterpillar ->
            if (RandomGenerator.nextInt(100) < 90) { // 90% шанс съесть гусеницу
                location.removeAnimal(caterpillar)
                currentFood = foodRequired
                return true
            }
        }
        
        // Если гусеницу не нашли или не смогли съесть, едим растения
        return super.eat(location)
    }
    
    override fun createOffspring(): Herbivore {
        return Duck()
    }
} 