package com.example.recipeproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchRecipeModel {

    //the information used for connecting the database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "157116";

    //private List<String> selectedOptions = new ArrayList<>();
    public List<String> recipeNameList = new ArrayList<>();
    public int recipeCount = 0;
    //private ResultSet resultSet;



    /**
     * Connect to the database and get the recipe name list from database according to the search words
     *
     * @param keyword user enter in the search Text Field
     * @throws SQLException
     */
    public void searchRecipe(String keyword, List<String> selectedOptions) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = conn.createStatement();
        recipeCount = 0;

        String sql = "SELECT name FROM recipename";
        ResultSet resultSet = statement.executeQuery(sql);
        List<String> searchResult = new ArrayList<>();


        while (resultSet.next()) {
            String name = resultSet.getString("name");
            if (name.contains(keyword)) {
                searchResult.add(name);

            }
        }


        //ObservableList<String> result = FXCollections.observableArrayList();
        recipeNameList.clear();
        // 遍历recipeNames，在recipename表格中的对应行搜索label1, label2, label3, label4列
        for (String recipeName : searchResult) {
            String searchQuery = "SELECT * FROM recipename WHERE name = ?";
            try (PreparedStatement searchStatement = conn.prepareStatement(searchQuery)) {
                searchStatement.setString(1, recipeName);
                try (ResultSet searchResultSet = searchStatement.executeQuery()) {
                    if (searchResultSet.next()) {
                        String label1 = searchResultSet.getString("label1");
                        String label2 = searchResultSet.getString("label2");
                        String label3 = searchResultSet.getString("label3");
                        String label4 = searchResultSet.getString("label4");

                        // 判断label1, label2, label3, label4是否包含selectedOptions中的所有元素
                        boolean allMatch = true;
                        for (String option : selectedOptions) {
                            boolean matchFound = false;
                            if (label1 != null && label1.equals(option)) {
                                matchFound = true;
                            } else if (label2 != null && label2.equals(option)) {
                                matchFound = true;
                            } else if (label3 != null && label3.equals(option)) {
                                matchFound = true;
                            } else if (label4 != null && label4.equals(option)) {
                                matchFound = true;
                            }

                            if (!matchFound) {
                                allMatch = false;
                                break;
                            }
                        }

                        // 如果找到了匹配的记录，则将recipeName添加到结果列表中
                        if (allMatch) {
                            recipeNameList.add(recipeName);
                            recipeCount++;
                        }
                    }
                }
            }
        }


        //System.out.println("Model recipeCount" + recipeCount);
        resultSet.close();
        statement.close();
        conn.close();
    }

}
