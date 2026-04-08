package com.plcoding.habittracker

import android.app.Application
import com.plcoding.habittracker.feature.habits.data.di.habitsDataModule
import com.plcoding.habittracker.feature.habits.presentation.di.habitsPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HabitTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HabitTrackerApp)
            modules(habitsDataModule, habitsPresentationModule)
        }
    }
}
