package com.example.model;

public class Borrower {
    private String id;
    private String lastName;
    private String firstName;
    private int age; 
    private String email;
    private String password;

    public Borrower(String id, String lastName, String firstName, int age, String email, String password) {
        this.id = id;
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
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
