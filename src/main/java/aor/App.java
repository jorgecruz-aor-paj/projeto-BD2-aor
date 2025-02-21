package aor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import aor.models.Song;
import aor.services.PlaylistService;
import aor.services.SongService;
import aor.utils.DatabaseInitializer;
import aor.utils.DatabaseUtils;

public class App implements AutoCloseable {
    private final Connection conn;
    private final SongService songService;
    private final PlaylistService playlistService;
    private final Scanner scanner;

    public App() throws SQLException {
        this.conn = DatabaseUtils.getConnection();
        this.songService = new SongService(conn);
        this.playlistService = new PlaylistService(conn);
        this.scanner = new Scanner(System.in);
    }

    private void showMenu() {
        System.out.println("\n=== Music Database Management System ===");
        System.out.println("1. Add new song");
        System.out.println("2. Update song title");
        System.out.println("3. Delete song");
        System.out.println("4. View all songs");
        System.out.println("5. Create random playlist");
        System.out.println("6. Initialize database with sample data");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private void processChoice(int choice) throws SQLException {
        switch (choice) {
            case 1 -> addNewSong();
            case 2 -> updateSongTitle();
            case 3 -> deleteSong();
            case 4 -> viewAllSongs();
            case 5 -> createRandomPlaylist();
            case 6 -> initializeDatabase();
            case 0 -> System.out.println("Goodbye!");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void initializeDatabase() throws SQLException {
        System.out.println("\n=== Initializing Database ===");
        DatabaseInitializer initializer = new DatabaseInitializer(conn);
        initializer.populateDatabase();
        System.out.println("Database initialized with sample data!");
    }

    private void addNewSong() throws SQLException {
        System.out.println("\n=== Add New Song ===");
        System.out.print("Enter song title: ");
        String title = scanner.nextLine();

        displayGenres();
        System.out.print("Select genre number: ");
        int genreId = Integer.parseInt(scanner.nextLine());

        displayArtists();
        System.out.print("Select artist number: ");
        int artistId = Integer.parseInt(scanner.nextLine());

        Song song = new Song(title, LocalDate.now(), genreId, artistId);

        System.out.print("Is this song part of an album? (y/n): ");
        if (scanner.nextLine().toLowerCase().equals("y")) {
            displayAlbums();
            System.out.print("Select album number: ");
            int albumId = Integer.parseInt(scanner.nextLine());
            song.setAlbumId(albumId);

            // Automatically get the next available track number
            String trackSql = "SELECT COALESCE(MAX(track_number), 0) + 1 AS next_track FROM song_position WHERE album_id = ?";
            try (var trackStmt = conn.prepareStatement(trackSql)) {
                trackStmt.setInt(1, albumId);
                try (var rs = trackStmt.executeQuery()) {
                    if (rs.next()) {
                        song.setTrackNumber(rs.getInt("next_track"));
                    }
                }
            }
        }

        songService.addSong(song);
        System.out.println("Song added successfully!");
    }

    private void updateSongTitle() throws SQLException {
        System.out.println("\n=== Update Song Title ===");
        displayAvailableSongs();

        System.out.print("Select song number: ");
        int songId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter new title: ");
        String newTitle = scanner.nextLine();

        songService.updateSongTitle(songId, newTitle);
        System.out.println("Song title updated successfully!");
    }

    private void displayAvailableSongs() throws SQLException {
        String sql = """
                SELECT s.song_id, s.song_title, a.artist_name
                FROM songs s
                JOIN artists a ON s.artist_id = a.artist_id
                ORDER BY s.song_id
                """;
        try (var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {
            System.out.println("\nAvailable Songs:");
            while (rs.next()) {
                System.out.printf("%d. %s (by %s)%n",
                        rs.getInt("song_id"),
                        rs.getString("song_title"),
                        rs.getString("artist_name"));
            }
        }
    }

    private void deleteSong() throws SQLException {
        System.out.println("\n=== Delete Song ===");
        displayAvailableSongs();

        System.out.print("Select song number: ");
        int songId = Integer.parseInt(scanner.nextLine());

        songService.deleteSong(songId);
        System.out.println("Song deleted successfully!");
    }

    private void viewAllSongs() throws SQLException {
        System.out.println("\n=== All Songs ===");
        List<Song> songs = songService.getAllSongs();
        for (Song song : songs) {
            System.out.println(song);
        }
    }

    private void createRandomPlaylist() throws SQLException {
        System.out.println("\n=== Create Random Playlist ===");

        displayGenres();
        System.out.print("Select genre number: ");
        int genreId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter number of songs: ");
        int numberOfSongs = Integer.parseInt(scanner.nextLine());

        List<Song> playlist = playlistService.createRandomPlaylist(genreId, numberOfSongs);
        System.out.println("\nGenerated Playlist:");
        for (Song song : playlist) {
            System.out.println(song);
        }
    }

    private void displayGenres() throws SQLException {
        String sql = "SELECT genre_id, genre_name FROM genres ORDER BY genre_id";
        try (var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {
            System.out.println("\nAvailable Genres:");
            while (rs.next()) {
                System.out.printf("%d. %s%n", rs.getInt("genre_id"), rs.getString("genre_name"));
            }
        }
    }

    private void displayArtists() throws SQLException {
        String sql = "SELECT artist_id, artist_name FROM artists ORDER BY artist_id";
        try (var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {
            System.out.println("\nAvailable Artists:");
            while (rs.next()) {
                System.out.printf("%d. %s%n", rs.getInt("artist_id"), rs.getString("artist_name"));
            }
        }
    }

    private void displayAlbums() throws SQLException {
        String sql = "SELECT album_id, album_name FROM albums ORDER BY album_id";
        try (var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {
            System.out.println("\nAvailable Albums:");
            while (rs.next()) {
                System.out.printf("%d. %s%n", rs.getInt("album_id"), rs.getString("album_name"));
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (scanner != null) {
            scanner.close();
        }
        DatabaseUtils.closeConnection();
    }

    public void run() throws SQLException {
        int choice;
        do {
            showMenu();
            try {
                choice = Integer.parseInt(scanner.nextLine());
                processChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                choice = -1;
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                choice = -1;
            }
        } while (choice != 0);
    }

    public static void main(String[] args) {
        try (App app = new App()) {
            app.run();
        } catch (SQLException e) {
            System.err.println("Fatal error: " + e.getMessage());
        }
    }
}