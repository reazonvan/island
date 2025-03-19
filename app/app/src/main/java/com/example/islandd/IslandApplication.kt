package com.example.islandd

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class IslandApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Ensure proper theme initialization
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
} 