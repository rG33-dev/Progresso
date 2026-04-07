package com.plcoding.habittracker.feature.habits.domain

import com.plcoding.habittracker.core.domain.DataError
import com.plcoding.habittracker.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.ZonedDateTime

interface HabitLocalDataSource {

    fun getHabitsForDayOfWeek(dayOfWeek: DayOfWeek): Flow<List<Habit>>

    fun getCompletedHabitIdsForDate(date: ZonedDateTime): Flow<Set<Long>>

    suspend fun toggleCompletion(habitId: Long, date: ZonedDateTime)

    suspend fun upsertHabit(habit: Habit): EmptyResult<DataError.Local>

    suspend fun deleteHabit(habitId: Long)

    suspend fun getHabitById(habitId: Long): Habit?

    fun getActiveHabitCount(): Flow<Int>

    suspend fun getAllHabits(): List<Habit>

    suspend fun getCompletionsInRange(
        start: ZonedDateTime,
        end: ZonedDateTime
    ): List<CompletionRecord>
}

data class CompletionRecord(
    val habitId: Long,
    val date: ZonedDateTime
)
