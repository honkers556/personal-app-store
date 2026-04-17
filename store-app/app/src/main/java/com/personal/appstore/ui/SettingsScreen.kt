package com.personal.appstore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit
) {
    var urlField by remember { mutableStateOf(viewModel.prefs.catalogUrl) }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp)
        ) {
            Text("Catalog URL", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "The URL to your catalog.json file on GitHub Pages.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = urlField,
                onValueChange = {
                    urlField = it
                    saved = false
                },
                label = { Text("catalog.json URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.saveCatalogUrl(urlField.trim())
                    saved = true
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save & Refresh")
            }

            if (saved) {
                Spacer(Modifier.height(8.dp))
                Text("Saved!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
