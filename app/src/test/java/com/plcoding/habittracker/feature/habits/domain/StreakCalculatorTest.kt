package com.plcoding.habittracker.feature.habits.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class StreakCalculatorTest {

    private val utc = ZoneId.of("UTC")

    private fun habit(
        weekDays: WeekDays = WeekDays(
            monday = true,
            tuesday = true,
            wednesday = true,
            thursday = true,
            friday = true,
            saturday = true,
            sunday = true
        ),
        createdAt: LocalDate = LocalDate.of(2026, 3, 1)
    ): Habit {
        return Habit(
            id = 1L,
            name = "Test",
            icon = HabitIcon.RUN,
            weekDays = weekDays,
            createdAt = createdAt.atStartOfDay(utc)
        )
    }

    private fun todayAt(date: LocalDate, zone: ZoneId = utc): ZonedDateTime {
        return date.atTime(12, 0).atZone(zone)
    }

    private fun completionsAt(
        vararg dates: LocalDate,
        zone: ZoneId = utc
    ): Set<ZonedDateTime> {
        return dates.map { it.atStartOfDay(zone) }.toSet()
    }

    // ──────────────────────────────────────────────
    // Current streak — basic cases
    // ──────────────────────────────────────────────

    @Test
    fun `current streak - no completions returns 0`() {
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = emptySet(),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `current streak - today completed counts as 1`() {
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completionsAt(today),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `current streak - consecutive days completed`() {
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 6),
                LocalDate.of(2026, 4, 7),
                today
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `current streak - gap breaks the streak`() {
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 5),
                // gap on April 6
                LocalDate.of(2026, 4, 7),
                today
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `current streak - today not completed starts from yesterday`() {
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 6),
                LocalDate.of(2026, 4, 7)
                // today NOT completed
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `current streak - today not completed and yesterday missed returns 0`() {
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 6)
                // April 7 and today not completed
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(0)
    }

    // ──────────────────────────────────────────────
    // Current streak — weekday-only schedule
    // ──────────────────────────────────────────────

    @Test
    fun `current streak - skips unscheduled days`() {
        // Habit only on weekdays (Mon-Fri), created March 1
        val weekdaysOnly = WeekDays(
            monday = true,
            tuesday = true,
            wednesday = true,
            thursday = true,
            friday = true,
            saturday = false,
            sunday = false
        )
        // Today is Monday April 6, 2026
        // Friday April 3 was completed, Sat/Sun skipped, Monday completed
        val today = LocalDate.of(2026, 4, 6) // Monday
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(weekDays = weekdaysOnly),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 3), // Friday
                today // Monday
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `current streak - today is unscheduled looks at last scheduled day`() {
        // Habit only on weekdays
        val weekdaysOnly = WeekDays(
            monday = true,
            tuesday = true,
            wednesday = true,
            thursday = true,
            friday = true,
            saturday = false,
            sunday = false
        )
        // Today is Saturday April 4, 2026
        // Friday April 3 was completed
        val today = LocalDate.of(2026, 4, 4) // Saturday
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(weekDays = weekdaysOnly),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 2), // Thursday
                LocalDate.of(2026, 4, 3)  // Friday
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(2)
    }

    // ──────────────────────────────────────────────
    // Current streak — creation date boundary
    // ──────────────────────────────────────────────

    @Test
    fun `current streak - does not count before creation date`() {
        val createdOn = LocalDate.of(2026, 4, 7)
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(createdAt = createdOn),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 7),
                today
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `current streak - created today with completion returns 1`() {
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(createdAt = today),
            completedDates = completionsAt(today),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `current streak - created today no completion returns 0`() {
        val today = LocalDate.of(2026, 4, 8)
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(createdAt = today),
            completedDates = emptySet(),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(0)
    }

    // ──────────────────────────────────────────────
    // Current streak — timezone edge cases
    // ──────────────────────────────────────────────

    @Test
    fun `current streak - positive offset timezone converts dates correctly`() {
        val tokyo = ZoneId.of("Asia/Tokyo") // UTC+9
        val today = LocalDate.of(2026, 4, 8)

        // Completion stored at UTC midnight for April 7 and 8
        val completions = setOf(
            LocalDate.of(2026, 4, 7).atStartOfDay(utc),
            LocalDate.of(2026, 4, 8).atStartOfDay(utc)
        )

        // "today" is April 8 in Tokyo
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completions,
            today = today.atTime(2, 0).atZone(tokyo)
        )
        // April 8 UTC midnight = April 8 09:00 Tokyo → still April 8
        // April 7 UTC midnight = April 7 09:00 Tokyo → still April 7
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `current streak - negative offset timezone converts dates correctly`() {
        val newYork = ZoneId.of("America/New_York") // UTC-4/-5
        val today = LocalDate.of(2026, 4, 8)

        // Completions stored as UTC midnight
        val completions = setOf(
            LocalDate.of(2026, 4, 7).atStartOfDay(utc),
            LocalDate.of(2026, 4, 8).atStartOfDay(utc)
        )

        // "today" is April 8 in New York
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completions,
            today = today.atTime(10, 0).atZone(newYork)
        )
        // April 8 UTC midnight = April 7 20:00 New York → becomes April 7 in NY!
        // April 7 UTC midnight = April 6 20:00 New York → becomes April 6 in NY!
        // So from NY perspective: completed on April 6 and 7, today is April 8 (not completed)
        // Streak from yesterday (April 7): April 7 completed → April 6 completed → 2
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `current streak - completion at UTC midnight shifts date in negative offset`() {
        val newYork = ZoneId.of("America/New_York") // UTC-4
        // Today is April 8 in NY
        // Only one completion: April 8 at UTC midnight
        // In NY that's April 7 at 20:00 → date is April 7
        val completions = setOf(
            LocalDate.of(2026, 4, 8).atStartOfDay(utc)
        )

        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(),
            completedDates = completions,
            today = LocalDate.of(2026, 4, 8).atTime(10, 0).atZone(newYork)
        )
        // Completion is April 7 in NY time, today is April 8 (not completed)
        // Start from yesterday (April 7): completed → streak = 1
        assertThat(result).isEqualTo(1)
    }

    // ──────────────────────────────────────────────
    // Best streak — basic cases
    // ──────────────────────────────────────────────

    @Test
    fun `best streak - no completions returns 0`() {
        val result = StreakCalculator.computeBestStreak(
            habit = habit(),
            completedDates = emptySet(),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `best streak - single completion returns 1`() {
        val result = StreakCalculator.computeBestStreak(
            habit = habit(),
            completedDates = completionsAt(LocalDate.of(2026, 3, 15)),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `best streak - consecutive completions`() {
        val result = StreakCalculator.computeBestStreak(
            habit = habit(),
            completedDates = completionsAt(
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 11),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 13),
                LocalDate.of(2026, 3, 14)
            ),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(5)
    }

    @Test
    fun `best streak - picks longest run not current`() {
        val result = StreakCalculator.computeBestStreak(
            habit = habit(),
            completedDates = completionsAt(
                // First run: 4 days
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 11),
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 13),
                // Gap
                // Second run: 2 days (current)
                LocalDate.of(2026, 4, 7),
                LocalDate.of(2026, 4, 8)
            ),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(4)
    }

    @Test
    fun `best streak - multiple runs returns the longest`() {
        val result = StreakCalculator.computeBestStreak(
            habit = habit(),
            completedDates = completionsAt(
                // Run 1: 2 days
                LocalDate.of(2026, 3, 5),
                LocalDate.of(2026, 3, 6),
                // Run 2: 3 days
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 11),
                LocalDate.of(2026, 3, 12),
                // Run 3: 1 day
                LocalDate.of(2026, 3, 20)
            ),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(3)
    }

    // ──────────────────────────────────────────────
    // Best streak — weekday-only schedule
    // ──────────────────────────────────────────────

    @Test
    fun `best streak - skips unscheduled days in streak counting`() {
        val weekdaysOnly = WeekDays(
            monday = true,
            tuesday = true,
            wednesday = true,
            thursday = true,
            friday = true,
            saturday = false,
            sunday = false
        )
        // Completed Fri March 6, Mon March 9, Tue March 10 (weekend skipped)
        val result = StreakCalculator.computeBestStreak(
            habit = habit(weekDays = weekdaysOnly),
            completedDates = completionsAt(
                LocalDate.of(2026, 3, 6),  // Friday
                LocalDate.of(2026, 3, 9),  // Monday
                LocalDate.of(2026, 3, 10)  // Tuesday
            ),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `best streak - missed scheduled day breaks streak across weekend`() {
        val weekdaysOnly = WeekDays(
            monday = true,
            tuesday = true,
            wednesday = true,
            thursday = true,
            friday = true,
            saturday = false,
            sunday = false
        )
        // Completed Thu March 5, Fri March 6, missed Mon March 9, completed Tue March 10
        val result = StreakCalculator.computeBestStreak(
            habit = habit(weekDays = weekdaysOnly),
            completedDates = completionsAt(
                LocalDate.of(2026, 3, 5),  // Thursday
                LocalDate.of(2026, 3, 6),  // Friday
                // Monday March 9 missed
                LocalDate.of(2026, 3, 10)  // Tuesday
            ),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(2)
    }

    // ──────────────────────────────────────────────
    // Best streak — creation date boundary
    // ──────────────────────────────────────────────

    @Test
    fun `best streak - only counts from creation date forward`() {
        val createdOn = LocalDate.of(2026, 3, 12)
        val result = StreakCalculator.computeBestStreak(
            habit = habit(createdAt = createdOn),
            completedDates = completionsAt(
                // These are before creation — should be ignored
                LocalDate.of(2026, 3, 10),
                LocalDate.of(2026, 3, 11),
                // After creation
                LocalDate.of(2026, 3, 12),
                LocalDate.of(2026, 3, 13)
            ),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(2)
    }

    // ──────────────────────────────────────────────
    // Best streak — timezone edge cases
    // ──────────────────────────────────────────────

    @Test
    fun `best streak - timezone conversion applied consistently`() {
        val tokyo = ZoneId.of("Asia/Tokyo")

        val completions = setOf(
            LocalDate.of(2026, 3, 10).atStartOfDay(utc),
            LocalDate.of(2026, 3, 11).atStartOfDay(utc),
            LocalDate.of(2026, 3, 12).atStartOfDay(utc)
        )

        val result = StreakCalculator.computeBestStreak(
            habit = habit(),
            completedDates = completions,
            today = LocalDate.of(2026, 4, 8).atTime(12, 0).atZone(tokyo)
        )
        // In Tokyo: all three dates shift +9 hours but stay on same calendar dates
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `best streak - negative offset can merge adjacent UTC dates into same local date`() {
        val newYork = ZoneId.of("America/New_York") // UTC-4

        // Two completions at UTC midnight on consecutive days
        // In NY time, both shift back to previous day's evening
        val completions = setOf(
            LocalDate.of(2026, 3, 10).atStartOfDay(utc), // March 9 20:00 NY
            LocalDate.of(2026, 3, 11).atStartOfDay(utc)  // March 10 20:00 NY
        )

        val result = StreakCalculator.computeBestStreak(
            habit = habit(),
            completedDates = completions,
            today = LocalDate.of(2026, 4, 8).atTime(10, 0).atZone(newYork)
        )
        // In NY: March 9 and March 10 → consecutive → streak of 2
        assertThat(result).isEqualTo(2)
    }

    // ──────────────────────────────────────────────
    // Edge cases — single day schedule
    // ──────────────────────────────────────────────

    @Test
    fun `current streak - habit only on one day of week`() {
        // Only scheduled on Wednesdays
        val wednesdayOnly = WeekDays(wednesday = true)
        // Today is Wednesday April 8, 2026
        val today = LocalDate.of(2026, 4, 8) // Wednesday
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(weekDays = wednesdayOnly),
            completedDates = completionsAt(
                LocalDate.of(2026, 3, 25), // Wed
                LocalDate.of(2026, 4, 1),  // Wed
                today                       // Wed
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `current streak - single day schedule with gap`() {
        val wednesdayOnly = WeekDays(wednesday = true)
        val today = LocalDate.of(2026, 4, 8) // Wednesday
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(weekDays = wednesdayOnly),
            completedDates = completionsAt(
                LocalDate.of(2026, 3, 25), // Wed - completed
                // April 1 (Wed) missed
                today                       // Wed - completed
            ),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `best streak - single day schedule`() {
        val wednesdayOnly = WeekDays(wednesday = true)
        val result = StreakCalculator.computeBestStreak(
            habit = habit(weekDays = wednesdayOnly),
            completedDates = completionsAt(
                LocalDate.of(2026, 3, 4),  // Wed
                LocalDate.of(2026, 3, 11), // Wed
                LocalDate.of(2026, 3, 18), // Wed
                // gap on March 25
                LocalDate.of(2026, 4, 1),  // Wed
                LocalDate.of(2026, 4, 8)   // Wed
            ),
            today = todayAt(LocalDate.of(2026, 4, 8))
        )
        assertThat(result).isEqualTo(3)
    }

    // ──────────────────────────────────────────────
    // Edge case — created mid-week
    // ──────────────────────────────────────────────

    @Test
    fun `current streak - created mid-week does not penalize earlier days`() {
        // Created on Wednesday, scheduled every day
        val createdOn = LocalDate.of(2026, 4, 8) // Wednesday
        val today = LocalDate.of(2026, 4, 10) // Friday
        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(createdAt = createdOn),
            completedDates = completionsAt(
                LocalDate.of(2026, 4, 8),
                LocalDate.of(2026, 4, 9),
                LocalDate.of(2026, 4, 10)
            ),
            today = todayAt(today)
        )
        // Mon/Tue before creation should not count as missed
        assertThat(result).isEqualTo(3)
    }

    // ──────────────────────────────────────────────
    // Edge case — all days completed since creation
    // ──────────────────────────────────────────────

    @Test
    fun `current streak - perfect record since creation`() {
        val createdOn = LocalDate.of(2026, 4, 1)
        val today = LocalDate.of(2026, 4, 8)
        val allDates = generateSequence(createdOn) { it.plusDays(1) }
            .takeWhile { !it.isAfter(today) }
            .toList()
            .toTypedArray()

        val result = StreakCalculator.computeCurrentStreak(
            habit = habit(createdAt = createdOn),
            completedDates = completionsAt(*allDates),
            today = todayAt(today)
        )
        assertThat(result).isEqualTo(8)
    }

    @Test
    fun `best streak equals current streak for perfect record`() {
        val createdOn = LocalDate.of(2026, 4, 1)
        val today = LocalDate.of(2026, 4, 8)
        val allDates = generateSequence(createdOn) { it.plusDays(1) }
            .takeWhile { !it.isAfter(today) }
            .toList()
            .toTypedArray()
        val completions = completionsAt(*allDates)
        val h = habit(createdAt = createdOn)

        val current = StreakCalculator.computeCurrentStreak(h, completions, todayAt(today))
        val best = StreakCalculator.computeBestStreak(h, completions, todayAt(today))

        assertThat(current).isEqualTo(best)
        assertThat(best).isEqualTo(8)
    }
}
