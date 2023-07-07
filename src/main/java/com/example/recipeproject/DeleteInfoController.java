package com.example.recipeproject;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DeleteInfoController {

    @FXML
    private Button deleteYesButton;

    @FXML
    private Button deleteNoButton;

    private DeleteModel deleteModel = new DeleteModel();
    private String recipeName;
    @FXML
    private void deleteYesButtonClicked(){
        deleteModel.deleteRecipeStep(recipeName);
        deleteModel.deleteRecipeName(recipeName);
        deleteModel.deleteRecipeTable(recipeName);
        Stage stage = (Stage) deleteYesButton.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void deleteNoButtonClicked(){
        Stage stage = (Stage) deleteNoButton.getScene().getWindow();
        stage.close();
    }

    public void getRecipeName(String name){
        recipeName = name;
    }
}
