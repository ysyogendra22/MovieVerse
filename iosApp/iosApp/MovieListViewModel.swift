import Foundation
import shared

// Mirrors shared's MovieListUiState shape as a native Swift enum rather than trying
// to switch on the Kotlin sealed interface directly from Swift — keeps this idiomatic
// and avoids relying on Kotlin/Native's sealed-class Swift interop conventions.
enum MovieListUiState {
    case loading
    case success([Movie])
    case error(String)
    case empty
}

@MainActor
final class MovieListViewModel: ObservableObject {
    @Published var uiState: MovieListUiState = .loading

    private let repository: MovieRepository = Injector.shared.movieRepository()

    func load() {
        uiState = .loading

        repository.getMovies { [weak self] movies, error in
            guard let self else { return }
            Task { @MainActor in
                if let error {
                    self.uiState = .error(Self.message(for: error))
                } else if let movies, !movies.isEmpty {
                    self.uiState = .success(movies)
                } else {
                    self.uiState = .empty
                }
            }
        }
    }

    private static func message(for error: Error) -> String {
        (error as? MovieError)?.toUserMessage()
            ?? "Something unexpected happened. Please try again."
    }
}
