import SwiftUI
import shared

struct MovieDetailView: View {
    @StateObject private var viewModel: MovieDetailViewModel
    let movieTitle: String

    init(movieId: Int32, movieTitle: String) {
        _viewModel = StateObject(wrappedValue: MovieDetailViewModel(movieId: movieId))
        self.movieTitle = movieTitle
    }

    var body: some View {
        content
            // Shown immediately from the nav argument, not just once the detail fetch
            // succeeds — otherwise Loading/Error states show a bare back arrow with no
            // title.
            .navigationTitle(movieTitle)
            .navigationBarTitleDisplayMode(.inline)
            .onAppear { viewModel.load() }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.uiState {
        case .loading:
            ProgressView()

        case .error(let message):
            VStack(spacing: 12) {
                Text(message)
                    .multilineTextAlignment(.center)
                    .foregroundStyle(.secondary)
                Button("Retry") { viewModel.load() }
            }
            .padding()

        case .success(let movie):
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    AsyncImage(url: URL(string: movie.posterUrl)) { image in
                        image.resizable().aspectRatio(contentMode: .fill)
                    } placeholder: {
                        Color.secondary.opacity(0.2)
                    }
                    .frame(height: 320)
                    .clipped()

                    Text("★ \(String(format: "%.1f", movie.rating))  ·  \(movie.releaseDate.toReadableDate())")
                        .font(.title3)
                        .foregroundStyle(.orange)
                        .padding(.horizontal)

                    if !movie.genres.isEmpty {
                        Text(movie.genres.joined(separator: ", "))
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .padding(.horizontal)
                    }

                    Text(movie.overview)
                        .font(.body)
                        .padding(.horizontal)
                }
            }
        }
    }
}
