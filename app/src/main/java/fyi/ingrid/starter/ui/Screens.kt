package fyi.ingrid.starter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fyi.ingrid.starter.DetailViewModel
import fyi.ingrid.starter.ListViewModel
import fyi.ingrid.starter.UiState

// The whole navigation graph: three destinations, and the stack that moves
// between them. Home opens List; tapping a row in List navigates to Detail
// with the chosen name as the route argument.
@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav) }
        composable("list") { ListScreen(nav) }
        composable("detail/{name}") { entry ->
            DetailScreen(entry.arguments?.getString("name").orEmpty())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text("start-android-compose") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("The starting point", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Three screens, one navigation graph. The list fetches live data " +
                    "on a background dispatcher; the detail screen shows one item. " +
                    "Loading and error are real UI states for you to build on."
            )
            Button(onClick = { nav.navigate("list") }) { Text("Open the list") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(nav: NavController) {
    val vm: ListViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("All 151 originals") }) }) { pad ->
        when (val s = state) {
            is UiState.Loading -> Centered(pad) { CircularProgressIndicator() }
            is UiState.Error -> Centered(pad) { ErrorText(s.message) }
            is UiState.Ready -> LazyColumn(Modifier.fillMaxSize().padding(pad)) {
                items(s.data) { item ->
                    Text(
                        text = item.name.replaceFirstChar { it.uppercase() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { nav.navigate("detail/${item.name}") }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(name: String) {
    val vm: DetailViewModel = viewModel()
    LaunchedEffect(name) { vm.load(name) }
    val state by vm.state.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        TopAppBar(title = { Text(name.replaceFirstChar { it.uppercase() }) })
    }) { pad ->
        when (val s = state) {
            is UiState.Loading -> Centered(pad) { CircularProgressIndicator() }
            is UiState.Error -> Centered(pad) { ErrorText(s.message) }
            is UiState.Ready -> Column(
                Modifier.padding(pad).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Types: ${s.data.types.joinToString(", ")}")
                Text("Height: ${s.data.heightM} m")
                Text("Weight: ${s.data.weightKg} kg")
            }
        }
    }
}

@Composable
private fun Centered(pad: androidx.compose.foundation.layout.PaddingValues, content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(pad).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        "$message. Check your connection and try again.",
        color = MaterialTheme.colorScheme.error,
    )
}
