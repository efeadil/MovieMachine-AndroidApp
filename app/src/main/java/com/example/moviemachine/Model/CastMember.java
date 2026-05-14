package com.example.moviemachine.Model;

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