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
    dropTables();
    createTables();

    // Insert genres
    String[] genres = { "Rock", "Pop", "Jazz", "Classical", "Hip Hop" };
    for (String genre : genres) {
      insertGenre(genre);
    }

    // Insert artists
    String[] artists = {
        "Queen", "Michael Jackson", "Miles Davis", "Mozart", "Eminem",
        "Led Zeppelin", "Madonna", "John Coltrane", "Beethoven", "Tupac",
        "Pink Floyd", "Prince", "Duke Ellington", "Bach", "Dr. Dre",
        "Rolling Stones", "ABBA", "Charlie Parker", "Tchaikovsky", "Kendrick Lamar"
    };
    for (String artist : artists) {
      insertArtist(artist);
    }

    // Insert albums
    String[][] albums = {
        { "A Night at the Opera", "1" },
        { "Thriller", "2" },
        { "Kind of Blue", "3" },
        { "Symphony No. 40", "4" },
        { "The Marshall Mathers LP", "5" },
        { "Led Zeppelin IV", "6" },
        { "Like a Prayer", "7" },
        { "A Love Supreme", "8" },
        { "Symphony No. 5", "9" },
        { "All Eyez on Me", "10" },
        { "The Dark Side of the Moon", "11" },
        { "Purple Rain", "12" },
        { "Ellington at Newport", "13" },
        { "The Well-Tempered Clavier", "14" },
        { "The Chronic", "15" }
    };
    for (String[] album : albums) {
      insertAlbum(album);
    }

    // Insert songs and song positions
    Object[][] songs = {
        // Rock songs
        { "Bohemian Rhapsody", LocalDate.of(1975, 11, 21), 1, 1, 1, 1 },
        { "Stairway to Heaven", LocalDate.of(1971, 11, 8), 1, 6, 6, 1 },
        { "Comfortably Numb", LocalDate.of(1979, 11, 30), 1, 11, 11, 1 },
        { "Start Me Up", LocalDate.of(1981, 8, 14), 1, 11, 11, 2 }, // Changed album_id from 16 to 11

        // Pop songs
        { "Thriller", LocalDate.of(1982, 11, 30), 2, 2, 2, 1 },
        { "Like a Prayer", LocalDate.of(1989, 3, 3), 2, 7, 7, 1 },
        { "Purple Rain", LocalDate.of(1984, 6, 25), 2, 12, 12, 1 },
        { "Dancing Queen", LocalDate.of(1976, 8, 15), 2, 12, 12, 2 }, // Changed album_id from 17 to 12

        // Jazz songs
        { "So What", LocalDate.of(1959, 8, 17), 3, 3, 3, 1 },
        { "A Love Supreme", LocalDate.of(1964, 12, 9), 3, 8, 8, 1 },
        { "Take the A Train", LocalDate.of(1941, 2, 15), 3, 13, 13, 1 },
        { "Now's the Time", LocalDate.of(1945, 11, 26), 3, 13, 13, 2 }, // Changed album_id from 18 to 13

        // Classical songs
        { "Symphony No. 40 in G minor", LocalDate.of(1788, 7, 25), 4, 4, 4, 1 },
        { "Symphony No. 5", LocalDate.of(1808, 12, 22), 4, 9, 9, 1 },
        { "Air on G String", LocalDate.of(1717, 1, 1), 4, 14, 14, 1 },
        { "The Nutcracker Suite", LocalDate.of(1892, 12, 18), 4, 14, 14, 2 }, // Changed album_id from 19 to 14

        // Hip Hop songs
        { "The Real Slim Shady", LocalDate.of(2000, 5, 16), 5, 5, 5, 1 },
        { "California Love", LocalDate.of(1995, 12, 3), 5, 10, 10, 1 },
        { "Nuthin' but a G Thang", LocalDate.of(1992, 11, 19), 5, 15, 15, 1 },
        { "Alright", LocalDate.of(2015, 3, 15), 5, 15, 15, 2 } // Changed album_id from 20 to 15
    };
    for (Object[] song : songs) {
      insertSong(song);
    }
  }

  private void dropTables() throws SQLException {
    String[] dropStatements = {
        "DROP TABLE IF EXISTS song_position CASCADE",
        "DROP TABLE IF EXISTS songs CASCADE",
        "DROP TABLE IF EXISTS albums CASCADE",
        "DROP TABLE IF EXISTS artists CASCADE",
        "DROP TABLE IF EXISTS genres CASCADE"
    };

    for (String sql : dropStatements) {
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.executeUpdate();
      }
    }
  }

  private void createTables() throws SQLException {
    String[] createStatements = {
        """
            CREATE TABLE genres (
                genre_id SERIAL PRIMARY KEY,
                genre_name VARCHAR(50) UNIQUE NOT NULL
            )
            """,
        """
            CREATE TABLE artists (
                artist_id SERIAL PRIMARY KEY,
                artist_name VARCHAR(100) UNIQUE NOT NULL
            )
            """,
        """
            CREATE TABLE albums (
                album_id SERIAL PRIMARY KEY,
                album_name VARCHAR(100) NOT NULL
            )
            """,
        """
            CREATE TABLE songs (
                song_id SERIAL PRIMARY KEY,
                song_title VARCHAR(200) NOT NULL,
                song_date DATE NOT NULL,
                genre_id INTEGER REFERENCES genres(genre_id),
                artist_id INTEGER REFERENCES artists(artist_id)
            )
            """,
        """
            CREATE TABLE song_position (
                track_number INTEGER NOT NULL,
                album_id INTEGER REFERENCES albums(album_id),
                song_id INTEGER REFERENCES songs(song_id),
                PRIMARY KEY (album_id, track_number)
            )
            """
    };

    for (String sql : createStatements) {
      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.executeUpdate();
      }
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

  private void insertAlbum(String[] albumData) throws SQLException {
    String sql = "INSERT INTO albums (album_id, album_name) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, Integer.parseInt(albumData[1]));
      stmt.setString(2, albumData[0]);
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