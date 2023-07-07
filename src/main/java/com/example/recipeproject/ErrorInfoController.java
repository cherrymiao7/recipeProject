package com.example.recipeproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ErrorInfoController {

    @FXML
    private Label errorLabel;

    public void setText(String errorInfo){
        errorLabel.setText(errorInfo);
    }

}
