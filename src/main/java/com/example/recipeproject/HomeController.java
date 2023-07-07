package com.example.recipeproject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.fxml.Initializable;

public class HomeController implements Initializable {

    @FXML
    private VBox hv_VBox;
    @FXML
    private TextField hvSearchInputTextField;
    @FXML
    private Button hvSearchButton;

    private List<String> recipeDB = new ArrayList<>();
    private List<String> recipe = new ArrayList<>();
    private List<String> resultRecipe = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置只能输入数字和字母的限制
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        hvSearchInputTextField.setTextFormatter(textFormatter);
    }

    @FXML
    private void searchButtonClicked() {

        try {
            FXMLLoader searchViewloader = new FXMLLoader(getClass().getResource("searchView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(searchViewloader.load()));

//            SearchController resultController = loader.getController();
//            resultController.setFruitName(fruitName);
            stage.show();
            hv_VBox.getScene().getWindow().hide();//隐藏主页
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to show search result");
            alert.showAndWait();
        }
    }

    @FXML
    private void createButtonClicked() {
        try{
            FXMLLoader createViewloader = new FXMLLoader(getClass().getResource("createView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(createViewloader.load()));

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to show search result");
            alert.showAndWait();
        }
    }

    @FXML
    private void exitButtonClicked() {
        Platform.exit();
    }
}