package com.plcoding.habittracker.feature.habits.domain

import java.time.ZonedDateTime

data class Habit(
    val id: Long,
    val name: String,
    val icon: String,
    val weekDays: WeekDays,
    val createdAt: ZonedDateTime
)
