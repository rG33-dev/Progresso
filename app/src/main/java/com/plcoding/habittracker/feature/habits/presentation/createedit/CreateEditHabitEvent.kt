package com.plcoding.habittracker.feature.habits.presentation.createedit

sealed interface CreateEditHabitEvent {
    data object HabitSaved : CreateEditHabitEvent
    data object HabitDeleted : CreateEditHabitEvent
}
