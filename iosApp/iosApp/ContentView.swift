import SwiftUI
import shared

// Navigating by (id, title) — not the Kotlin Movie object itself, since Kotlin-exported
// classes aren't guaranteed Hashable — lets the detail screen show the title
// immediately while it fetches full details by id, instead of a bare back arrow.
private struct MovieDetailArgs: Hashable {
    let id: Int32
    let title: String
}

struct ContentView: View {
    @StateObject private var viewModel = MovieListViewModel()

    var body: some View {
        NavigationStack {
            content
                .navigationTitle("MovieVerse")
                .navigationDestination(for: MovieDetailArgs.self) { args in
                    MovieDetailView(movieId: args.id, movieTitle: args.title)
                }
        }
        .onAppear { viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.uiState {
        case .loading:
            ProgressView()

        case .empty:
            MessageView(message: "No movies found.", onRetry: nil)

        case .error(let message):
            MessageView(message: message, onRetry: { viewModel.load() })

        case .success(let movies):
            List(movies, id: \.id) { movie in
                NavigationLink(value: MovieDetailArgs(id: movie.id, title: movie.title)) {
                    MovieRow(movie: movie)
                }
            }
            .listStyle(.plain)
        }
    }
}

private struct MessageView: View {
    let message: String
    let onRetry: (() -> Void)?

    var body: some View {
        VStack(spacing: 12) {
            Text(message)
                .multilineTextAlignment(.center)
                .foregroundStyle(.secondary)
            if let onRetry {
                Button("Retry", action: onRetry)
            }
        }
        .padding()
    }
}

private struct MovieRow: View {
    let movie: Movie

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            AsyncImage(url: URL(string: movie.posterUrl)) { image in
                image.resizable().aspectRatio(contentMode: .fill)
            } placeholder: {
                Color.secondary.opacity(0.2)
            }
            .frame(width: 56, height: 84)
            .clipShape(RoundedRectangle(cornerRadius: 8))

            VStack(alignment: .leading, spacing: 4) {
                Text(movie.title)
                    .font(.headline)
                Text(movie.releaseDate.toReadableDate())
                    .font(.caption)
                    .foregroundStyle(.secondary)
                Text(movie.overview)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .lineLimit(2)
                Text("★ \(String(format: "%.1f", movie.rating))")
                    .font(.caption)
                    .foregroundStyle(.orange)
            }
        }
        .padding(.vertical, 4)
    }
}

#Preview {
    ContentView()
}
