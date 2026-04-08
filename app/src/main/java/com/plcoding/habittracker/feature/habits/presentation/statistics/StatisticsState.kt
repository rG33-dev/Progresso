package com.plcoding.habittracker.feature.habits.presentation.statistics

import com.plcoding.habittracker.feature.habits.domain.HabitIcon
import java.time.LocalDate

data class StatisticsState(
    val thisWeekPercentage: Int = 0,
    val bestStreak: Int = 0,
    val activeHabitCount: Int = 0,
    val heatmapData: List<HeatmapDay> = emptyList(),
    val habitStreaks: List<HabitStreakUi> = emptyList(),
)

data class HeatmapDay(
    val date: LocalDate,
    val completionRatio: Float,
    val isToday: Boolean,
    val isFuture: Boolean,
)

data class HabitStreakUi(
    val habitId: Long,
    val name: String,
    val icon: HabitIcon,
    val currentStreak: Int,
    val bestStreak: Int,
)
