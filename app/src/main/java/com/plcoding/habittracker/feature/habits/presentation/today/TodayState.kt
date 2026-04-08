package com.plcoding.habittracker.feature.habits.presentation.today

import com.plcoding.habittracker.feature.habits.domain.HabitIcon

data class TodayState(
    val habits: List<TodayHabitUi> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
)

data class TodayHabitUi(
    val habitId: Long,
    val name: String,
    val icon: HabitIcon,
    val currentStreak: Int,
    val isCompleted: Boolean,
)
