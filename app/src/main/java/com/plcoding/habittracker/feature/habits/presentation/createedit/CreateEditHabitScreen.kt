package com.plcoding.habittracker.feature.habits.presentation.createedit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.habittracker.R
import com.plcoding.habittracker.core.presentation.ObserveAsEvents
import com.plcoding.habittracker.core.presentation.designsystem.HabitIconContainer
import com.plcoding.habittracker.core.presentation.designsystem.HabitTrackerButton
import com.plcoding.habittracker.core.presentation.designsystem.HabitTrackerDialog
import com.plcoding.habittracker.core.presentation.designsystem.HabitTrackerTopBar
import com.plcoding.habittracker.feature.habits.domain.HabitIcon
import com.plcoding.habittracker.feature.habits.domain.WeekDays
import com.plcoding.habittracker.ui.theme.HabitTrackerTheme
import com.plcoding.habittracker.ui.theme.InterFontFamily
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek

@Composable
fun CreateEditHabitScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: CreateEditHabitViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CreateEditHabitEvent.HabitSaved -> onNavigateBack()
            CreateEditHabitEvent.HabitDeleted -> onNavigateBack()
        }
    }

    CreateEditHabitScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateEditHabitScreen(
    state: CreateEditHabitState,
    onAction: (CreateEditHabitAction) -> Unit,
    onBackClick: () -> Unit,
) {
    if (state.isDeleteDialogVisible) {
        HabitTrackerDialog(
            title = stringResource(R.string.delete_habit_title),
            description = stringResource(R.string.delete_habit_description),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { onAction(CreateEditHabitAction.OnConfirmDelete) },
            onDismiss = { onAction(CreateEditHabitAction.OnDismissDeleteDialog) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp),
    ) {
        HabitTrackerTopBar(
            title = stringResource(
                if (state.isEditMode) R.string.edit_habit else R.string.new_habit
            ),
            onBackClick = onBackClick,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Icon display
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary

            HabitIconContainer(
                icon = state.selectedIcon,
                size = 72.dp,
                iconSize = 28.dp,
                cornerRadius = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.surface,
                borderStroke = if (state.isEditMode) {
                    BorderStroke(2.dp, primaryColor)
                } else {
                    null
                },
                modifier = if (!state.isEditMode) {
                    Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = primaryColor,
                                cornerRadius = CornerRadius(20.dp.toPx()),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(6.dp.toPx(), 6.dp.toPx())
                                    )
                                )
                            )
                        }
                        .clickable { onAction(CreateEditHabitAction.OnToggleIconPicker) }
                } else {
                    Modifier.clickable { onAction(CreateEditHabitAction.OnToggleIconPicker) }
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.tap_to_change_icon),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Icon picker
        AnimatedVisibility(visible = state.isIconPickerExpanded || !state.isEditMode) {
            Column {
                Text(
                    text = stringResource(R.string.choose_icon),
                    style = MaterialTheme.typography.bodySmall,
                    color = HabitTrackerTheme.colors.textTertiary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconPickerGrid(
                    selectedIcon = state.selectedIcon,
                    onIconSelect = { onAction(CreateEditHabitAction.OnIconSelect(it)) },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Name field
        Text(
            text = stringResource(R.string.habit_name),
            style = MaterialTheme.typography.bodySmall,
            color = HabitTrackerTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        NameTextField(
            value = state.habitName,
            onValueChange = { onAction(CreateEditHabitAction.OnNameChange(it)) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Day picker
        Text(
            text = stringResource(R.string.schedule),
            style = MaterialTheme.typography.bodySmall,
            color = HabitTrackerTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        DayOfWeekPicker(
            weekDays = state.weekDays,
            onToggleDay = { onAction(CreateEditHabitAction.OnToggleDay(it)) },
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save button
        HabitTrackerButton(
            text = stringResource(
                if (state.isEditMode) R.string.save_changes else R.string.save_habit
            ),
            onClick = { onAction(CreateEditHabitAction.OnSaveClick) },
            enabled = state.canSave && !state.isSaving,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Secondary action
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(
                    if (state.isEditMode) R.string.delete_habit else R.string.discard_habit
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                ),
                color = HabitTrackerTheme.colors.destructive,
                modifier = Modifier.clickable {
                    if (state.isEditMode) {
                        onAction(CreateEditHabitAction.OnDeleteClick)
                    } else {
                        onBackClick()
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconPickerGrid(
    selectedIcon: HabitIcon,
    onIconSelect: (HabitIcon) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 5,
    ) {
        HabitIcon.entries.forEach { icon ->
            val isSelected = icon == selectedIcon
            HabitIconContainer(
                icon = icon,
                size = 48.dp,
                iconSize = 28.dp,
                cornerRadius = 12.dp,
                backgroundColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    HabitTrackerTheme.colors.surfaceElevated
                },
                iconTint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                borderStroke = if (isSelected) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
                } else {
                    null
                },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onIconSelect(icon) },
            )
        }
    }
}

@Composable
private fun NameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, HabitTrackerTheme.colors.border, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        textStyle = TextStyle(
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
    )
}

@Composable
private fun DayOfWeekPicker(
    weekDays: WeekDays,
    onToggleDay: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    val days = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "T",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "S",
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        days.forEach { (dayOfWeek, label) ->
            val isSelected = weekDays.isScheduledFor(dayOfWeek)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(HabitTrackerTheme.colors.surfaceBright)
                                .border(
                                    1.5.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(10.dp)
                                )
                        } else {
                            Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    HabitTrackerTheme.colors.border,
                                    RoundedCornerShape(10.dp)
                                )
                        }
                    )
                    .clickable { onToggleDay(dayOfWeek) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        HabitTrackerTheme.colors.textTertiary
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun CreateHabitScreenPreview() {
    HabitTrackerTheme {
        CreateEditHabitScreen(
            state = CreateEditHabitState(
                isEditMode = false,
                habitName = "Morning Run",
                selectedIcon = HabitIcon.RUN,
                weekDays = WeekDays(monday = true, wednesday = true, friday = true),
                canSave = true,
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun EditHabitScreenPreview() {
    HabitTrackerTheme {
        CreateEditHabitScreen(
            state = CreateEditHabitState(
                isEditMode = true,
                habitName = "Read",
                selectedIcon = HabitIcon.READ,
                weekDays = WeekDays(
                    monday = true, tuesday = true, wednesday = true,
                    thursday = true, friday = true
                ),
                canSave = true,
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}
