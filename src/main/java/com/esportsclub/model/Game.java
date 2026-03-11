package com.esportsclub.model;

public class Game {

    private int id;
    private String name;
    private String genre;
    private String mode;

    public Game() {}

    public Game(int id, String name, String genre, String mode) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.mode = mode;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getGenre() { return genre; }
    public String getMode() { return mode; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setMode(String mode) { this.mode = mode; }

    @Override
    public String toString() {
        return "Game{id=" + id + ", name='" + name + "', genre='" + genre + "', mode='" + mode + "'}";
    }
}