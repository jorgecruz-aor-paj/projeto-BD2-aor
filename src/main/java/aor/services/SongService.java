package aor.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import aor.dao.SongDAO;
import aor.models.Song;

public class SongService {
  private final SongDAO songDAO;

  public SongService(Connection connection) {
    this.songDAO = new SongDAO(connection);
  }

  public void addSong(Song song) throws SQLException {
    songDAO.addSong(song);
  }

  public void updateSongTitle(int songId, String newTitle) throws SQLException {
    songDAO.updateSongTitle(songId, newTitle);
  }

  public void deleteSong(int songId) throws SQLException {
    songDAO.deleteSong(songId);
  }

  public List<Song> getAllSongs() throws SQLException {
    return songDAO.getAllSongs();
  }
}