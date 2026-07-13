import Foundation
import shared

enum MovieDetailUiState {
    case loading
    case success(Movie)
    case error(String)
}

@MainActor
final class MovieDetailViewModel: ObservableObject {
    @Published var uiState: MovieDetailUiState = .loading

    private let movieId: Int32
    private let repository: MovieRepository = Injector.shared.movieRepository()

    init(movieId: Int32) {
        self.movieId = movieId
    }

    func load() {
        uiState = .loading

        repository.getMovieDetail(id: movieId) { [weak self] movie, error in
            guard let self else { return }
            Task { @MainActor in
                if let error {
                    self.uiState = .error(Self.message(for: error))
                } else if let movie {
                    self.uiState = .success(movie)
                }
            }
        }
    }

    private static func message(for error: Error) -> String {
        (error as? MovieError)?.toUserMessage()
            ?? "Something unexpected happened. Please try again."
    }
}
