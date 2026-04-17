package com.personal.appstore.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.appstore.data.CatalogRepository
import com.personal.appstore.data.PreferencesRepository
import com.personal.appstore.data.model.AppEntry
import com.personal.appstore.install.ApkInstaller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val apps: List<AppEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdated: String = "",
    val searchQuery: String = "",
    val installProgress: Map<String, Float> = emptyMap()
)

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val catalogRepo = CatalogRepository()
    val prefs = PreferencesRepository(application)
    val installer = ApkInstaller(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            catalogRepo.fetchCatalog(prefs.catalogUrl)
                .onSuccess { catalog ->
                    _uiState.update {
                        it.copy(
                            apps = catalog.apps,
                            lastUpdated = catalog.updated,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun filteredApps(): List<AppEntry> {
        val q = _uiState.value.searchQuery.trim().lowercase()
        return if (q.isEmpty()) _uiState.value.apps
        else _uiState.value.apps.filter {
            it.name.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.category.lowercase().contains(q)
        }
    }

    fun installApp(app: AppEntry) {
        viewModelScope.launch {
            _uiState.update { it.copy(installProgress = it.installProgress + (app.id to 0f)) }
            installer.downloadAndInstall(app.apkUrl, app.id) { progress ->
                _uiState.update { it.copy(installProgress = it.installProgress + (app.id to progress)) }
            }.onSuccess {
                _uiState.update { it.copy(installProgress = it.installProgress - app.id) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        installProgress = it.installProgress - app.id,
                        error = "Install failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun saveCatalogUrl(url: String) {
        prefs.catalogUrl = url
        refresh()
    }

    fun getInstalledVersionCode(id: String) = installer.getInstalledVersionCode(id)
}
