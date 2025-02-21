package aor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App implements AutoCloseable {
    private final static String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres";
    private Connection conn;

    public App() throws SQLException {
        this.conn = DriverManager.getConnection(App.URL, App.USER, App.PASSWORD);

    }

    private void queryEmployees() throws SQLException {
        String sql = "SELECT * FROM emp WHERE ndep = ? or ndep= ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, 10);
            stm.setInt(2, 20);
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.println("Nome: " + rs1.getString("nome") + " Dep:"
                            + rs1.getString("ndep"));
                }
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    public static void main(String[] args) {
        try (App app = new App()) {
            app.queryEmployees();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}