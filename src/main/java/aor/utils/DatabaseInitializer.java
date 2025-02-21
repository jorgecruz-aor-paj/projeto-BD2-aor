package aor.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class DatabaseInitializer {
  private final Connection connection;

  public DatabaseInitializer(Connection connection) {
    this.connection = connection;
  }

  public void populateDatabase() throws SQLException {
    // Insert genres
    String[] genres = { "Rock", "Pop", "Jazz", "Classical", "Hip Hop" };
    for (String genre : genres) {
      insertGenre(genre);
    }

    // Insert artists
    String[] artists = { "Queen", "Michael Jackson", "Miles Davis", "Mozart", "Eminem" };
    for (String artist : artists) {
      insertArtist(artist);
    }

    // Insert albums
    String[][] albums = {
        { "A Night at the Opera", "1" },
        { "Thriller", "2" },
        { "Kind of Blue", "3" },
        { "Symphony No. 40", "4" },
        { "The Marshall Mathers LP", "5" }
    };
    for (String[] album : albums) {
      insertAlbum(album[0]);
    }

    // Insert songs and song positions
    Object[][] songs = {
        { "Bohemian Rhapsody", LocalDate.of(1975, 11, 21), 1, 1, 1, 1 },
        { "Thriller", LocalDate.of(1982, 11, 30), 2, 2, 2, 1 },
        { "So What", LocalDate.of(1959, 8, 17), 3, 3, 3, 1 },
        { "Symphony No. 40 in G minor", LocalDate.of(1788, 7, 25), 4, 4, 4, 1 },
        { "The Real Slim Shady", LocalDate.of(2000, 5, 16), 5, 5, 5, 1 }
    };
    for (Object[] song : songs) {
      insertSong(song);
    }
  }

  private void insertGenre(String genreName) throws SQLException {
    String sql = "INSERT INTO genres (genre_name) VALUES (?) ON CONFLICT (genre_name) DO NOTHING";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, genreName);
      stmt.executeUpdate();
    }
  }

  private void insertArtist(String artistName) throws SQLException {
    String sql = "INSERT INTO artists (artist_name) VALUES (?) ON CONFLICT (artist_name) DO NOTHING";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, artistName);
      stmt.executeUpdate();
    }
  }

  private void insertAlbum(String albumName) throws SQLException {
    String sql = "INSERT INTO albums (album_name) VALUES (?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, albumName);
      stmt.executeUpdate();
    }
  }

  private void insertSong(Object[] songData) throws SQLException {
    String sql = "INSERT INTO songs (song_title, song_date, genre_id, artist_id) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, (String) songData[0]);
      stmt.setDate(2, Date.valueOf((LocalDate) songData[1]));
      stmt.setInt(3, (Integer) songData[2]);
      stmt.setInt(4, (Integer) songData[3]);
      stmt.executeUpdate();

      // Insert song position if album info is provided
      try (var rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          int songId = rs.getInt(1);
          String positionSql = "INSERT INTO song_position (track_number, album_id, song_id) VALUES (?, ?, ?)";
          try (PreparedStatement posStmt = connection.prepareStatement(positionSql)) {
            posStmt.setInt(1, (Integer) songData[5]);
            posStmt.setInt(2, (Integer) songData[4]);
            posStmt.setInt(3, songId);
            posStmt.executeUpdate();
          }
        }
      }
    }
  }
}