package com.esportsclub.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Singleton instance
    private static DBConnection instance;
    private Connection connection;

    // Veritabanı bilgileri
    private static final String URL = "jdbc:mysql://localhost:3306/esports_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "528344"; // MySQL şifreni buraya yazacaksın

    // Private constructor — dışarıdan new DBConnection() yapılamaz
    private DBConnection() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Veritabanı bağlantısı başarılı!");
        } catch (SQLException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
        }
    }

    // Tek instance'ı döndüren metot
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    // Connection nesnesini döndüren metot
    public Connection getConnection() {
        return connection;
    }
}