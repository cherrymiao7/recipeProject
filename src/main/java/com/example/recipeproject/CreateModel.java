package com.example.recipeproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreateModel {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "157116";
    private static final String IMAGE_FOLDER = "src/img/";


    private TableView<CreateTableRowData> tableView = new TableView<>();
    private ObservableList<CreateTableRowData> data;
    //private ObservableList<TableRowDataTest> data_View = FXCollections.observableArrayList();
    //private String recipeName = "a_test";
    private boolean [] skipRow = new boolean[50];

    public String recipeName;
    public String prepTime;
    public String cookTime;
    public String stepText;
    private List<String> selectedCategories= new ArrayList<>();
    private File selectedImageFile;

    private ObservableList<CreateTableRowData> data_View = FXCollections.observableArrayList();

    public void getData_View(ObservableList<CreateTableRowData> data){
        data_View = data;
    }
    public void getRecipeTitle(String name){
        recipeName = name;
    }

    public void getPrepTime(String time){
        prepTime = time;
    }
    public void getCookTime(String time){
        cookTime = time;
    }
    public void getStepText(String step){
        stepText = step;
    }
    public void getType(List<String> list){
        selectedCategories = list;
    }
    public void getImageFile(File file){
        selectedImageFile = file;
    }

    public void saveRecipeName(){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement preparedStatement;

            String insertQuery = "INSERT INTO recipename (name) VALUES (?)";
            preparedStatement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, recipeName);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createRecipeTableDB(){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = conn.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS `" + recipeName + "` (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "ingredient VARCHAR(100), " +
                    "quantity DOUBLE, " +
                    "unit VARCHAR(100))";
            statement.executeUpdate(createTableQuery);

            // 查询表格数据
//            String selectQuery = "SELECT * FROM " + recipeName;
            String selectQuery = "SELECT * FROM `" + recipeName + "`";
            ResultSet resultSet = statement.executeQuery(selectQuery);
            data = FXCollections.observableArrayList();
            // 添加一行默认的空数据
            for (int i = 0; i < 30; i++) { // 这里假设表格有10行
                data.add(new CreateTableRowData(0, null, null, null));
            }

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ingredient = resultSet.getString("ingredients");
                double quantity = resultSet.getDouble("quantity");
                String unit = resultSet.getString("unit");

                CreateTableRowData rowData = new CreateTableRowData(id, ingredient, quantity, unit);
                data.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getTable(){
        System.out.println("getTable");
        for(int i=0;i<30;i++){
            skipRow[i]=false;
        }
        int index = 0;
        for (CreateTableRowData rowData : data_View) {
            index ++;
            System.out.println(rowData.getIngredients()+" "+rowData.getQuantity()+" "+rowData.getUnit());
            if (rowData.getIngredients() == null && rowData.getUnit() == null && rowData.getQuantity() == null) {
                // 如果一行的所有列都为空，跳过该行
                skipRow[index] = true;
            } else {
                if (rowData.getIngredients() == null) {
                    // 如果 ingredient 为空，显示警告并跳过该行
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Missing Data");
                    alert.setHeaderText("Please enter the ingredient");
                    alert.setContentText("Skipping the row without ingredient.");
                    alert.showAndWait();
                    skipRow[index] = true;
                }

                if (rowData.getQuantity() == null) {
                    // 如果 quantity 为空，显示警告并跳过该行
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Missing Data");
                    alert.setHeaderText("Please enter the quantity for ingredient: " + rowData.getIngredients());
                    alert.setContentText("Skipping the row without quantity.");
                    alert.showAndWait();
                    skipRow[index] = true;
                }

                if (rowData.getUnit() == null) {
                    // 如果 unit 为空，显示警告并跳过该行
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Missing Data");
                    alert.setHeaderText("Please enter the unit for ingredient: " + rowData.getIngredients());
                    alert.setContentText("Skipping the row without unit.");
                    alert.showAndWait();
                    skipRow[index] = true;
                }
            }
        }
    }

    public void saveTable(){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 删除原有表格的数据
            //String deleteDataQuery = "DELETE FROM " + name;
            String deleteDataQuery = "DELETE FROM `" + recipeName + "`";
            PreparedStatement deleteStatement = conn.prepareStatement(deleteDataQuery);
            deleteStatement.executeUpdate();
            deleteStatement.close();

            PreparedStatement insertStatement = conn.prepareStatement(
                    "INSERT INTO " + recipeName + " (ingredient, quantity, unit) VALUES (?, ?, ?)"
            );

            int index = 0;
            for (CreateTableRowData rowData : data_View) {
                index ++;
                System.out.println(index);
                if (!skipRow[index]) {
                    System.out.println("save"+index);
                    insertStatement.setString(1, rowData.getIngredients());
                    insertStatement.setObject(2, rowData.getQuantity());
                    insertStatement.setString(3, rowData.getUnit());
                    insertStatement.executeUpdate();
                }
            }
            Statement updateStatement = conn.createStatement();
            String updateQuery = "ALTER TABLE " + recipeName + " DROP COLUMN id";
            updateStatement.executeUpdate(updateQuery);

            updateQuery = "ALTER TABLE " + recipeName + " ADD COLUMN id INT AUTO_INCREMENT PRIMARY KEY FIRST";
            updateStatement.executeUpdate(updateQuery);

            insertStatement.close();
            conn.close();
            System.out.println("Table data saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveType(){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement preparedStatement;

            // Clear the existing values in the specified row
            String updateQuery = "UPDATE " + "recipename" + " SET label1 = ?, label2 = ?, label3 = ?, label4 = ? WHERE name = ?";
            preparedStatement = conn.prepareStatement(updateQuery);
            preparedStatement.setString(1, "");
            preparedStatement.setString(2, "");
            preparedStatement.setString(3, "");
            preparedStatement.setString(4, "");
            preparedStatement.setString(5, recipeName);
            preparedStatement.executeUpdate();


            // Insert the selected categories into the corresponding columns
            updateQuery = "UPDATE " + "recipename" + " SET label1 = ?, label2 = ?, label3 = ?, label4 = ? WHERE name = ?";
            preparedStatement = conn.prepareStatement(updateQuery);
            for (int i = 0; i < selectedCategories.size(); i++) {
                preparedStatement.setString(i + 1, selectedCategories.get(i));
            }
            for (int i = selectedCategories.size(); i < 4; i++) {
                preparedStatement.setString(i + 1, "");
            }
            preparedStatement.setString(5, recipeName);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //public static void createRecipeStepRecord(String recipeName) {
    public void createRecipeStepRecord() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertQuery = "INSERT INTO recipestep (name) VALUES (?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setString(1, recipeName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTimeAndStep() {
        try {
            // 重新建立数据库连接
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 更新数据库中的数据
            String updateQuery1 = "UPDATE recipestep SET `prepared time` = ?, `cook time` = ?, steps = ? WHERE name = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery1);
            updateStatement.setString(1, prepTime);
            updateStatement.setString(2, cookTime);
            updateStatement.setString(3, stepText);
            updateStatement.setString(4, recipeName);
            updateStatement.executeUpdate();
            updateStatement.close();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveImage() {
        if (selectedImageFile != null) {
            try {
                // 复制图片到目标文件夹
                String fileName = selectedImageFile.getName();
                File destinationFile = new File(IMAGE_FOLDER + fileName);
                Path sourcePath = selectedImageFile.toPath();
                Path destinationPath = destinationFile.toPath();
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // 获取相对路径
                Path relativePath = Paths.get("src").relativize(destinationPath);

                // 将图片路径保存到数据库
                saveImagePathToDatabase(recipeName, relativePath.toString());

                System.out.println("Image saved successfully.");
            } catch (IOException e) {
                System.out.println("Error saving image: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Error saving image path to database: " + e.getMessage());
            }
        } else {
            System.out.println("No image selected.");
        }
    }

    private void saveImagePathToDatabase(String recipeName, String imagePath) throws SQLException {
        String updateQuery = "UPDATE recipestep SET image = ? WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, imagePath);
            preparedStatement.setString(2, recipeName);
            preparedStatement.executeUpdate();
        }
    }

    public void saveRecipeInfo(){

        System.out.println("SAVE!!!");

        saveRecipeName();
        saveType();

        createRecipeTableDB();
        getTable();
        saveTable();
        saveImage();
        createRecipeStepRecord();
        saveTimeAndStep();

    }
}
