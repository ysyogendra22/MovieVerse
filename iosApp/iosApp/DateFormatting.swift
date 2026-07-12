import Foundation

extension String {
    /// Movie.releaseDate is TVMaze's raw "yyyy-MM-dd" (or "Unknown" when TVMaze has no
    /// premiere date). Falls back to the original string for anything unparseable
    /// rather than showing garbage.
    func toReadableDate() -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        inputFormatter.locale = Locale(identifier: "en_US_POSIX")

        guard let date = inputFormatter.date(from: self) else { return self }

        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "MMM d, yyyy"
        return outputFormatter.string(from: date)
    }
}
