package com.example.utility;
import java.sql.*;

public class DatabaseConnection {
    public Connection getConnection() throws SQLException {
        String DATABASE_URL = "jdbc:postgresql://ep-bold-dew-ah8zwm25-pooler.c-3.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_0ysfDLiU2Ful&sslmode=require&channelBinding=require";

        try {
            System.out.println("Connection established");
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}
