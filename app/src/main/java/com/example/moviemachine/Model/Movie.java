package com.example.moviemachine.Model;

/**
 * Model Class (MVC - M)
 * EN: Represents a movie or TV show object containing basic info from TMDB.
 * TR: Bir film veya diziye ait verileri tutan model sınıfıdır.
 */
public class Movie {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private double voteAverage;
    private String mediaType = "movie";

    public Movie(int id, String title, String overview, String posterPath, double voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
    }

    // --- GETTER AND SETTER METHODS ---
    // EN: Encapsulation is used to access private fields safely.
    // TR: Verilere güvenli erişim için kapsülleme (encapsulation) kullanılmıştır.

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public double getVoteAverage() { return voteAverage; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
}
