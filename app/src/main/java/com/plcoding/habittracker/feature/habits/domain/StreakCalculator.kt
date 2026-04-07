package com.plcoding.habittracker.feature.habits.domain

import java.time.ZonedDateTime

object StreakCalculator {

    fun computeCurrentStreak(
        habit: Habit,
        completedDates: Set<ZonedDateTime>,
        today: ZonedDateTime
    ): Int {
        val completedLocalDates = completedDates.map { it.toLocalDate() }.toSet()
        val todayLocal = today.toLocalDate()
        val createdLocal = habit.createdAt.toLocalDate()

        val isTodayScheduled = habit.weekDays.isScheduledFor(todayLocal.dayOfWeek)
        val isTodayCompleted = todayLocal in completedLocalDates

        // Start from today if completed, otherwise start from yesterday
        var current = if (isTodayScheduled && !isTodayCompleted) {
            todayLocal.minusDays(1)
        } else {
            todayLocal
        }

        var streak = 0
        while (!current.isBefore(createdLocal)) {
            if (!habit.weekDays.isScheduledFor(current.dayOfWeek)) {
                current = current.minusDays(1)
                continue
            }
            if (current in completedLocalDates) {
                streak++
                current = current.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    fun computeBestStreak(
        habit: Habit,
        completedDates: Set<ZonedDateTime>,
        today: ZonedDateTime
    ): Int {
        val completedLocalDates = completedDates.map { it.toLocalDate() }.toSet()
        val todayLocal = today.toLocalDate()
        val createdLocal = habit.createdAt.toLocalDate()

        var current = createdLocal
        var currentRun = 0
        var bestRun = 0

        while (!current.isAfter(todayLocal)) {
            if (!habit.weekDays.isScheduledFor(current.dayOfWeek)) {
                current = current.plusDays(1)
                continue
            }
            if (current in completedLocalDates) {
                currentRun++
                if (currentRun > bestRun) {
                    bestRun = currentRun
                }
            } else {
                currentRun = 0
            }
            current = current.plusDays(1)
        }
        return bestRun
    }
}
