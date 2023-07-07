package com.example.recipeproject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SaveInfoController {
    @FXML
    private Button saveYesButton;

    @FXML
    private Button saveNoButton;

    public boolean isYes = false;

    private EditSaveModel editSaveModel;

    private CreateModel createModel;

    private String modelFile;

    public void setEditSaveModel(EditSaveModel editSaveModel){
        this.editSaveModel = editSaveModel;
        modelFile = "EditSaveModel";
    }

    public void setCreateModel(CreateModel createModel){
        this.createModel = createModel;
        modelFile = "CreateModel";
    }

    @FXML
    public void saveYesButtonClicked(){
        if(modelFile == "EditSaveModel") {
            editSaveModel.saveRecipeInfo();
            Stage stage = (Stage) saveYesButton.getScene().getWindow();
            stage.close();



        }else if(modelFile == "CreateModel"){
            createModel.saveRecipeInfo();
            Stage stage = (Stage) saveYesButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void saveNoButtonClicked(){
        Stage stage = (Stage) saveNoButton.getScene().getWindow();
        stage.close();
    }
}
