package aor.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import aor.config.DatabaseConfig;
import aor.models.Song;

public class SongDAO {
  private final Connection connection;

  public SongDAO(Connection connection) {
    this.connection = connection;
  }

  public void addSong(Song song) throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.INSERT_SONG,
        Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, song.getTitle());
      stmt.setDate(2, Date.valueOf(song.getDate()));
      stmt.setInt(3, song.getGenreId());
      stmt.setInt(4, song.getArtistId());

      stmt.executeUpdate();

      // If song is part of an album, add to song_position
      if (song.getAlbumId() != null && song.getTrackNumber() != null) {
        String sql = "INSERT INTO song_position (track_number, album_id, song_id) VALUES (?, ?, ?)";
        try (ResultSet rs = stmt.getGeneratedKeys()) {
          if (rs.next()) {
            int songId = rs.getInt(1);
            try (PreparedStatement posStmt = connection.prepareStatement(sql)) {
              posStmt.setInt(1, song.getTrackNumber());
              posStmt.setInt(2, song.getAlbumId());
              posStmt.setInt(3, songId);
              posStmt.executeUpdate();
            }
          }
        }
      }
    }
  }

  public void updateSongTitle(int songId, String newTitle) throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.UPDATE_SONG_TITLE)) {
      stmt.setString(1, newTitle);
      stmt.setInt(2, songId);
      stmt.executeUpdate();
    }
  }

  public void deleteSong(int songId) throws SQLException {
    // First, check if song is in an album
    String checkAlbumSql = "SELECT album_id FROM song_position WHERE song_id = ?";
    Integer albumId = null;

    try (PreparedStatement checkStmt = connection.prepareStatement(checkAlbumSql)) {
      checkStmt.setInt(1, songId);
      try (ResultSet rs = checkStmt.executeQuery()) {
        if (rs.next()) {
          albumId = rs.getInt("album_id");
        }
      }
    }

    // Delete from song_position first (if exists)
    String deletePositionSql = "DELETE FROM song_position WHERE song_id = ?";
    try (PreparedStatement posStmt = connection.prepareStatement(deletePositionSql)) {
      posStmt.setInt(1, songId);
      posStmt.executeUpdate();
    }

    // Delete the song
    try (PreparedStatement songStmt = connection.prepareStatement(DatabaseConfig.DELETE_SONG)) {
      songStmt.setInt(1, songId);
      songStmt.executeUpdate();
    }

    // If song was in album, check if album is now empty
    if (albumId != null) {
      String checkEmptyAlbumSql = "SELECT COUNT(*) FROM song_position WHERE album_id = ?";
      try (PreparedStatement checkEmptyStmt = connection.prepareStatement(checkEmptyAlbumSql)) {
        checkEmptyStmt.setInt(1, albumId);
        try (ResultSet rs = checkEmptyStmt.executeQuery()) {
          if (rs.next() && rs.getInt(1) == 0) {
            // Album is empty, delete it
            String deleteAlbumSql = "DELETE FROM albums WHERE album_id = ?";
            try (PreparedStatement albumStmt = connection.prepareStatement(deleteAlbumSql)) {
              albumStmt.setInt(1, albumId);
              albumStmt.executeUpdate();
            }
          }
        }
      }
    }
  }

  public List<Song> getAllSongs() throws SQLException {
    List<Song> songs = new ArrayList<>();
    try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.GET_SONG_DETAILS);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        Song song = new Song();
        song.setId(rs.getInt("song_id"));
        song.setTitle(rs.getString("song_title"));
        song.setDate(rs.getDate("song_date").toLocalDate());
        song.setGenreName(rs.getString("genre_name"));
        song.setArtistName(rs.getString("artist_name"));

        // Handle nullable album fields
        String albumName = rs.getString("album_name");
        if (albumName != null) {
          song.setAlbumName(albumName);
          song.setTrackNumber(rs.getInt("track_number"));
        }

        songs.add(song);
      }
    }
    return songs;
  }
}