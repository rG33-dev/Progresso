package com.plcoding.habittracker.feature.habits.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.habittracker.feature.habits.domain.Habit
import com.plcoding.habittracker.feature.habits.domain.HabitLocalDataSource
import com.plcoding.habittracker.feature.habits.domain.StreakCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

class StatisticsViewModel(
    private val dataSource: HabitLocalDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val now = ZonedDateTime.now()
            val zone = now.zone
            val todayLocal = now.toLocalDate()
            val allHabits = dataSource.getAllHabits()

            if (allHabits.isEmpty()) return@launch

            val earliestCreation = allHabits.minOf { it.createdAt }
            val completions = dataSource.getCompletionsInRange(earliestCreation, now)
            val completionsByHabit = completions.groupBy { it.habitId }
                .mapValues { (_, records) -> records.map { it.date }.toSet() }

            val thisWeekPercentage = computeThisWeekPercentage(
                allHabits, completionsByHabit, todayLocal, zone
            )

            val habitStreaks = allHabits.map { habit ->
                val dates = completionsByHabit[habit.id] ?: emptySet()
                HabitStreakUi(
                    habitId = habit.id,
                    name = habit.name,
                    icon = habit.icon,
                    currentStreak = StreakCalculator.computeCurrentStreak(habit, dates, now),
                    bestStreak = StreakCalculator.computeBestStreak(habit, dates, now),
                )
            }

            val globalBestStreak = habitStreaks.maxOfOrNull { it.bestStreak } ?: 0

            val heatmapData = computeHeatmap(
                allHabits, completionsByHabit, todayLocal, zone
            )

            _state.update {
                StatisticsState(
                    thisWeekPercentage = thisWeekPercentage,
                    bestStreak = globalBestStreak,
                    activeHabitCount = allHabits.size,
                    heatmapData = heatmapData,
                    habitStreaks = habitStreaks,
                )
            }
        }
    }

    private fun computeThisWeekPercentage(
        habits: List<Habit>,
        completionsByHabit: Map<Long, Set<ZonedDateTime>>,
        todayLocal: LocalDate,
        zone: ZoneId,
    ): Int {
        val mondayOfThisWeek = todayLocal.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        var totalScheduled = 0
        var totalCompleted = 0

        var day = mondayOfThisWeek
        while (!day.isAfter(todayLocal)) {
            val dayOfWeek = day.dayOfWeek
            for (habit in habits) {
                if (habit.weekDays.isScheduledFor(dayOfWeek)) {
                    val createdLocal = habit.createdAt.withZoneSameInstant(zone).toLocalDate()
                    if (!day.isBefore(createdLocal)) {
                        totalScheduled++
                        val dates = completionsByHabit[habit.id] ?: emptySet()
                        val completedLocalDates = dates.map {
                            it.withZoneSameInstant(zone).toLocalDate()
                        }.toSet()
                        if (day in completedLocalDates) {
                            totalCompleted++
                        }
                    }
                }
            }
            day = day.plusDays(1)
        }

        return if (totalScheduled > 0) (totalCompleted * 100 / totalScheduled) else 0
    }

    private fun computeHeatmap(
        habits: List<Habit>,
        completionsByHabit: Map<Long, Set<ZonedDateTime>>,
        todayLocal: LocalDate,
        zone: ZoneId,
    ): List<HeatmapDay> {
        val sundayOfThisWeek = todayLocal.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        val mondayStart = sundayOfThisWeek.minusDays(27)

        return (0 until 28).map { offset ->
            val day = mondayStart.plusDays(offset.toLong())
            val dayOfWeek = day.dayOfWeek
            var scheduled = 0
            var completed = 0

            for (habit in habits) {
                if (habit.weekDays.isScheduledFor(dayOfWeek)) {
                    val createdLocal = habit.createdAt.withZoneSameInstant(zone).toLocalDate()
                    if (!day.isBefore(createdLocal)) {
                        scheduled++
                        val dates = completionsByHabit[habit.id] ?: emptySet()
                        val completedLocalDates = dates.map {
                            it.withZoneSameInstant(zone).toLocalDate()
                        }.toSet()
                        if (day in completedLocalDates) {
                            completed++
                        }
                    }
                }
            }

            HeatmapDay(
                date = day,
                completionRatio = if (scheduled > 0) completed.toFloat() / scheduled else 0f,
                isToday = day == todayLocal,
                isFuture = day.isAfter(todayLocal),
            )
        }
    }
}
