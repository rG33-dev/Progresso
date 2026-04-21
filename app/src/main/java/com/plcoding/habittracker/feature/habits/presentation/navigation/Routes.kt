package com.plcoding.habittracker.feature.habits.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route{

   @Serializable data object Preview : Route

    @Serializable
    data object Today : Route

    @Serializable
    data object Statistics : Route

    @Serializable
    data class CreateEditHabit(val habitId: Long? = null) : Route
}
