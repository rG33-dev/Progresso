package com.plcoding.habittracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plcoding.habittracker.feature.habits.data.HabitCompletionEntity
import com.plcoding.habittracker.feature.habits.data.HabitDao
import com.plcoding.habittracker.feature.habits.data.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCompletionEntity::class],
    version = 1
)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao
}
