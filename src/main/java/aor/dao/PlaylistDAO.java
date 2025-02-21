package aor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import aor.config.DatabaseConfig;
import aor.models.Playlist;
import aor.models.Song;

public class PlaylistDAO {
  private final Connection connection;

  public PlaylistDAO(Connection connection) {
    this.connection = connection;
  }

  public List<Song> getRandomPlaylist(int genreId, int limit) throws SQLException {
    List<Song> playlist = new ArrayList<>();

    try (PreparedStatement stmt = connection.prepareStatement(DatabaseConfig.GET_RANDOM_PLAYLIST)) {
      stmt.setInt(1, genreId);
      stmt.setInt(2, limit);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Song song = new Song();
          song.setId(rs.getInt("song_id"));
          song.setTitle(rs.getString("song_title"));
          song.setArtistName(rs.getString("artist_name"));
          song.setAlbumName(rs.getString("album_name"));
          playlist.add(song);
        }
      }
    }
    return playlist;
  }

  public void createPlaylist(Playlist playlist) throws SQLException {
    String sql = "INSERT INTO playlists (playlist_name, genre_id) VALUES (?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, playlist.getName());
      stmt.setInt(2, playlist.getGenreId());
      stmt.executeUpdate();
    }
  }

  public void deletePlaylist(int playlistId) throws SQLException {
    String sql = "DELETE FROM playlists WHERE playlist_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, playlistId);
      stmt.executeUpdate();
    }
  }
}