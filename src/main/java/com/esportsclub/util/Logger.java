package com.esportsclub.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    // Singleton instance
    private static Logger instance;
    private PrintWriter writer;

    private static final String LOG_FILE = "esports_log.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private constructor
    private Logger() {
        try {
            writer = new PrintWriter(new FileWriter(LOG_FILE, true));
        } catch (IOException e) {
            System.out.println("Logger could not be initialized: " + e.getMessage());
        }
    }

    // Get single instance
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    // Log info message
    public void info(String message) {
        log("INFO", message);
    }

    // Log error message
    public void error(String message) {
        log("ERROR", message);
    }

    // Log warning message
    public void warning(String message) {
        log("WARNING", message);
    }

    // Core log method
    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = "[" + timestamp + "] [" + level + "] " + message;
        System.out.println(logMessage);
        if (writer != null) {
            writer.println(logMessage);
            writer.flush();
        }
    }

    // Close the writer
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}