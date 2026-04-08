package com.plcoding.habittracker.feature.habits.presentation.createedit

import com.plcoding.habittracker.feature.habits.domain.HabitIcon
import com.plcoding.habittracker.feature.habits.domain.WeekDays

data class CreateEditHabitState(
    val isEditMode: Boolean = false,
    val habitName: String = "",
    val selectedIcon: HabitIcon = HabitIcon.RUN,
    val weekDays: WeekDays = WeekDays(),
    val isIconPickerExpanded: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val canSave: Boolean = false,
)
