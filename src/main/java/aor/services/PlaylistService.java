package aor.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import aor.dao.PlaylistDAO;
import aor.models.Playlist;
import aor.models.Song;

public class PlaylistService {
  private final PlaylistDAO playlistDAO;

  public PlaylistService(Connection connection) {
    this.playlistDAO = new PlaylistDAO(connection);
  }

  public List<Song> createRandomPlaylist(int genreId, int numberOfSongs) throws SQLException {
    return playlistDAO.getRandomPlaylist(genreId, numberOfSongs);
  }

  public void savePlaylist(Playlist playlist) throws SQLException {
    playlistDAO.createPlaylist(playlist);
  }

  public void removePlaylist(int playlistId) throws SQLException {
    playlistDAO.deletePlaylist(playlistId);
  }
}