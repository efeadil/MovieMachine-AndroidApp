package com.example.moviemachine.Model;

/**
 * Bu sınıf, bir filmdeki oyuncu bilgilerini tutan model sınıfıdır.
 * Oyuncunun ismini ve profil fotoğrafının yolunu saklar.
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