package com.safekids.ui.screen.timelimit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.safekids.ui.viewmodel.TimeLimitViewModel
import com.safekids.ui.viewmodel.TimeLimitUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeLimitScreen(
    packageName: String,
    appName: String,
    onNavigateBack: () -> Unit,
    viewModel: TimeLimitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dailyLimitHours by viewModel.dailyLimitHours.collectAsState()
    val dailyLimitMinutes by viewModel.dailyLimitMinutes.collectAsState()
    val weekdayLimitHours by viewModel.weekdayLimitHours.collectAsState()
    val weekdayLimitMinutes by viewModel.weekdayLimitMinutes.collectAsState()
    val weekendLimitHours by viewModel.weekendLimitHours.collectAsState()
    val weekendLimitMinutes by viewModel.weekendLimitMinutes.collectAsState()
    val warningThreshold by viewModel.warningThreshold.collectAsState()
    val isEnabled by viewModel.isEnabled.collectAsState()

    var showWeekdayLimit by remember { mutableStateOf(false) }
    var showWeekendLimit by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Load existing time limit
    LaunchedEffect(packageName) {
        viewModel.loadTimeLimitForApp(packageName)
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is TimeLimitUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as TimeLimitUiState.Success).message,
                    duration = SnackbarDuration.Short
                )
                // Navigate back after successful save
                kotlinx.coroutines.delay(1000)
                onNavigateBack()
            }
            is TimeLimitUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (uiState as TimeLimitUiState.Error).message,
                    duration = SnackbarDuration.Long
                )
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Time Limit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.removeTimeLimit(packageName)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remove")
                    }
                    Button(
                        onClick = {
                            val validationError = viewModel.validateForm()
                            if (validationError != null) {
                                // Show error
                            } else {
                                viewModel.setTimeLimit(
                                    packageName = packageName,
                                    appName = appName,
                                    dailyLimitMillis = viewModel.getDailyLimitMillis(),
                                    weekdayLimitMillis = viewModel.getWeekdayLimitMillis(),
                                    weekendLimitMillis = viewModel.getWeekendLimitMillis(),
                                    warningThresholdPercent = warningThreshold,
                                    isEnabled = isEnabled
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState !is TimeLimitUiState.Saving
                    ) {
                        if (uiState is TimeLimitUiState.Saving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Name Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Enable/Disable Switch
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Enable Time Limit",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Enforce time restrictions for this app",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { viewModel.setEnabled(it) }
                        )
                    }
                }
            }

            // Daily Limit
            item {
                Text(
                    text = "Daily Time Limit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = if (dailyLimitHours > 0) dailyLimitHours.toString() else "",
                                onValueChange = {
                                    val hours = it.toIntOrNull() ?: 0
                                    viewModel.setDailyLimit(hours, dailyLimitMinutes)
                                },
                                label = { Text("Hours") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = if (dailyLimitMinutes > 0) dailyLimitMinutes.toString() else "",
                                onValueChange = {
                                    val minutes = it.toIntOrNull() ?: 0
                                    viewModel.setDailyLimit(dailyLimitHours, minutes)
                                },
                                label = { Text("Minutes") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                        Text(
                            text = "Set the maximum daily usage time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Advanced Options
            item {
                Text(
                    text = "Advanced Options",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Weekday Limit Toggle
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Different limit for weekdays",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Switch(
                                checked = showWeekdayLimit,
                                onCheckedChange = {
                                    showWeekdayLimit = it
                                    if (!it) {
                                        viewModel.setWeekdayLimit(null, null)
                                    } else {
                                        viewModel.setWeekdayLimit(dailyLimitHours, dailyLimitMinutes)
                                    }
                                }
                            )
                        }

                        if (showWeekdayLimit) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = weekdayLimitHours?.toString() ?: "",
                                    onValueChange = {
                                        val hours = it.toIntOrNull()
                                        viewModel.setWeekdayLimit(hours, weekdayLimitMinutes)
                                    },
                                    label = { Text("Hours") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = weekdayLimitMinutes?.toString() ?: "",
                                    onValueChange = {
                                        val minutes = it.toIntOrNull()
                                        viewModel.setWeekdayLimit(weekdayLimitHours, minutes)
                                    },
                                    label = { Text("Minutes") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }

            // Weekend Limit Toggle
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Different limit for weekends",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Switch(
                                checked = showWeekendLimit,
                                onCheckedChange = {
                                    showWeekendLimit = it
                                    if (!it) {
                                        viewModel.setWeekendLimit(null, null)
                                    } else {
                                        viewModel.setWeekendLimit(dailyLimitHours, dailyLimitMinutes)
                                    }
                                }
                            )
                        }

                        if (showWeekendLimit) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = weekendLimitHours?.toString() ?: "",
                                    onValueChange = {
                                        val hours = it.toIntOrNull()
                                        viewModel.setWeekendLimit(hours, weekendLimitMinutes)
                                    },
                                    label = { Text("Hours") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = weekendLimitMinutes?.toString() ?: "",
                                    onValueChange = {
                                        val minutes = it.toIntOrNull()
                                        viewModel.setWeekendLimit(weekendLimitHours, minutes)
                                    },
                                    label = { Text("Minutes") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }

            // Warning Threshold
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Warning at ${warningThreshold}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = warningThreshold.toFloat(),
                            onValueChange = { viewModel.setWarningThreshold(it.toInt()) },
                            valueRange = 50f..100f,
                            steps = 9
                        )
                        Text(
                            text = "Show warning when ${warningThreshold}% of time limit is used",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
