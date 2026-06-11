package com.example.moviemachine.Model;

/**
 * Model Class (MVC - M)
 * EN: Represents a cast member in a movie, holding their name and profile image path.
 * TR: Bir filmdeki oyuncu bilgilerini tutan model sınıfıdır.
 */
public class CastMember {
    private String name;
    private String profilePath;

    public CastMember(String name, String profilePath) {
        this.name = name;
        this.profilePath = profilePath;
    }

    public String getName() { return name; }
    public String getProfilePath() { return profilePath; }
}
