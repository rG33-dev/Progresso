package com.plcoding.habittracker.feature.habits.presentation.di

import com.plcoding.habittracker.feature.habits.presentation.createedit.CreateEditHabitViewModel
import com.plcoding.habittracker.feature.habits.presentation.statistics.StatisticsViewModel
import com.plcoding.habittracker.feature.habits.presentation.today.TodayViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val habitsPresentationModule = module {
    viewModelOf(::TodayViewModel)
    viewModelOf(::StatisticsViewModel)
    viewModelOf(::CreateEditHabitViewModel)
}
