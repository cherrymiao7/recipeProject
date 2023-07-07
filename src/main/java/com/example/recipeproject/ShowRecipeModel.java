package com.example.recipeproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowRecipeModel {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "157116";

    private String preparedTime;
    private String cookTime;
    private String steps;

    public int columnCount;
    public List<String> columnName = new ArrayList<>();
    public List<String> recipeList = new ArrayList<>();
    public Statement statement;
    public ResultSet resultSet;
    List<ObservableList<String>> column_row = new ArrayList();

    // 公共表名变量
    public String recipeName = "";

    /**
     * Get recipe Detail (prepared time, cook time
     *
     * @throws SQLException
     */

    public void getRecipeDetail() throws SQLException {
        // 建立数据库连接
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        // 查询语句
        String query = "SELECT * FROM recipestep WHERE name = ?";

        // 创建 PreparedStatement 并设置参数
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, recipeName);

        // 执行查询
        ResultSet resultSet = preparedStatement.executeQuery();

        // 获取查询结果
        if (resultSet.next()) {
            // 读取查询结果中的数据
            preparedTime = resultSet.getString("prepared time");
            cookTime = resultSet.getString("cook time");
            steps = resultSet.getString("steps");
        }
    }

    public void getIngredient() throws SQLException {
        columnName.clear();
        column_row.clear();
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER , DB_PASSWORD);
        // 创建查询语句
        String query = "SELECT * FROM `" + recipeName + "`";

        // 执行查询
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        // 获取结果集元数据
        columnCount = resultSet.getMetaData().getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columnName.add(resultSet.getMetaData().getColumnName(i));
        }

        while (resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= columnCount; i++) {
                row.add(resultSet.getString(i));
                //System.out.println("row: " + row);
            }
            column_row.add(row);
            //System.out.println("column: "+ column_row);
        }
        // 关闭连接和资源
        resultSet.close();
        statement.close();
        connection.close();
    }

    public void updateIngredientTable(int serve) throws SQLException {
        columnName.clear();
        column_row.clear();
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER , DB_PASSWORD);
        // 创建查询语句
        String query = "SELECT * FROM `" + recipeName + "`";

        // 执行查询
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        // 获取结果集元数据
        columnCount = resultSet.getMetaData().getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columnName.add(resultSet.getMetaData().getColumnName(i));
        }

        while (resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            //System.out.println("resultSet.getString(3): " + resultSet.getString(3));
            for (int i = 1; i <= columnCount; i++) {
                if(i == 3){
                    row.add(String.valueOf(Double.parseDouble(resultSet.getString(i))*serve));
                }else {
                    row.add(resultSet.getString(i));
                }
                //System.out.println("row: " + row);
            }
            column_row.add(row);
            //System.out.println("column: "+ column_row);
        }
        // 关闭连接和资源
        resultSet.close();
        statement.close();
        connection.close();
    }



    //public static String recipename = "vegetable beef soup recipe";
    private List<String> labelList = new ArrayList<>();

    public void fetchAndDisplayData(String recipename) {
        try {
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 查询语句
            String query = "SELECT * FROM recipestep WHERE name = ?";

            // 创建 PreparedStatement 并设置参数
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, recipename);

            // 执行查询
            ResultSet resultSet = preparedStatement.executeQuery();

            // 获取查询结果
            if (resultSet.next()) {
                // 读取查询结果中的数据
                String prepared_time = resultSet.getString("prepared time");
                String cook_time = resultSet.getString("cook time");
                String steps1 = resultSet.getString("steps");

                preparedTime = prepared_time;
                cookTime = cook_time;
                steps = steps1;
            }

            // 关闭连接和资源
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void labelsFromDatabase(String recipename) {
        try {
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 查询语句
            String query = "SELECT label1, label2, label3, label4 FROM recipename WHERE name = ?";

            // 创建 PreparedStatement 并设置参数
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, recipename);

            // 执行查询
            ResultSet resultSet = preparedStatement.executeQuery();

            // 获取查询结果
            if (resultSet.next()) {
                // 读取查询结果中的数据
                String label1 = resultSet.getString("label1");
                String label2 = resultSet.getString("label2");
                String label3 = resultSet.getString("label3");
                String label4 = resultSet.getString("label4");

                // 将不为空的内容添加到列表
                if (label1 != null && !label1.isEmpty()) {
                    labelList.add(label1);
                    System.out.println("label1: "+label1);
                }
                if (label2 != null && !label2.isEmpty()) {
                    labelList.add(label2);
                    System.out.println("label2: "+label2);
                }
                if (label3 != null && !label3.isEmpty()) {
                    labelList.add(label3);
                    System.out.println("label3: "+label3);
                }
                if (label4 != null && !label4.isEmpty()) {
                    labelList.add(label4);
                    System.out.println("label4: "+label4);
                }
            }

            // 关闭连接和资源
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    String relativePath;
    //String resultRelativePath;


    public String getImage(){
        return relativePath;
    }

    public void getImagePath(String name) {
        relativePath = "img1/recipe1.png";//这个能显示
        //relativePath = "img1/recipe.png";//这个不能显示
/*
        try {
            // 连接数据库
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 查询图片路径
            String selectQuery = "SELECT image FROM recipestep WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                relativePath = "img1/"+resultSet.getString("image");
                //===============

                System.out.println("ShowImage: "+relativePath);
                // 显示图片
                // Image image;
                //image = new Image(relativePath);
                //rvImageView.setImage(image);
            }

//
//            int lastIndex = relativePath.lastIndexOf("/");
//            System.out.println(lastIndex);
//
//            if (lastIndex >= 0) {
//                int thirdLastIndex = relativePath.lastIndexOf("/", lastIndex - 2);
//                if (thirdLastIndex >= 0) {
//                    resultRelativePath = relativePath.substring(thirdLastIndex + 1);
//                    System.out.println("1result: "+resultRelativePath);  // 输出 "img1/"
//                }
//                System.out.println("2result: "+resultRelativePath);
//            }

            // 关闭数据库连接
            resultSet.close();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error querying image path from database: " + e.getMessage());
        }*/


    }

    public String getPrepTime(){
        return preparedTime;
    }
    public String getCookTime(){
        return cookTime;
    }
    public String getStepText(){
        return steps;
    }
    public List<String> getLabel(){
        return labelList;
    }


}
