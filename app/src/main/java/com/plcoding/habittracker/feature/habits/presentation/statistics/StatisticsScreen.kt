package com.plcoding.habittracker.feature.habits.presentation.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.habittracker.R
import com.plcoding.habittracker.core.presentation.designsystem.HabitIconContainer
import com.plcoding.habittracker.core.presentation.designsystem.HabitTrackerTopBar
import com.plcoding.habittracker.feature.habits.domain.HabitIcon
import com.plcoding.habittracker.ui.theme.HabitTrackerTheme
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StatisticsScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StatisticsScreen(
        state = state,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun StatisticsScreen(
    state: StatisticsState,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        HabitTrackerTopBar(
            title = stringResource(R.string.statistics),
            onBackClick = onNavigateBack,
        )

        Spacer(modifier = Modifier.height(24.dp))

        SummaryCards(
            thisWeekPercentage = state.thisWeekPercentage,
            bestStreak = state.bestStreak,
            activeCount = state.activeHabitCount,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ActivityHeatmap(heatmapData = state.heatmapData)

        Spacer(modifier = Modifier.height(24.dp))

        StreaksList(habitStreaks = state.habitStreaks)
    }
}

@Composable
private fun SummaryCards(
    thisWeekPercentage: Int,
    bestStreak: Int,
    activeCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            label = stringResource(R.string.this_week),
            value = "$thisWeekPercentage%",
            valueColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = stringResource(R.string.best_streak),
            value = "$bestStreak",
            valueColor = HabitTrackerTheme.colors.success,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = stringResource(R.string.active),
            value = "$activeCount",
            valueColor = HabitTrackerTheme.colors.accentPink,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = HabitTrackerTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = valueColor,
        )
    }
}

@Composable
private fun ActivityHeatmap(
    heatmapData: List<HeatmapDay>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.activity),
            style = MaterialTheme.typography.bodySmall,
            color = HabitTrackerTheme.colors.textTertiary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        val dayLabels = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(modifier = Modifier.width(24.dp))
            dayLabels.forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = HabitTrackerTheme.colors.textTertiary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val weeks = heatmapData.chunked(7)
        weeks.forEachIndexed { weekIndex, weekDays ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.width(24.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Text(
                        text = "W${weekIndex + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = HabitTrackerTheme.colors.textTertiary,
                    )
                }
                weekDays.forEach { day ->
                    HeatmapCell(
                        day = day,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            if (weekIndex < weeks.lastIndex) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HeatmapLegend()
    }
}

@Composable
private fun HeatmapCell(
    day: HeatmapDay,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val borderColor = HabitTrackerTheme.colors.border
    val surfaceElevatedColor = HabitTrackerTheme.colors.surfaceElevated

    val cellColor = when {
        day.isFuture -> Color.Transparent
        day.completionRatio <= 0f -> surfaceElevatedColor
        day.completionRatio <= 0.25f -> primaryColor.copy(alpha = 0.3f)
        day.completionRatio <= 0.5f -> primaryColor.copy(alpha = 0.6f)
        day.completionRatio <= 0.75f -> primaryColor.copy(alpha = 0.85f)
        else -> primaryColor
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (day.isFuture) {
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = borderColor,
                            cornerRadius = CornerRadius(6.dp.toPx()),
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(4.dp.toPx(), 4.dp.toPx())
                                )
                            )
                        )
                    }
                } else if (day.isToday) {
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(cellColor)
                        .border(1.5.dp, secondaryColor, RoundedCornerShape(6.dp))
                } else {
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(cellColor)
                }
            )
    )
}

@Composable
private fun HeatmapLegend(modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceElevatedColor = HabitTrackerTheme.colors.surfaceElevated

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.less),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
            color = HabitTrackerTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.width(4.dp))
        listOf(
            surfaceElevatedColor,
            primaryColor.copy(alpha = 0.3f),
            primaryColor.copy(alpha = 0.6f),
            primaryColor.copy(alpha = 0.85f),
            primaryColor,
        ).forEach { color ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
        Text(
            text = stringResource(R.string.more),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
            color = HabitTrackerTheme.colors.textTertiary,
        )
    }
}

@Composable
private fun StreaksList(
    habitStreaks: List<HabitStreakUi>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.streaks),
            style = MaterialTheme.typography.bodySmall,
            color = HabitTrackerTheme.colors.textTertiary,
        )

        habitStreaks.forEach { streak ->
            StreakItem(streak = streak)
        }
    }
}

@Composable
private fun StreakItem(
    streak: HabitStreakUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HabitIconContainer(
            icon = streak.icon,
            size = 36.dp,
            iconSize = 18.dp,
            cornerRadius = 10.dp,
        )

        Text(
            text = streak.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${streak.currentStreak}",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 21.sp),
                color = HabitTrackerTheme.colors.success,
            )
            Text(
                text = stringResource(R.string.best, streak.bestStreak),
                style = MaterialTheme.typography.labelSmall,
                color = HabitTrackerTheme.colors.textTertiary,
            )
        }
    }
}

@Preview
@Composable
private fun StatisticsScreenPreview() {
    HabitTrackerTheme {
        StatisticsScreen(
            state = StatisticsState(
                thisWeekPercentage = 72,
                bestStreak = 15,
                activeHabitCount = 5,
                heatmapData = (0 until 28).map { i ->
                    HeatmapDay(
                        date = LocalDate.now().minusDays((27 - i).toLong()),
                        completionRatio = (i % 5) / 4f,
                        isToday = i == 24,
                        isFuture = i > 24,
                    )
                },
                habitStreaks = listOf(
                    HabitStreakUi(1, "Morning Run", HabitIcon.RUN, 5, 12),
                    HabitStreakUi(2, "Read", HabitIcon.READ, 3, 8),
                    HabitStreakUi(3, "Meditate", HabitIcon.MEDITATE, 0, 4),
                ),
            ),
            onNavigateBack = {},
        )
    }
}
