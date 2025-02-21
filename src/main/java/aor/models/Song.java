package aor.models;

import java.time.LocalDate;

public class Song {
    private Integer id;
    private String title;
    private LocalDate date;
    private Integer genreId;
    private String genreName;
    private Integer artistId;
    private String artistName;
    private Integer albumId;
    private String albumName;
    private Integer trackNumber;

    // Default constructor
    public Song() {
    }

    // Constructor with required fields
    public Song(String title, LocalDate date, Integer genreId, Integer artistId) {
        this.title = title;
        this.date = date;
        this.genreId = genreId;
        this.artistId = artistId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    @Override
    public String toString() {
        return String.format(" %-30s %-20s %-20s %-30s", this.getTitle(), this.getGenreName(), this.getArtistName(),
                this.getAlbumName() != null ? this.getAlbumName() : "---");
    }
}