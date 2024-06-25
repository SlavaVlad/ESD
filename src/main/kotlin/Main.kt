/*
 * Copyright (c) 2024.
 * by https://github.com/SlavaVlad
 */


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@FlowPreview
@Composable
@Preview
fun App() {
    var music by remember { mutableStateOf(true) }
    var term by remember { mutableStateOf("") }
    val searchFlow = remember { MutableStateFlow("") }
    var limit by remember { mutableStateOf(10) }
    val entities = remember { mutableStateListOf<Api.Track>() }
    val downloadScope = CoroutineScope(Dispatchers.Default)

    LaunchedEffect(searchFlow) {
        searchFlow
            .debounce(500) // задержка в 500 миллисекунд
            .collect { term ->
                if (term.isNotBlank() and (term.length > 3)) {
                    entities.clear()
                    downloadScope.launch {
                        entities.addAll(Api.searchMusic(term, limit) ?: emptyList())
                    }
                }
            }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                Box(Modifier.background(MaterialTheme.colors.secondary).fillMaxWidth()) {
                    Box(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Epidemic sounds downloader", fontWeight = FontWeight.Bold)
                    }
                }
            }, content = { pv ->
                Row(Modifier.padding(vertical = pv.calculateTopPadding() + 16.dp).padding(horizontal = 24.dp)) {
                    Column() {
                        Section("Search", divider = false) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                OutlinedTextField(
                                    value = term,
                                    onValueChange = { newTerm ->
                                        term = newTerm
                                        searchFlow.value = newTerm
                                    },
                                    label = { Text("Search term") },
                                    modifier = Modifier.weight(0.8f)
                                )
                                OutlinedTextField(
                                    value = limit.toString(),
                                    onValueChange = { newLimit ->
                                        limit = newLimit.toIntOrNull() ?: 10
                                    },
                                    label = { Text("Limit") },
                                    modifier = Modifier.weight(0.2f)
                                )
                            }
                            Spacer(Modifier.padding(8.dp))
                        }
                        Section("Options") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("SFX", Modifier.weight(0.4f))
                                Switch(checked = music, onCheckedChange = { music = it }, Modifier.weight(0.2f))
                                Text("MUSIC", Modifier.weight(0.4f))
                            }
                        }

                        // list of entities
                        LazyColumn {
                            items(entities) { entity ->
                                Entity(entity, onDownload = {
                                    downloadScope.launch {
                                        Api.download(entity)
                                    }
                                }, onPlay = {
                                    downloadScope.launch {
                                        Api.play(entity)
                                    }
                                })
                            }
                        }
                    }
                }
            }
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Preview
@Composable
fun DefaultPreview() {
    App()
}