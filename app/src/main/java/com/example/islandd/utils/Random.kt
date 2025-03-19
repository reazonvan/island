package com.example.islandd.utils

import kotlin.random.Random

object RandomGenerator {
    fun nextBoolean(): Boolean = Random.nextBoolean()
    
    fun nextInt(from: Int, until: Int): Int = Random.nextInt(from, until)
    
    fun nextDouble(): Double = Random.nextDouble()
    
    fun nextInt(until: Int): Int = Random.nextInt(until)
} 