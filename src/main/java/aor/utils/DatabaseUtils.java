package aor.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import aor.config.DatabaseConfig;

public class DatabaseUtils {

  private static Connection connection = null;

  public static Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      connection = DriverManager.getConnection(
          DatabaseConfig.getUrl(),
          DatabaseConfig.getUser(),
          DatabaseConfig.getPassword());
    }
    return connection;
  }

  public static void closeConnection() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        System.err.println("Error closing database connection: " + e.getMessage());
      }
    }
  }

  public static void rollback() {
    if (connection != null) {
      try {
        connection.rollback();
      } catch (SQLException e) {
        System.err.println("Error during rollback: " + e.getMessage());
      }
    }
  }
}