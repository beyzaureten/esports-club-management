package com.esportsclub.model;

public class Game {

    private int id;
    private String name;
    private String genre; // "FPS", "MOBA", "RPG" vb.
    private String mode;  // "5v5", "1v1" vb.

    public Game() {}

    public Game(int id, String name, String genre, String mode) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.mode = mode;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getGenre() { return genre; }
    public String getMode() { return mode; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setMode(String mode) { this.mode = mode; }

    @Override
    public String toString() {
        return "Game{id=" + id + ", name='" + name + "', genre='" + genre + "', mode='" + mode + "'}";
    }
}