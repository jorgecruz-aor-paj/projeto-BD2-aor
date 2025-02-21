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
        System.out.println("\n=== Spoty-AOR ===");
        System.out.println("1. Adicionar música");
        System.out.println("2. Mudar titulo de música");
        System.out.println("3. Apagar música");
        System.out.println("4. Listar todas as músicas");
        System.out.println("5. Criar uma playlist aleatória");
        System.out.println("6. Iniciar a base de dados");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void processChoice(int choice) throws SQLException {
        switch (choice) {
            case 1 -> addNewSong();
            case 2 -> updateSongTitle();
            case 3 -> deleteSong();
            case 4 -> viewAllSongs();
            case 5 -> createRandomPlaylist();
            case 6 -> initializeDatabase();
            case 0 -> System.out.println("Obrigada por ouvir o Spotify-AOR! Até à próxima");
            default -> System.out.println("Escolha inválida. Selecione uma opção válida.");
        }
    }

    private void initializeDatabase() throws SQLException {
        System.out.println("\n=== Iniciando a Base de Dados ===");
        DatabaseInitializer initializer = new DatabaseInitializer(conn);
        initializer.populateDatabase();
        System.out.println("Base de dados iniciada!");
    }

    private void addNewSong() throws SQLException {
        System.out.println("\n=== Adicionar Música ===");
        System.out.print("Nome da música a adicionar: ");
        String title = scanner.nextLine();

        displayGenres();
        System.out.print("Selecione o estilo músical: ");
        int genreId = Integer.parseInt(scanner.nextLine());

        displayArtists();
        System.out.print("Selecione o autor da música: ");
        int artistId = Integer.parseInt(scanner.nextLine());

        Song song = new Song(title, LocalDate.now(), genreId, artistId);

        System.out.print("Pretende que esta música faça parte de um albúm? (s/n): ");
        if (scanner.nextLine().toLowerCase().equals("s")) {
            displayAlbums();
            System.out.print("Selecione o nr. do album que deseja adicionar: ");
            int albumId = Integer.parseInt(scanner.nextLine());
            song.setAlbumId(albumId);

            // Automatically get the next available track number
            String trackSql = "SELECT COALESCE(MAX(track_number), 0) + 1 AS next_track FROM song_position WHERE album_id = ?";
            try (var trackStmt = conn.prepareStatement(trackSql)) {
                trackStmt.setInt(1, albumId);
                try (var rs = trackStmt.executeQuery()) {
                    if (rs.next()) {
                        song.setTrackNumber(rs.getInt("Proxima_faixa"));
                    }
                }
            }
        }

        songService.addSong(song);
        System.out.println("Música adicionada com sucesso");
    }

    private void updateSongTitle() throws SQLException {
        System.out.println("\n=== Mudar Titulo de Música ===");
        displayAvailableSongs();

        System.out.print("Escolha a música que prentende alterar: ");
        int songId = Integer.parseInt(scanner.nextLine());

        System.out.print("Escreva um novo título: ");
        String newTitle = scanner.nextLine();

        songService.updateSongTitle(songId, newTitle);
        System.out.println("Nome da música alterado com sucesso!");
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
            System.out.println("\nMúsicas Disponíveis:");
            while (rs.next()) {
                System.out.printf("%d. %s (by %s)%n",
                        rs.getInt("song_id"),
                        rs.getString("song_title"),
                        rs.getString("artist_name"));
            }
        }
    }

    private void deleteSong() throws SQLException {
        System.out.println("\n=== Apagar Música ===");
        displayAvailableSongs();

        System.out.print("Escolha a música que prentende eliminar :");
        int songId = Integer.parseInt(scanner.nextLine());

        songService.deleteSong(songId);
        System.out.println("Música removida com sucesso");
    }

    private void viewAllSongs() throws SQLException {
        System.out.println("\n=== Todas as músicas===");
        List<Song> songs = songService.getAllSongs();
        System.out.printf("%-30s %-20s %-20s %-30s\n", "Título", "Género Musical", "Autor", "Álbum");
        for (Song song : songs) {
            System.out.println(song);
        }
    }

    private void createRandomPlaylist() throws SQLException {
        System.out.println("\n=== Criar uma nova Playlist ===");

        displayGenres();
        System.out.print("Selecione o género para a sua playlist: ");
        int genreId = Integer.parseInt(scanner.nextLine());

        System.out.print("Quantas músicas pretende adicionar: ");
        int numberOfSongs = Integer.parseInt(scanner.nextLine());

        List<Song> playlist = playlistService.createRandomPlaylist(genreId, numberOfSongs);
        System.out.println("\nPlaylist gerada:");
        System.out.printf("%-30s %-20s %-30s\n", "Título", "Autor", "Álbum");
        for (Song song : playlist) {
            System.out.printf("%-30s %-20s %-30s\n",
                    song.getTitle(),
                    song.getArtistName(),
                    song.getAlbumName() != null ? song.getAlbumName() : "---");
        }
    }

    private void displayGenres() throws SQLException {
        String sql = "SELECT genre_id, genre_name FROM genres ORDER BY genre_id";
        try (var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {
            System.out.println("\nGéneros disponíveis:");
            while (rs.next()) {
                System.out.printf("%d. %s%n", rs.getInt("genre_id"), rs.getString("genre_name"));
            }
        }
    }

    private void displayArtists() throws SQLException {
        String sql = "SELECT artist_id, artist_name FROM artists ORDER BY artist_id";
        try (var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {
            System.out.println("\nArtistas Disponíveis:");
            while (rs.next()) {
                System.out.printf("%d. %s%n", rs.getInt("artist_id"), rs.getString("artist_name"));
            }
        }
    }

    private void displayAlbums() throws SQLException {
        String sql = "SELECT album_id, album_name FROM albums ORDER BY album_id";
        try (var stmt = conn.prepareStatement(sql);
                var rs = stmt.executeQuery()) {
            System.out.println("\nAlbuns Disponíveis:");
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
                System.out.println("Introduza um número válido.");
                choice = -1;
            } catch (SQLException e) {
                System.out.println("ERRO AO ACEDER À BASE DE DADOS: " + e.getMessage());
                choice = -1;
            }
        } while (choice != 0);
    }

    public static void main(String[] args) {
        try (App app = new App()) {
            app.run();
        } catch (SQLException e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }
}