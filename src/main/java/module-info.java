module com.example.recipeproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.recipeproject to javafx.fxml;
    exports com.example.recipeproject;
}