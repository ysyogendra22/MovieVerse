import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = MovieListViewModel()

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 0) {
                Text(viewModel.greeting)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .padding()

                if viewModel.isLoading {
                    Spacer()
                    ProgressView()
                    Spacer()
                } else {
                    List(viewModel.movies, id: \.id) { movie in
                        NavigationLink(value: movie.id) {
                            MovieRow(movie: movie)
                        }
                    }
                    .listStyle(.plain)
                }
            }
            .navigationTitle("MovieVerse")
            .navigationDestination(for: Int32.self) { movieId in
                if let movie = viewModel.movies.first(where: { $0.id == movieId }) {
                    MovieDetailView(movie: movie)
                }
            }
        }
        .onAppear { viewModel.load() }
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
