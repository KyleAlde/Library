package com.example.utility;
import java.sql.*;

public class DatabaseConnection {
    private static final String TEST_DATABASE_URL = "jdbc:postgresql://ep-long-tree-ahfewdq1-pooler.c-3.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_xUDkPfQ6IVB2&sslmode=require&channelBinding=require";

    private static final String DATABASE_URL = "jdbc:postgresql://ep-bold-dew-ah8zwm25-pooler.c-3.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_0ysfDLiU2Ful&sslmode=require&channelBinding=require";

    private Connection connection;

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(TEST_DATABASE_URL);
        }
        return connection;
    }
}
