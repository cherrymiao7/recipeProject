package com.example.recipeproject;

import java.sql.*;

public class DeleteModel {


    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "157116";

    public void deleteRecipeStep(String recipeName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = "DELETE FROM recipestep WHERE name = ?";
            try (PreparedStatement statement = conn.prepareStatement(deleteQuery)) {
                statement.setString(1, recipeName);
                int rowsAffected = statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecipeName(String recipeName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = "DELETE FROM recipename WHERE name = ?";
            try (PreparedStatement statement = conn.prepareStatement(deleteQuery)) {
                statement.setString(1, recipeName);
                int rowsAffected = statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecipeTable(String recipeName) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = "DROP TABLE `" + recipeName + "`";
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(deleteQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
