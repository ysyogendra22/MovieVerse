import Foundation
import shared

@MainActor
final class MovieListViewModel: ObservableObject {
    @Published var greeting: String = ""
    @Published var movies: [Movie] = []
    @Published var isLoading: Bool = true

    private let repository: MovieRepository = Injector.shared.movieRepository()

    func load() {
        greeting = Greeting().greet()

        repository.getMovies { [weak self] movies, error in
            guard let self else { return }
            Task { @MainActor in
                self.movies = movies ?? []
                self.isLoading = false
            }
        }
    }
}
