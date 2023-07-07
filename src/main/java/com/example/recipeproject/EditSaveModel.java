package com.example.recipeproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditSaveModel {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/recipes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "157116";
    private static final String IMAGE_FOLDER = "src/main/java/com/example/recipeproject/img1/";
    //private static final String IMAGE_FOLDER = "src/main/java/com/example/recipeproject/img1/";


    // 公共表名变量
    //public static String recipename = "apple pie recipe";
    // 声明全局的 connection 变量
    private Connection connection;


    public String recipeName;
    public String recipeOldName;
    public String prepTime;
    public String cookTime;
    public String type;
    public String stepText;

    public List<String> selectedCategories= new ArrayList<>();
    private File selectedImageFile;

    public void getRecipeTitle(String name){
        recipeName = name;
    }


    public void getPrepTime(String time){
        prepTime = time;
    }
    public void getCookTime(String time){
        cookTime = time;
    }
    public void getType(List<String> list){
        selectedCategories = list;
    }
    public void getStepText(String step){
        stepText = step;
    }
    public void getImageFile(File file){
        selectedImageFile = file;
    }


    public void saveTimeAndStep(String oldName) {
        try {
            // 重新建立数据库连接
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 更新数据库中的数据
            String updateQuery1 = "UPDATE recipestep SET `prepared time` = ?, `cook time` = ?, steps = ?, name = ? WHERE name = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery1);
            updateStatement.setString(1, prepTime);
            updateStatement.setString(2, cookTime);
            updateStatement.setString(3, stepText);
            updateStatement.setString(4, recipeName);
            updateStatement.setString(5, oldName);
            updateStatement.executeUpdate();
            updateStatement.close();

            // 更新label里recipe名
            String updateQuery2 = "UPDATE recipename SET name = ? WHERE name = ?";
            PreparedStatement updateStatement2 = connection.prepareStatement(updateQuery2);
            updateStatement2.setString(1, recipeName);
            updateStatement2.setString(2, oldName);
            updateStatement2.executeUpdate();
            updateStatement2.close();

            // 更新表名
            String oldTableName = oldName;
            String newTableName = recipeName;
            String alterQuery = "ALTER TABLE `" + oldTableName + "` RENAME TO `" + newTableName + "`";
            PreparedStatement alterStatement = connection.prepareStatement(alterQuery);
            alterStatement.executeUpdate();
            alterStatement.close();

            oldName = recipeName;

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 公共表名变量

    public ObservableList<TableRowData> data;
    private int maxId;

    public int columnCount = 0;

    public ResultSetMetaData metaData;
    public List<String> columnName = new ArrayList<>();

    public void initializeTable(String name) {
        recipeOldName = name;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = conn.createStatement();

            //String sql = "SELECT * FROM `" + name + "`";
            String sql = "SELECT * FROM `" + name + "`";
            ResultSet resultSet = statement.executeQuery(sql);

            data = FXCollections.observableArrayList();

            metaData = resultSet.getMetaData();
            columnCount = metaData.getColumnCount();
            //System.out.println("columnCount: "+columnCount);

            columnName.add("");
            columnName.add("");
            for (int i = 2; i <= columnCount; i++) {
                columnName.add(metaData.getColumnName(i));
                //System.out.println("columnName: "+columnName);
            }
            //System.out.println("columnCount: "+columnCount);

            // 添加表格行数据
            maxId = 0;
            int blankRowCount = 0; // 记录空白行数量
            while (resultSet.next()) {
                TableRowData rowData = new TableRowData();
                rowData.setId(resultSet.getInt("id"));
                rowData.setIngredients(resultSet.getString("ingredients"));
                rowData.setQuantityStr(resultSet.getString("quantity"));
                rowData.setUnit(resultSet.getString("unit"));

                System.out.print(resultSet.getString("ingredients"));
                System.out.print(resultSet.getString("quantity"));
                System.out.println(resultSet.getString("unit"));

                data.add(rowData);

                if (rowData.getId() > maxId) {
                    maxId = rowData.getId();
                }
                //System.out.println("maxId: "+maxId);
            }
            // 添加额外的空白行数据
            for (int i = 0; i < 100; i++) {
                TableRowData rowData = new TableRowData();
                rowData.setId(maxId + i + 1); // 使用动态的递增 ID
                rowData.setIngredients("");
                rowData.setQuantityStr("");
                rowData.setUnit("");
                data.add(rowData);
                blankRowCount++; // 增加空白行数量
            }
            // 跳过空白行的数量，更新最新的 ID
            maxId += blankRowCount;

            System.out.println("maxId: "+maxId);
//            tableView.setItems(data);
//            tableView.setEditable(true); // 允许编辑表格
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> recipeNameList = new ArrayList<>();
    public int recipeCount = 0;
    //private ResultSet resultSet;



    /**
     * Connect to the database and get the recipe name list from database according to the search words
     *
     * @throws SQLException
     */
    public void searchRecipeList() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = conn.createStatement();
        recipeCount = 0;

        String sql = "SELECT name FROM recipename";
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            recipeCount++;
            recipeNameList.add(name);
        }
        System.out.println("RecipeList: "+recipeNameList);
    }

    public List<String> getAllRecipe(){
        return recipeNameList;
    }




    public void saveNewIngredients(String name) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 删除原有表格的数据
            String deleteDataQuery = "DELETE FROM `" + name + "`";
            PreparedStatement deleteStatement = conn.prepareStatement(deleteDataQuery);
            deleteStatement.executeUpdate();
            deleteStatement.close();


            for (TableRowData rowData : data) {
                if (!isRowEmpty(rowData)) {
                    // 执行插入操作
                    String insertQuery = "INSERT INTO `" + name + "` (id, ingredients, quantity, unit) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
                    preparedStatement.setInt(1, rowData.getId());
                    preparedStatement.setString(2, rowData.getIngredients());
                    setDoubleOrNull(preparedStatement, 3, rowData.getQuantity());
                    preparedStatement.setString(4, rowData.getUnit());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();

                }
            }

// 重新排序 id 列
            String updateQuery = "ALTER TABLE `" + name + "` DROP COLUMN id";
            Statement updateStatement = conn.createStatement();
            updateStatement.executeUpdate(updateQuery);
            updateStatement.close();

            updateQuery = "ALTER TABLE `" + name + "` ADD COLUMN id INT AUTO_INCREMENT PRIMARY KEY FIRST";
            updateStatement = conn.createStatement();
            updateStatement.executeUpdate(updateQuery);
            updateStatement.close();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isRowEmpty(TableRowData rowData) {
        return rowData.getIngredients().isEmpty() && rowData.getQuantity().isEmpty() && rowData.getUnit().isEmpty();
    }

    private void setDoubleOrNull(PreparedStatement preparedStatement, int parameterIndex, String value) throws SQLException {
        if (value == null || value.isEmpty()) {
            preparedStatement.setDouble(parameterIndex, 0.0);
        } else {
            preparedStatement.setDouble(parameterIndex, Double.parseDouble(value));
        }
    }

    public void saveType(String name,List<String> list){
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
            preparedStatement.setString(5, name);
            preparedStatement.executeUpdate();


            // Insert the selected categories into the corresponding columns
            updateQuery = "UPDATE " + "recipename" + " SET label1 = ?, label2 = ?, label3 = ?, label4 = ? WHERE name = ?";
            preparedStatement = conn.prepareStatement(updateQuery);
            for (int i = 0; i < list.size(); i++) {
                preparedStatement.setString(i + 1, list.get(i));
            }
            for (int i = list.size(); i < 4; i++) {
                preparedStatement.setString(i + 1, "");
            }
            preparedStatement.setString(5, name);
            preparedStatement.executeUpdate();


            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveImage(File selectedImageFile, String name) {
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
                //saveImagePathToDatabase(name, relativePath.toString());
                saveImagePathToDatabase(name, fileName);

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





    public void saveRecipeInfo() {
        System.out.println("SAVE!!!");

        //Save recipe name
        saveTimeAndStep(recipeOldName);//Save detail information of the recipe
        saveNewIngredients(recipeName);//Save ingredient table
        saveType(recipeName,selectedCategories);
        saveImage(selectedImageFile,recipeName);

    }


    public static class TableRowData {
        private int id;
        private SimpleStringProperty ingredients;
        private SimpleStringProperty quantity;
        private SimpleStringProperty unit;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public TableRowData() {
            this.id = 0;
            this.ingredients = new SimpleStringProperty();
            this.quantity = new SimpleStringProperty();
            this.unit = new SimpleStringProperty();
        }

        public ObservableValue<String> getColumnValue(int columnIndex) {
            switch (columnIndex) {
                case 2:
                    return ingredients;
                case 3:
                    return quantity;
                case 4:
                    return unit;
                default:
                    return null;
            }
        }

        public String getIngredients() {
            return ingredients.get();
        }

        public void setIngredients(String ingredients) {
            this.ingredients.set(ingredients);
        }

        public String getQuantity() {
            return quantity.get();
        }

        public void setQuantityStr(String quantity) {
            this.quantity.set(quantity);
        }


        public String getUnit() {
            return unit.get();
        }

        public void setUnit(String unit) {
            this.unit.set(unit);
        }
    }
}





