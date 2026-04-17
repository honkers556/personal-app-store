package com.personal.appstore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.personal.appstore.data.model.AppEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: AppEntry,
    viewModel: StoreViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val installProgress = state.installProgress[app.id]
    val installedVersion = viewModel.getInstalledVersionCode(app.id)
    val hasUpdate = installedVersion != null && app.versionCode > installedVersion

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(app.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = app.iconUrl,
                    contentDescription = "${app.name} icon",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(app.name, style = MaterialTheme.typography.headlineSmall)
                    Text(app.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    Text("v${app.versionName}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(20.dp))

            if (installProgress != null) {
                LinearProgressIndicator(
                    progress = { installProgress },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Downloading… ${(installProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Button(
                    onClick = { viewModel.installApp(app) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        when {
                            hasUpdate -> "Update to v${app.versionName}"
                            installedVersion != null -> "Reinstall"
                            else -> "Install"
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionTitle("Description")
            Text(app.description, style = MaterialTheme.typography.bodyMedium)

            if (!app.changelog.isNullOrBlank()) {
                Spacer(Modifier.height(20.dp))
                SectionTitle("What's New")
                Text(app.changelog, style = MaterialTheme.typography.bodyMedium)
            }

            app.sizeBytes?.let { bytes ->
                Spacer(Modifier.height(20.dp))
                SectionTitle("Details")
                DetailRow("Size", formatSize(bytes))
                DetailRow("Package", app.id)
                DetailRow("Min Android", "API ${app.minSdk}")
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.outline)
        Text(value)
    }
}

private fun formatSize(bytes: Long): String {
    return when {
        bytes >= 1_000_000 -> "%.1f MB".format(bytes / 1_000_000.0)
        bytes >= 1_000 -> "%.1f KB".format(bytes / 1_000.0)
        else -> "$bytes B"
    }
}
