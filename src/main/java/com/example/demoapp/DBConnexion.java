package com.example.demoapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnexion {
  private static final String URL = "jdbc:mysql://localhost:3306/demoapp";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "";

  private static Connection connection;

  public static Connection getConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
      }
    } catch (SQLException e) {
      System.out.println("Erreur lors de l'établissement de la connexion à la base de données:");
      System.out.println("SQLState: " + e.getSQLState());
      System.out.println("Code d'erreur: " + e.getErrorCode());
      System.out.println("Message: " + e.getMessage());
      e.printStackTrace();
    }
    return connection;
  }

  public static void closeConnection() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
