package com.example.model;

public class Borrower {
    private String id;
    private String lastName;
    private String firstName;
    private int age; 
    private BorrowerType type;
    private String email;
    private String password;

    public Borrower(String id, String lastName, String firstName, int age, BorrowerType type, String email, String password) {
        this.id = id;
        this.type = type;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public int getAge() { return age; }
    public BorrowerType getType() { return type; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public enum BorrowerType {
        STUDENT,
        FACULTY,
        GENERAL
    }
}
