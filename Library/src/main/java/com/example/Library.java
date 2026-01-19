package com.example;

public class Library {
    private String id;
    private String name;
    private String address;

    public Library(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
}
