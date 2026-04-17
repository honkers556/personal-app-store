package com.personal.appstore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.personal.appstore.data.model.AppEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    viewModel: StoreViewModel,
    onAppClick: (AppEntry) -> Unit,
    onSettingsClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val apps = viewModel.filteredApps()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Store") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::setSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                ErrorView(
                    message = state.error!!,
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.fillMaxSize()
                )
            } else if (apps.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No apps found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apps, key = { it.id }) { app ->
                        val progress = state.installProgress[app.id]
                        val installedVersion = viewModel.getInstalledVersionCode(app.id)
                        AppCard(
                            app = app,
                            installedVersionCode = installedVersion,
                            installProgress = progress,
                            onClick = { onAppClick(app) },
                            onInstall = { viewModel.installApp(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search apps…") },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun AppCard(
    app: AppEntry,
    installedVersionCode: Int?,
    installProgress: Float?,
    onClick: () -> Unit,
    onInstall: () -> Unit
) {
    val hasUpdate = installedVersionCode != null && app.versionCode > installedVersionCode
    val isInstalled = installedVersionCode != null

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = app.iconUrl,
                contentDescription = "${app.name} icon",
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (hasUpdate) {
                        Badge { Text("Update") }
                    }
                }
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "v${app.versionName} · ${app.category}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.width(8.dp))

            if (installProgress != null) {
                CircularProgressIndicator(
                    progress = { installProgress },
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Button(onClick = onInstall) {
                    Text(
                        when {
                            hasUpdate -> "Update"
                            isInstalled -> "Open"
                            else -> "Install"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Failed to load catalog",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
