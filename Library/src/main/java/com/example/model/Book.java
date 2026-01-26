package com.example.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private BookStatus status;
    private String synopsis;
    private String coverImagePath;
    private Set<String> genres;
    private Set<String> availableFormats;

    public Book(String isbn, String title, String author, String synopsis, String publisher, LocalDate publicationDate, BookStatus status, String coverImagePath) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.synopsis = synopsis;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.status = status;
        this.coverImagePath = coverImagePath;
        this.genres = new HashSet<>();
        this.availableFormats = new HashSet<>();
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getSynopsis() { return synopsis; }
    public String getPublisher() { return publisher; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public BookStatus getStatus() { return status; }
    public String getCoverImagePath() { return coverImagePath; }
    public Set<String> getGenres() { return genres; }
    public Set<String> getAvailableFormats() { return availableFormats; }

    public void setCoverImagePath(String coverImagePath) { 
        this.coverImagePath = coverImagePath; 
    }


    public void addGenre(String genre) {
        genres.add(genre);
    }

    public void addAvailableFormats(String format) {
        availableFormats.add(format);
    }

    public enum BookStatus {
        AVAILABLE,
        CHECKED_OUT,
        RESERVED,
        LOST,
        DAMAGED
    }
}
