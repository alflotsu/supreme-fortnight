package io.peng.sparrowdelivery.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import io.peng.sparrowdelivery.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

/**
 * iOS-style wheel date/time picker component with shadcn theming
 * Provides a smooth, native iOS experience for delivery scheduling
 */
@Composable
fun ShadcnWheelDateTimePickerDialog(
    onDateTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialDateTime: LocalDateTime = LocalDateTime.now().plusHours(1)
) {
    var selectedDate by remember { mutableStateOf(initialDateTime.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(initialDateTime.toLocalTime()) }
    var showDatePicker by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
         )
    ) {
        ShadcnTheme {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // Add some margin from screen edges
                    .wrapContentHeight(),
                shape = RoundedCornerShape(ShadcnBorderRadius.lg),
                colors = CardDefaults.cardColors(
                    containerColor = ShadcnTheme.colors.background
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 24.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ShadcnSpacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(ShadcnSpacing.lg)
                ) {
                    // Header with sliding toggle
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Column(modifier = Modifier.padding(top=8.dp)) {
                            Text(
                                text = "Schedule Delivery",
                                style = ShadcnTypography.h3.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = InterFontFamily
                                ),
                                color = ShadcnTheme.colors.foreground
                            )
                            Text(
                                text = if (showDatePicker) "Select date" else "Select time",
                                style = ShadcnTypography.muted,
                                color = ShadcnTheme.colors.mutedForeground,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        // Date/Time toggle
                        SlidingToggle(
                            options = listOf(
                                SlidingToggleOption(
                                    value = true,
                                    label = "Date",
                                    icon = Icons.Default.DateRange
                                ),
                                SlidingToggleOption(
                                    value = false,
                                    label = "Time",
                                    icon = Icons.Default.Schedule
                                )
                            ),
                            selectedOption = showDatePicker,
                            onOptionSelected = { showDatePicker = it },
                            modifier = Modifier.fillMaxWidth(),
                            height = 32.dp
                        )
                    }
                    
                    HorizontalDivider(color = ShadcnTheme.colors.border)
                    
                    // Wheel Picker Content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showDatePicker) {
                            WheelDatePicker(
                                startDate = LocalDate.now(),
                                yearsRange = IntRange(
                                    LocalDate.now().year,
                                    LocalDate.now().year + 1
                                ),
                                onSnappedDate = { snappedDate ->
                                    selectedDate = snappedDate
                                },
                                selectorProperties = WheelPickerDefaults.selectorProperties(
                                    enabled = true,
                                    shape = RoundedCornerShape(ShadcnBorderRadius.md),
                                    color = ShadcnTheme.colors.muted.copy(alpha = 0.3f),
                                    border = null
                                ),
                                textStyle = ShadcnTypography.p.copy(
                                    fontFamily = InterFontFamily,
                                    fontSize = 16.sp // Reduced from 18sp to prevent overflow
                                ),
                                textColor = ShadcnTheme.colors.foreground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        } else {
                            WheelTimePicker(
                                startTime = selectedTime,
                                onSnappedTime = { snappedTime ->
                                    selectedTime = snappedTime
                                },
                                selectorProperties = WheelPickerDefaults.selectorProperties(
                                    enabled = true,
                                    shape = RoundedCornerShape(ShadcnBorderRadius.md),
                                    color = ShadcnTheme.colors.muted.copy(alpha = 0.3f),
                                    border = null
                                ),
                                textStyle = ShadcnTypography.p.copy(
                                    fontFamily = InterFontFamily,
                                    fontSize = 16.sp // Consistent with date picker
                                ),
                                textColor = ShadcnTheme.colors.foreground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(horizontal = 4.dp) // Consistent padding
                            )
                        }
                    }
                    
                    HorizontalDivider(color = ShadcnTheme.colors.border)
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.md)
                    ) {
                        ShadcnTextButton(
                            text = "Cancel",
                            onClick = onDismiss,
                            variant = ShadcnButtonVariant.Outline,
                            modifier = Modifier.weight(1f)
                        )
                        
                        ShadcnTextButton(
                            text = if (showDatePicker) "Next" else "Schedule",
                            onClick = {
                                if (showDatePicker) {
                                    showDatePicker = false
                                } else {
                                    // Combine date and time, convert to millis
                                    val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                                    val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    onDateTimeSelected(millis)
                                }
                            },
                            variant = ShadcnButtonVariant.Default,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Preview of selected date/time
                    if (!showDatePicker) {
                        ShadcnCard(variant = ShadcnCardVariant.Outlined) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(ShadcnSpacing.sm)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = ShadcnTheme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Scheduled for:",
                                        style = ShadcnTypography.small,
                                        color = ShadcnTheme.colors.mutedForeground
                                    )
                                    Text(
                                        text = "${selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${selectedDate.monthValue}/${selectedDate.dayOfMonth}/${selectedDate.year} at ${selectedTime.hour.toString().padStart(2, '0')}:${selectedTime.minute.toString().padStart(2, '0')}",
                                        style = ShadcnTypography.p.copy(fontWeight = FontWeight.Medium),
                                        color = ShadcnTheme.colors.foreground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
