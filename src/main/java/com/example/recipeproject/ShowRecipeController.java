package com.example.recipeproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ShowRecipeController implements Initializable {

    @FXML
    public VBox rv_Vbox;

    @FXML
    public Label rvRecipeTitle;

    @FXML
    private Label rvPrepTimeLabel;

    @FXML
    private Label rvCookTimeLabel;

    @FXML
    private Label rvTypeLabel;

    @FXML
    private TextField rvServeCountTextField;

    @FXML
    public TextArea rvStepTextArea;

    @FXML
    private Button rvEditButton;

    @FXML
    private Button rvDeleteButton;

    @FXML
    private Button rvReturnButton;

    @FXML
    private ImageView rvImageView;

    @FXML
    private TableView<ObservableList<String>> rvIngredientTableView= new TableView<>();

    private ShowRecipeModel showRecipeModel = new ShowRecipeModel();

    private List<String> allRecipeList;
    //private EditSaveModel editSaveModel = new EditSaveModel();

    /**
     * Initialize the controller of recipeView, restrict the character type of serve count input TextField
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置只能输入数字的限制
//        Pattern pattern = Pattern.compile("\\d*");
        Pattern pattern = Pattern.compile("[1-9]*");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (pattern.matcher(change.getControlNewText()).matches() && newText.length() <= 3) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        rvServeCountTextField.setTextFormatter(textFormatter);
        rvServeCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            //System.out.println(newValue);
            try {
                if(!newValue.isEmpty()) {
                    updateIngredientTable(Integer.valueOf(newValue));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    private String recipeName;
    private String prepTime;
    private String cookTime;
    private List<String> labelList = new ArrayList<>();
    private String stepText;
    private String imagePath;

    public void getRecipeName(String name){
        recipeName = name;
        showRecipeModel.recipeName = name;
    }

    public void getAllRecipe(List<String> list){
        allRecipeList = list;
    }

    /**
     * Get the information data of the selected recipe from the recipeModel
     *
     * @throws SQLException
     */
    public void getRecipeInfo() throws SQLException {
        showRecipeModel.fetchAndDisplayData(recipeName);
        showRecipeModel.labelsFromDatabase(recipeName);

        showRecipeModel.getRecipeDetail();
        showRecipeModel.getImagePath(recipeName);

        prepTime = showRecipeModel.getPrepTime();
        //System.out.println("prepTime"+prepTime);
        cookTime = showRecipeModel.getCookTime();
        stepText = showRecipeModel.getStepText();
        labelList = showRecipeModel.getLabel();

        imagePath = showRecipeModel.getImage();
        setText();

    }

    /**
     * Set the recipe detail Text of recipeView
     */
    @FXML
    private void setText(){
        rvPrepTimeLabel.setText(prepTime);
        rvCookTimeLabel.setText(cookTime);
        rvTypeLabel.setText("Type");
        rvStepTextArea.setText(stepText);

        String labelStr = "";
        for(int i=0;i<labelList.size();i++){
            //System.out.println(labelList.get(i));
            labelStr += labelList.get(i)+" ";
        }
        rvTypeLabel.setText(labelStr);
        //File imageFile = new File("img1/recipe1.png");


        File imageFile = new File(imagePath);
        String string = imageFile.toURI().toString();
        Image image = new Image(string);
        rvImageView.setImage(image);
    }

    /**
     * Get the ingredient table from recipeModel, and then set the data to recipeView
     * @throws SQLException
     */
    @FXML
    public void setIngredientTable() throws SQLException {
        showRecipeModel.getIngredient();
        showRecipeModel.columnName.add("");
        for (int i = 1; i <= showRecipeModel.columnCount; i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(showRecipeModel.columnName.get(i-1));
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<String>, String> cellData) {
                    ObservableList<String> row = cellData.getValue();
                    if (row.size() >= columnIndex) {
                        return new SimpleStringProperty(row.get(columnIndex - 1));
                    } else {
                        return null;
                    }
                }
            });
            rvIngredientTableView.getColumns().add(column);
            }
        for(int i = 0; i< showRecipeModel.column_row.size(); i++){
                rvIngredientTableView.getItems().add(showRecipeModel.column_row.get(i));
        }
    }

    /**
     * Get the ingredient table from recipeModel, and then set the data to recipeView
     * @throws SQLException
     */
    @FXML
    public void updateIngredientTable(int serve) throws SQLException {
        showRecipeModel.updateIngredientTable(serve);
        showRecipeModel.columnName.add("");
        rvIngredientTableView.getColumns().clear();
        rvIngredientTableView.getItems().clear();
        for (int i = 1; i <= showRecipeModel.columnCount; i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(showRecipeModel.columnName.get(i-1));
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<String>, String> cellData) {
                    ObservableList<String> row = cellData.getValue();
                    if (row.size() >= columnIndex) {
                        return new SimpleStringProperty(row.get(columnIndex - 1));
                    } else {
                        return null;
                    }
                }
            });
                rvIngredientTableView.getColumns().add(column);
        }
        for(int i = 0; i< showRecipeModel.column_row.size(); i++){
            rvIngredientTableView.getItems().add(showRecipeModel.column_row.get(i));
            //System.out.println("TableView: "+recipeModel.column_row.get(i));
        }
    }

    /**
     * To be called when the edit Button clicked
     *
     * @throws SQLException
     */
    @FXML
    private void editButtonClicked() throws SQLException {
        try {
            FXMLLoader editViewLoader = new FXMLLoader(getClass().getResource("editView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(editViewLoader.load()));

            //EditController
            EditController editController = editViewLoader.getController();
            editController.returnRecipeName(recipeName);
            editController.evRecipeTitleTextField.setText(recipeName);
            editController.returnRecipeName(recipeName);
            editController.createRecipeInfo();
            //editController.setIngredientTable();
            editController.editSaveModel.initializeTable(recipeName);
            editController.ini_tableView();

            stage.show();
            //rv_Vbox.getScene().getWindow().hide();//隐藏
            //Close recent View
            Stage recipeStage = (Stage) rvEditButton.getScene().getWindow();
            recipeStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to show search result");
            alert.showAndWait();
        }
    }

    /**
     * To be called when the delete Button clicked
     */
    @FXML
    private void deleteButtonClicked() {
        try {
            FXMLLoader deleteInfoLoader = new FXMLLoader(getClass().getResource("DeleteInfo.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(deleteInfoLoader.load()));
            DeleteInfoController deleteInfoController = deleteInfoLoader.getController();
            deleteInfoController.getRecipeName(recipeName);
            stage.show();
/*
            //Close recent View
            Stage stage2 = (Stage) rvReturnButton.getScene().getWindow();
            stage2.close();*/

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to show search result");
            alert.showAndWait();
        }
    }


    /**
     * To be called when the return Button clicked
     */
    @FXML
    private void returnButtonClicked() throws IOException {
        //Close recent View
        Stage stage = (Stage) rvReturnButton.getScene().getWindow();
        stage.close();

        //Open the Search View
        try{
        FXMLLoader searchViewLoader = new FXMLLoader(getClass().getResource("searchView.fxml"));
        Stage searchStage = new Stage();
        searchStage.setScene(new Scene(searchViewLoader.load()));

        searchStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open search view");
            alert.showAndWait();
        }
    }
}