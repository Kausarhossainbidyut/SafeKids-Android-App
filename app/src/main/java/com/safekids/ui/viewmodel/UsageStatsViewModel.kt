package com.safekids.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safekids.domain.model.AppUsage
import com.safekids.domain.model.UsageStatistics
import com.safekids.domain.repository.UsageStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UsageStatsViewModel @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsageStatsUiState>(UsageStatsUiState.Loading)
    val uiState: StateFlow<UsageStatsUiState> = _uiState.asStateFlow()

    private val _todayUsage = MutableStateFlow<List<AppUsage>>(emptyList())
    val todayUsage: StateFlow<List<AppUsage>> = _todayUsage.asStateFlow()

    private val _statistics = MutableStateFlow<UsageStatistics?>(null)
    val statistics: StateFlow<UsageStatistics?> = _statistics.asStateFlow()

    init {
        // Sync usage stats on start
        syncUsageStats()
        loadTodayUsage()
        observeStatistics()
    }

    fun loadTodayUsage() {
        viewModelScope.launch {
            _uiState.value = UsageStatsUiState.Loading
            try {
                usageStatsRepository.observeTodayUsage()
                    .catch { e ->
                        _uiState.value = UsageStatsUiState.Error(
                            e.localizedMessage ?: "Failed to load usage data"
                        )
                    }
                    .collect { usageList ->
                        _todayUsage.value = usageList.sortedByDescending { it.usageTimeMillis }
                        _uiState.value = UsageStatsUiState.Success
                    }
            } catch (e: Exception) {
                _uiState.value = UsageStatsUiState.Error(
                    e.localizedMessage ?: "Failed to load usage data"
                )
            }
        }
    }

    fun loadUsageForDate(date: String) {
        viewModelScope.launch {
            _uiState.value = UsageStatsUiState.Loading
            try {
                val usageList = usageStatsRepository.getUsageForDate(date)
                _todayUsage.value = usageList.sortedByDescending { it.usageTimeMillis }
                _uiState.value = UsageStatsUiState.Success
            } catch (e: Exception) {
                _uiState.value = UsageStatsUiState.Error(
                    e.localizedMessage ?: "Failed to load usage data"
                )
            }
        }
    }

    fun loadUsageForDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            _uiState.value = UsageStatsUiState.Loading
            try {
                val usageList = usageStatsRepository.getUsageForDateRange(startDate, endDate)
                _todayUsage.value = usageList.sortedByDescending { it.usageTimeMillis }
                _uiState.value = UsageStatsUiState.Success
            } catch (e: Exception) {
                _uiState.value = UsageStatsUiState.Error(
                    e.localizedMessage ?: "Failed to load usage data"
                )
            }
        }
    }

    private fun observeStatistics() {
        viewModelScope.launch {
            try {
                val today = java.time.LocalDate.now().toString()
                val stats = usageStatsRepository.getUsageStatistics(today, today)
                _statistics.value = stats
            } catch (e: Exception) {
                // Handle error silently for statistics
            }
        }
    }

    fun refreshUsage() {
        loadTodayUsage()
    }

    fun syncUsageStats() {
        viewModelScope.launch {
            try {
                usageStatsRepository.syncUsageStats()
                loadTodayUsage()
            } catch (e: Exception) {
                _uiState.value = UsageStatsUiState.Error(
                    "Failed to sync: ${e.localizedMessage}"
                )
            }
        }
    }

    fun getTopApps(limit: Int = 5): List<AppUsage> {
        return _todayUsage.value.take(limit)
    }

    fun getTotalScreenTime(): Long {
        return _todayUsage.value.sumOf { it.usageTimeMillis }
    }

    fun getTotalLaunches(): Int {
        return _todayUsage.value.sumOf { it.launchCount }
    }
}

sealed class UsageStatsUiState {
    object Loading : UsageStatsUiState()
    object Success : UsageStatsUiState()
    data class Error(val message: String) : UsageStatsUiState()
}
