package com.safekids.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safekids.domain.model.AppCategory
import com.safekids.domain.model.InstalledApp
import com.safekids.domain.repository.AppManagementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    private val appManagementRepository: AppManagementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppListUiState>(AppListUiState.Loading)
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    private val _apps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val apps: StateFlow<List<InstalledApp>> = _apps.asStateFlow()

    private val _filteredApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val filteredApps: StateFlow<List<InstalledApp>> = _filteredApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<AppCategory?>(null)
    val selectedCategory: StateFlow<AppCategory?> = _selectedCategory.asStateFlow()

    private val _showBlockedOnly = MutableStateFlow(false)
    val showBlockedOnly: StateFlow<Boolean> = _showBlockedOnly.asStateFlow()

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.value = AppListUiState.Loading
            try {
                appManagementRepository.observeAllUserApps()
                    .catch { e ->
                        _uiState.value = AppListUiState.Error(
                            e.localizedMessage ?: "Failed to load apps"
                        )
                    }
                    .collect { appList ->
                        _apps.value = appList.sortedBy { it.appName }
                        applyFilters()
                        _uiState.value = AppListUiState.Success
                    }
            } catch (e: Exception) {
                _uiState.value = AppListUiState.Error(
                    e.localizedMessage ?: "Failed to load apps"
                )
            }
        }
    }

    fun syncInstalledApps() {
        viewModelScope.launch {
            try {
                _uiState.value = AppListUiState.Loading
                appManagementRepository.syncInstalledApps()
                loadInstalledApps()
            } catch (e: Exception) {
                _uiState.value = AppListUiState.Error(
                    "Failed to sync: ${e.localizedMessage}"
                )
            }
        }
    }

    fun blockApp(packageName: String) {
        viewModelScope.launch {
            try {
                appManagementRepository.blockApp(packageName)
                // Apps list will auto-update via Flow
            } catch (e: Exception) {
                _uiState.value = AppListUiState.Error(
                    "Failed to block app: ${e.localizedMessage}"
                )
            }
        }
    }

    fun unblockApp(packageName: String) {
        viewModelScope.launch {
            try {
                appManagementRepository.unblockApp(packageName)
                // Apps list will auto-update via Flow
            } catch (e: Exception) {
                _uiState.value = AppListUiState.Error(
                    "Failed to unblock app: ${e.localizedMessage}"
                )
            }
        }
    }

    fun toggleAppBlock(packageName: String, currentlyBlocked: Boolean) {
        if (currentlyBlocked) {
            unblockApp(packageName)
        } else {
            blockApp(packageName)
        }
    }

    fun searchApps(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun filterByCategory(category: AppCategory?) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun toggleShowBlockedOnly() {
        _showBlockedOnly.value = !_showBlockedOnly.value
        applyFilters()
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _showBlockedOnly.value = false
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = _apps.value

        // Search filter
        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter {
                it.appName.contains(_searchQuery.value, ignoreCase = true) ||
                        it.packageName.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        // Category filter
        _selectedCategory.value?.let { category ->
            filtered = filtered.filter { it.category == category }
        }

        // Blocked filter
        if (_showBlockedOnly.value) {
            filtered = filtered.filter { it.isBlocked }
        }

        _filteredApps.value = filtered
    }

    fun getCategories(): List<AppCategory> {
        return _apps.value
            .map { it.category }
            .distinct()
            .sorted()
    }

    fun getBlockedAppsCount(): Int {
        return _apps.value.count { it.isBlocked }
    }

    fun getTotalAppsCount(): Int {
        return _apps.value.size
    }

    fun getAppsByCategory(category: AppCategory): List<InstalledApp> {
        return _apps.value.filter { it.category == category }
    }
}

sealed class AppListUiState {
    object Loading : AppListUiState()
    object Success : AppListUiState()
    data class Error(val message: String) : AppListUiState()
}
