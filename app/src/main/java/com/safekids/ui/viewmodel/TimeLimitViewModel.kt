package com.safekids.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safekids.domain.model.TimeLimit
import com.safekids.domain.repository.TimeLimitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeLimitViewModel @Inject constructor(
    private val timeLimitRepository: TimeLimitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimeLimitUiState>(TimeLimitUiState.Idle)
    val uiState: StateFlow<TimeLimitUiState> = _uiState.asStateFlow()

    private val _timeLimits = MutableStateFlow<List<TimeLimit>>(emptyList())
    val timeLimits: StateFlow<List<TimeLimit>> = _timeLimits.asStateFlow()

    private val _selectedTimeLimit = MutableStateFlow<TimeLimit?>(null)
    val selectedTimeLimit: StateFlow<TimeLimit?> = _selectedTimeLimit.asStateFlow()

    // Form state
    private val _dailyLimitHours = MutableStateFlow(0)
    val dailyLimitHours: StateFlow<Int> = _dailyLimitHours.asStateFlow()

    private val _dailyLimitMinutes = MutableStateFlow(0)
    val dailyLimitMinutes: StateFlow<Int> = _dailyLimitMinutes.asStateFlow()

    private val _weekdayLimitHours = MutableStateFlow<Int?>(null)
    val weekdayLimitHours: StateFlow<Int?> = _weekdayLimitHours.asStateFlow()

    private val _weekdayLimitMinutes = MutableStateFlow<Int?>(null)
    val weekdayLimitMinutes: StateFlow<Int?> = _weekdayLimitMinutes.asStateFlow()

    private val _weekendLimitHours = MutableStateFlow<Int?>(null)
    val weekendLimitHours: StateFlow<Int?> = _weekendLimitHours.asStateFlow()

    private val _weekendLimitMinutes = MutableStateFlow<Int?>(null)
    val weekendLimitMinutes: StateFlow<Int?> = _weekendLimitMinutes.asStateFlow()

    private val _warningThreshold = MutableStateFlow(80)
    val warningThreshold: StateFlow<Int> = _warningThreshold.asStateFlow()

    private val _isEnabled = MutableStateFlow(true)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    init {
        loadAllTimeLimits()
    }

    fun loadAllTimeLimits() {
        viewModelScope.launch {
            try {
                timeLimitRepository.observeAllActiveLimits()
                    .catch { e ->
                        _uiState.value = TimeLimitUiState.Error(
                            e.localizedMessage ?: "Failed to load time limits"
                        )
                    }
                    .collect { limits ->
                        _timeLimits.value = limits.sortedBy { it.appName }
                    }
            } catch (e: Exception) {
                _uiState.value = TimeLimitUiState.Error(
                    e.localizedMessage ?: "Failed to load time limits"
                )
            }
        }
    }

    fun loadTimeLimitForApp(packageName: String) {
        viewModelScope.launch {
            try {
                timeLimitRepository.observeLimitForApp(packageName)
                    .catch { e ->
                        _uiState.value = TimeLimitUiState.Error(
                            e.localizedMessage ?: "Failed to load time limit"
                        )
                    }
                    .collect { limit ->
                        _selectedTimeLimit.value = limit
                        limit?.let { populateFormFromTimeLimit(it) }
                    }
            } catch (e: Exception) {
                _uiState.value = TimeLimitUiState.Error(
                    e.localizedMessage ?: "Failed to load time limit"
                )
            }
        }
    }

    fun setTimeLimit(
        packageName: String,
        appName: String,
        dailyLimitMillis: Long,
        weekdayLimitMillis: Long? = null,
        weekendLimitMillis: Long? = null,
        warningThresholdPercent: Int = 80,
        isEnabled: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = TimeLimitUiState.Saving
                timeLimitRepository.setTimeLimit(
                    packageName = packageName,
                    appName = appName,
                    dailyLimitMillis = dailyLimitMillis,
                    weekdayLimitMillis = weekdayLimitMillis,
                    weekendLimitMillis = weekendLimitMillis
                )
                _uiState.value = TimeLimitUiState.Success("Time limit set successfully")
                resetForm()
            } catch (e: Exception) {
                _uiState.value = TimeLimitUiState.Error(
                    "Failed to set time limit: ${e.localizedMessage}"
                )
            }
        }
    }

    fun removeTimeLimit(packageName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = TimeLimitUiState.Saving
                timeLimitRepository.removeTimeLimit(packageName)
                _uiState.value = TimeLimitUiState.Success("Time limit removed")
                resetForm()
            } catch (e: Exception) {
                _uiState.value = TimeLimitUiState.Error(
                    "Failed to remove time limit: ${e.localizedMessage}"
                )
            }
        }
    }

    fun enableTimeLimit(packageName: String) {
        viewModelScope.launch {
            try {
                // Note: Repository doesn't have this method, implementing workaround
                val limit = timeLimitRepository.getLimitForApp(packageName)
                limit?.let {
                    timeLimitRepository.setTimeLimit(
                        packageName = it.packageName,
                        appName = it.appName,
                        dailyLimitMillis = it.dailyLimitMillis,
                        weekdayLimitMillis = it.weekdayLimitMillis,
                        weekendLimitMillis = it.weekendLimitMillis
                    )
                }
            } catch (e: Exception) {
                _uiState.value = TimeLimitUiState.Error(
                    "Failed to enable: ${e.localizedMessage}"
                )
            }
        }
    }

    fun disableTimeLimit(packageName: String) {
        viewModelScope.launch {
            try {
                timeLimitRepository.removeTimeLimit(packageName)
            } catch (e: Exception) {
                _uiState.value = TimeLimitUiState.Error(
                    "Failed to disable: ${e.localizedMessage}"
                )
            }
        }
    }

    fun resetLimitForApp(packageName: String) {
        viewModelScope.launch {
            try {
                timeLimitRepository.resetAppUsage(packageName)
                _uiState.value = TimeLimitUiState.Success("Usage reset successfully")
            } catch (e: Exception) {
                _uiState.value = TimeLimitUiState.Error(
                    "Failed to reset: ${e.localizedMessage}"
                )
            }
        }
    }

    // Form management
    fun setDailyLimit(hours: Int, minutes: Int) {
        _dailyLimitHours.value = hours
        _dailyLimitMinutes.value = minutes
    }

    fun setWeekdayLimit(hours: Int?, minutes: Int?) {
        _weekdayLimitHours.value = hours
        _weekdayLimitMinutes.value = minutes
    }

    fun setWeekendLimit(hours: Int?, minutes: Int?) {
        _weekendLimitHours.value = hours
        _weekendLimitMinutes.value = minutes
    }

    fun setWarningThreshold(percent: Int) {
        _warningThreshold.value = percent.coerceIn(0, 100)
    }

    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
    }

    fun getDailyLimitMillis(): Long {
        return ((_dailyLimitHours.value * 60L + _dailyLimitMinutes.value) * 60L * 1000L)
    }

    fun getWeekdayLimitMillis(): Long? {
        val hours = _weekdayLimitHours.value ?: return null
        val minutes = _weekdayLimitMinutes.value ?: 0
        return ((hours * 60L + minutes) * 60L * 1000L)
    }

    fun getWeekendLimitMillis(): Long? {
        val hours = _weekendLimitHours.value ?: return null
        val minutes = _weekendLimitMinutes.value ?: 0
        return ((hours * 60L + minutes) * 60L * 1000L)
    }

    fun validateForm(): String? {
        if (_dailyLimitHours.value == 0 && _dailyLimitMinutes.value == 0) {
            return "Please set a daily time limit"
        }
        if (_dailyLimitHours.value > 24) {
            return "Daily limit cannot exceed 24 hours"
        }
        return null
    }

    private fun populateFormFromTimeLimit(timeLimit: TimeLimit) {
        val dailyHours = (timeLimit.dailyLimitMillis / (60 * 60 * 1000)).toInt()
        val dailyMinutes = ((timeLimit.dailyLimitMillis % (60 * 60 * 1000)) / (60 * 1000)).toInt()
        _dailyLimitHours.value = dailyHours
        _dailyLimitMinutes.value = dailyMinutes

        timeLimit.weekdayLimitMillis?.let { millis ->
            val hours = (millis / (60 * 60 * 1000)).toInt()
            val minutes = ((millis % (60 * 60 * 1000)) / (60 * 1000)).toInt()
            _weekdayLimitHours.value = hours
            _weekdayLimitMinutes.value = minutes
        }

        timeLimit.weekendLimitMillis?.let { millis ->
            val hours = (millis / (60 * 60 * 1000)).toInt()
            val minutes = ((millis % (60 * 60 * 1000)) / (60 * 1000)).toInt()
            _weekendLimitHours.value = hours
            _weekendLimitMinutes.value = minutes
        }

        _warningThreshold.value = timeLimit.warningThresholdPercent
        _isEnabled.value = timeLimit.isEnabled
    }

    fun resetForm() {
        _dailyLimitHours.value = 0
        _dailyLimitMinutes.value = 0
        _weekdayLimitHours.value = null
        _weekdayLimitMinutes.value = null
        _weekendLimitHours.value = null
        _weekendLimitMinutes.value = null
        _warningThreshold.value = 80
        _isEnabled.value = true
        _selectedTimeLimit.value = null
    }

    fun getExceededLimits(): List<TimeLimit> {
        return _timeLimits.value.filter { it.limitExceeded }
    }

    fun getEnabledLimitsCount(): Int {
        return _timeLimits.value.count { it.isEnabled }
    }
}

sealed class TimeLimitUiState {
    object Idle : TimeLimitUiState()
    object Saving : TimeLimitUiState()
    data class Success(val message: String) : TimeLimitUiState()
    data class Error(val message: String) : TimeLimitUiState()
}
