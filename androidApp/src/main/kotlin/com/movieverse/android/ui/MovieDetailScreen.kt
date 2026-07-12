package com.movieverse.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.movieverse.shared.domain.error.toUserMessage
import com.movieverse.shared.presentation.MovieDetailUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    movieTitle: String,
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = koinViewModel { parametersOf(movieId) }
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                // Shown immediately from the nav argument, not just once the detail
                // fetch succeeds — otherwise Loading/Error states show a bare back
                // arrow with no title.
                title = { Text(movieTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is MovieDetailUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is MovieDetailUiState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.error.toUserMessage(), style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = viewModel::load, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Retry")
                        }
                    }
                }

                is MovieDetailUiState.Success -> Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = state.movie.posterUrl,
                        contentDescription = state.movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(320.dp)
                    )
                    Text(
                        text = "★ ${state.movie.rating}  ·  ${state.movie.releaseDate.toReadableDate()}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    if (state.movie.genres.isNotEmpty()) {
                        Text(
                            text = state.movie.genres.joinToString(", "),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Text(
                        text = state.movie.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
