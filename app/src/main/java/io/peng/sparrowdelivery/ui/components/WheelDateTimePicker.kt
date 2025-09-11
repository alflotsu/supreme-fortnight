package io.peng.sparrowdelivery.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import io.peng.sparrowdelivery.ui.theme.*
import io.peng.sparrowdelivery.ui.components.stitch.StitchCard
import androidx.compose.material3.MaterialTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId


@Composable
fun WheelDateTimePickerDialog(
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
        StitchTheme {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // Add some margin from screen edges
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 24.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
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
                                style = SparrowTypography.h3.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = SparrowFontFamily
                                ),
                                color = SparrowTheme.colors.foreground
                            )
                            Text(
                                text = if (showDatePicker) "Select date" else "Select time",
                                style = SparrowTypography.muted,
                                color = SparrowTheme.colors.mutedForeground,
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
                            height = 48.dp
                        )
                    }
                    
                    HorizontalDivider(color = SparrowTheme.colors.border)
                    
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
                                    shape = RoundedCornerShape(SparrowBorderRadius.md),
                                    color = SparrowTheme.colors.muted.copy(alpha = 0.3f),
                                    border = null
                                ),
                                textStyle = SparrowTypography.p.copy(
                                    fontFamily = SparrowFontFamily,
                                    fontSize = 16.sp // Reduced from 18sp to prevent overflow
                                ),
                                textColor = SparrowTheme.colors.foreground,
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
                                    shape = RoundedCornerShape(SparrowBorderRadius.md),
                                    color = SparrowTheme.colors.muted.copy(alpha = 0.3f),
                                    border = null
                                ),
                                textStyle = SparrowTypography.p.copy(
                                    fontFamily = SparrowFontFamily,
                                    fontSize = 16.sp // Consistent with date picker
                                ),
                                textColor = SparrowTheme.colors.foreground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(horizontal = 4.dp) // Consistent padding
                            )
                        }
                    }
                    
                    HorizontalDivider(color = SparrowTheme.colors.border)
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SparrowTextButton(
                            text = "Cancel",
                            onClick = onDismiss,
                            variant = ButtonVariant.Outline,
                            modifier = Modifier.weight(1f)
                        )
                        
                        SparrowTextButton(
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
                            variant = ButtonVariant.Default,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Preview of selected date/time
                    if (!showDatePicker) {
                        StitchCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = SparrowTheme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Scheduled for:",
                                        style = SparrowTypography.small,
                                        color = SparrowTheme.colors.mutedForeground
                                    )
                                    Text(
                                        text = "${selectedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${selectedDate.monthValue}/${selectedDate.dayOfMonth}/${selectedDate.year} at ${selectedTime.hour.toString().padStart(2, '0')}:${selectedTime.minute.toString().padStart(2, '0')}",
                                        style = SparrowTypography.p.copy(fontWeight = FontWeight.Medium),
                                        color = SparrowTheme.colors.foreground
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
