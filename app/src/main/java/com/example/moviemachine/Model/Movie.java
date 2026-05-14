package com.example.moviemachine.Model;

public class Movie {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private double voteAverage;

    // Yeni eklediğimiz özellikler
    private String imdbId;
    private String mediaType = "movie"; // Varsayılan olarak hep film sayıyoruz

    // Constructor (Yapıcı Metod)
    public Movie(int id, String title, String overview, String posterPath, double voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
    }

    // --- GETTER VE SETTER METOTLARI ---

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public double getVoteAverage() { return voteAverage; }

    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
}