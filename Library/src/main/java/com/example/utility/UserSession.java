package com.example.utility;

public class UserSession {
    private static UserSession instance;
    private String userId;
    private String userName;
    private String userType; // "librarian" or "borrower"
    private boolean isLoggedIn;

    private UserSession() {
        this.isLoggedIn = false;
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void createSession(String userId, String userName, String userType) {
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.isLoggedIn = true;
        System.out.println("Session created for user: " + userId + " (" + userType + ")");
    }

    public void clearSession() {
        this.userId = null;
        this.userName = null;
        this.userType = null;
        this.isLoggedIn = false;
        System.out.println("Session cleared");
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isLibrarian() {
        return "librarian".equals(userType);
    }

    public boolean isBorrower() {
        return "borrower".equals(userType);
    }
}
