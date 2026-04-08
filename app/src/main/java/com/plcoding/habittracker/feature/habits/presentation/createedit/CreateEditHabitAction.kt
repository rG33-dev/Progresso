package com.plcoding.habittracker.feature.habits.presentation.createedit

import com.plcoding.habittracker.feature.habits.domain.HabitIcon
import java.time.DayOfWeek

sealed interface CreateEditHabitAction {
    data class OnNameChange(val name: String) : CreateEditHabitAction
    data class OnIconSelect(val icon: HabitIcon) : CreateEditHabitAction
    data class OnToggleDay(val dayOfWeek: DayOfWeek) : CreateEditHabitAction
    data object OnToggleIconPicker : CreateEditHabitAction
    data object OnSaveClick : CreateEditHabitAction
    data object OnDeleteClick : CreateEditHabitAction
    data object OnConfirmDelete : CreateEditHabitAction
    data object OnDismissDeleteDialog : CreateEditHabitAction
}
