package aor.models;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
  private int id;
  private String name;
  private int genreId;
  private List<Song> songs;

  public Playlist() {
    this.songs = new ArrayList<>();
  }

  public Playlist(String name, int genreId) {
    this.name = name;
    this.genreId = genreId;
    this.songs = new ArrayList<>();
  }

  // Getters and Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getGenreId() {
    return genreId;
  }

  public void setGenreId(int genreId) {
    this.genreId = genreId;
  }

  public List<Song> getSongs() {
    return songs;
  }

  public void setSongs(List<Song> songs) {
    this.songs = songs;
  }

  public void addSong(Song song) {
    this.songs.add(song);
  }

  @Override
  public String toString() {
    return "Playlist{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", genreId=" + genreId +
        ", songs=" + songs +
        '}';
  }
}