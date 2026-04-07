package com.plcoding.habittracker.feature.habits.data

import com.plcoding.habittracker.feature.habits.domain.CompletionRecord
import com.plcoding.habittracker.feature.habits.domain.Habit
import com.plcoding.habittracker.feature.habits.domain.WeekDays
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun HabitEntity.toHabit(): Habit {
    return Habit(
        id = id,
        name = name,
        icon = icon,
        weekDays = WeekDays(
            monday = monday == 1,
            tuesday = tuesday == 1,
            wednesday = wednesday == 1,
            thursday = thursday == 1,
            friday = friday == 1,
            saturday = saturday == 1,
            sunday = sunday == 1
        ),
        createdAt = Instant.ofEpochMilli(createdAt)
            .atZone(ZoneId.of("UTC"))
    )
}

fun Habit.toHabitEntity(): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        icon = icon,
        monday = if (weekDays.monday) 1 else 0,
        tuesday = if (weekDays.tuesday) 1 else 0,
        wednesday = if (weekDays.wednesday) 1 else 0,
        thursday = if (weekDays.thursday) 1 else 0,
        friday = if (weekDays.friday) 1 else 0,
        saturday = if (weekDays.saturday) 1 else 0,
        sunday = if (weekDays.sunday) 1 else 0,
        createdAt = createdAt.toInstant().toEpochMilli()
    )
}

fun HabitCompletionRaw.toCompletionRecord(): CompletionRecord {
    return CompletionRecord(
        habitId = habitId,
        date = Instant.ofEpochMilli(date)
            .atZone(ZoneId.of("UTC"))
    )
}

fun ZonedDateTime.toEpochMillis(): Long {
    return toInstant().toEpochMilli()
}
