import SwiftUI
import shared

struct MovieDetailView: View {
    let movie: Movie

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                AsyncImage(url: URL(string: movie.posterUrl)) { image in
                    image.resizable().aspectRatio(contentMode: .fill)
                } placeholder: {
                    Color.secondary.opacity(0.2)
                }
                .frame(height: 320)
                .clipped()

                Text("★ \(String(format: "%.1f", movie.rating))")
                    .font(.title3)
                    .foregroundStyle(.orange)
                    .padding(.horizontal)

                Text(movie.overview)
                    .font(.body)
                    .padding(.horizontal)
            }
        }
        .navigationTitle(movie.title)
        .navigationBarTitleDisplayMode(.inline)
    }
}
