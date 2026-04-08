package com.plcoding.habittracker.feature.habits.presentation.today

sealed interface TodayAction {
    data class ToggleCompletion(val habitId: Long) : TodayAction
}
