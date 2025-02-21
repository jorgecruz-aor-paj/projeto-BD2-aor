package aor.config;

public class DatabaseConfig {
  // Database connection constants
  private static final String URL = "jdbc:postgresql://localhost:5432/music_db";
  private static final String USER = "postgres";
  private static final String PASSWORD = "postgres";

  // Getter methods for database properties
  public static String getUrl() {
    return URL;
  }

  public static String getUser() {
    return USER;
  }

  public static String getPassword() {
    return PASSWORD;
  }

  // SQL queries as constants
  public static final String INSERT_SONG = "INSERT INTO songs (song_title, song_date, genre_id, artist_id) VALUES (?, ?, ?, ?)";

  public static final String UPDATE_SONG_TITLE = "UPDATE songs SET song_title = ? WHERE song_id = ?";

  public static final String DELETE_SONG = "DELETE FROM songs WHERE song_id = ?";

  public static final String GET_SONG_DETAILS = "SELECT s.song_id, s.song_title, s.song_date, " +
      "g.genre_name, a.artist_name, al.album_name, sp.track_number " +
      "FROM songs s " +
      "JOIN genres g ON s.genre_id = g.genre_id " +
      "JOIN artists a ON s.artist_id = a.artist_id " +
      "LEFT JOIN song_position sp ON s.song_id = sp.song_id " +
      "LEFT JOIN albums al ON sp.album_id = al.album_id";

  public static final String GET_RANDOM_PLAYLIST = "SELECT s.song_id, s.song_title, a.artist_name " +
      "FROM songs s " +
      "JOIN artists a ON s.artist_id = a.artist_id " +
      "WHERE s.genre_id = ? " +
      "ORDER BY RANDOM() " +
      "LIMIT ?";
}