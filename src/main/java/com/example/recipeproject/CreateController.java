package com.example.recipeproject;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.DoubleStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class CreateController implements Initializable {

    @FXML
    private TextField cvRecipeTitleTextField;

    @FXML
    private TextField cvPrepTimeTextField;

    @FXML
    private TextField cvCookTimeTextField;

    @FXML
    private StackPane cvButtonStackPane;

    @FXML
    private TextArea cvStepTextArea;

    @FXML
    private Button cvSaveButton;

    @FXML
    private Button cvReturnButton;

    @FXML
    private TableView cvIngredientTableView;

    @FXML
    private ImageView cvImageView;


    private CreateModel createModel = new CreateModel();

    private List<String> selectedCategories= new ArrayList<>();

    private ObservableList<CreateTableRowData> data_View = FXCollections.observableArrayList();

    private ObservableList<String> options;
    private ObservableList<String> filteredOptions;
    private List<String> selectedOptions = new ArrayList<>();

    private File selectedImageFile;

    /**
     * Initialize the controller of recipeView,
     * restrict the character type of cook time and prepare time input TextField,
     * restrict the character type of recipe title input TextField,
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createTableView();
        System.out.println("createTableView");

        selectedCategories.clear();

        Pattern pattern1 = Pattern.compile("[0-9]*");
        UnaryOperator<TextFormatter.Change> filter1 = change -> {
            String newText = change.getControlNewText();
            if (pattern1.matcher(change.getControlNewText()).matches() && newText.length() <= 3) {
                return change;
            }
            return null;
        };

        TextFormatter<String> prepTimeTextFormatter = new TextFormatter<>(filter1);
        TextFormatter<String> cookTimeTextFormatter = new TextFormatter<>(filter1);
        cvPrepTimeTextField.setTextFormatter(prepTimeTextFormatter);
        cvCookTimeTextField.setTextFormatter(cookTimeTextFormatter);

        Pattern pattern2 = Pattern.compile("[a-zA-Z0-9 ]*");
        UnaryOperator<TextFormatter.Change> filter2 = change -> {
            if (pattern2.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        TextFormatter<String> recipeTitleTextFormatter = new TextFormatter<>(filter2);
        cvRecipeTitleTextField.setTextFormatter(recipeTitleTextFormatter);

        createCustomComboBoxListView();
    }

    ListView<String> listView = new ListView<>();
    StackPane dropdownPane = new StackPane(listView);
    private void createCustomComboBoxListView() {
        options = FXCollections.observableArrayList(
                "Meat",
                "Vegetable",
                "SeaFood",
                "Dessert"
        );

        filteredOptions = FXCollections.observableArrayList(
                options.subList(0, 4)
        );

        selectedOptions.clear();

        listView.setPrefHeight(120);
        listView.setCellFactory(param -> new ListCell<String>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setText(item);
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            selectedOptions.add(item);
                        } else {
                            selectedOptions.remove(item);
                        }
                        System.out.println(selectedOptions);
                    });
                    setGraphic(checkBox);
                }
            }
        });
        listView.setItems(filteredOptions);

        dropdownPane.getStyleClass().add("dropdown-pane");
        dropdownPane.setPrefHeight(120);

        cvButtonStackPane.setOnMouseClicked(this::handleButtonClicked);
    }

    PopupControl popupControl = new PopupControl() {
        @Override
        public void show(Window window) {
            super.show(window);
            Window buttonWindow = cvButtonStackPane.getScene().getWindow();
            Bounds buttonBounds = cvButtonStackPane.localToScreen(cvButtonStackPane.getBoundsInLocal());
            double buttonBottom = buttonBounds.getMaxY();
            double dropdownHeight = dropdownPane.getHeight();
            double dropdownY = buttonBottom + 2;
            double windowHeight = buttonWindow.getHeight();
            double windowBottom = buttonWindow.getY() + windowHeight;
            double dropdownTop = dropdownY + dropdownHeight;

            if (dropdownTop > windowBottom) {
                dropdownY = buttonBounds.getMinY() - dropdownHeight;
            }

            setAnchorX(buttonBounds.getMinX());
            setAnchorY(dropdownY);
        }
    };

    private void getNewType(){
        selectedCategories.clear();
        for(int i=0;i<selectedOptions.size();i++){
            selectedCategories.add(selectedOptions.get(i));
        }
    }

    private void handleButtonClicked(MouseEvent event) {

        dropdownPane.setPrefHeight(120);
        // 将 dropdownPane 的宽度设置为按钮宽度加上一个固定值
        dropdownPane.prefWidthProperty().bind(cvButtonStackPane.widthProperty());

        popupControl.setAutoHide(true);
        popupControl.setHideOnEscape(true);
        popupControl.getScene().setRoot(dropdownPane);


        if (popupControl.isShowing()) {
            popupControl.hide();
        } else {
            popupControl.show(cvButtonStackPane.getScene().getWindow());
        }
    }

    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            // 显示选择的图片
            Image image = new Image(selectedImageFile.toURI().toString());
            cvImageView.setImage(image);
        }
    }

    /**
     * To be called when the save Button clicked,
     * then judge if the texts are entered correctly,
     * and then transmit the information data to Model
     */
    @FXML
    private void saveButtonClicked() {

        //============
        //saveInfo
        System.out.println("saveButtonClicked");
        try {
            String errorText = "Error: ";
            if(isTitleEmpty() || isCookTimeEmpty() || isPrepTimeEmpty() || isStepEmpty()) {
                if(isTitleEmpty()){
                    errorText +="title, ";
                }
                if(isCookTimeEmpty()){
                    errorText +="preparing time, ";
                }
                if(isPrepTimeEmpty()){
                    errorText +="cooking time, ";
                }
                if(isStepEmpty()){
                    errorText +="steps, ";
                }
                errorText = errorText.substring(0, errorText.length() - 2);
                errorText += " is empty!";
                System.out.println(errorText);

                FXMLLoader errorInfoLoader = new FXMLLoader(getClass().getResource("errorInfo.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(errorInfoLoader.load()));
                ErrorInfoController errorInfoController = errorInfoLoader.getController();
                errorInfoController.setText(errorText);
                stage.show();

            }else {
                FXMLLoader saveInfoLoader = new FXMLLoader(getClass().getResource("saveInfo.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(saveInfoLoader.load()));
                stage.show();
                SaveInfoController saveInfoController = saveInfoLoader.getController();

                saveInfoController.setCreateModel(createModel);

                updateRecipeInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to show search result");
            alert.showAndWait();
        }

/*
        //数据变量 = get...(); ->随后存入数据库
        String recipeName = getRecipeTitle();
        updateRecipeInfo();

        createModel.saveRecipeName();
        createModel.saveType();

        createModel.createRecipeTableDB();
        createModel.getTable();
        createModel.saveTable();
        createModel.saveImage();
        createModel.createRecipeStepRecord();
        createModel.saveTimeAndStep();
*/

        //createModel.createRecipeStepRecord(recipeName);

    }


    @FXML
    public boolean isTitleEmpty(){
        //recipeNameEdited = getRecipeTitle();
        if(getRecipeTitle().isEmpty()){
            return true;
        } else
            return false;
    }

    @FXML
    public boolean isPrepTimeEmpty(){
        //prepTimeEdited = getCookTime();
        if(getCookTime().isEmpty()){
            return true;
        } else
            return false;
    }

    @FXML
    public boolean isCookTimeEmpty(){
        //cookTimeEdited = getPrepTime();
        if(getPrepTime().isEmpty()){
            return true;
        } else
            return false;
    }

    @FXML
    public boolean isStepEmpty(){
        //stepTextEdited = getStepText();
        if(getStepText().isEmpty()){
            return true;
        } else
            return false;
    }


    public void updateRecipeInfo(){
        System.out.println("updateRecipeInfo");
        //数据变量 = get...(); ->随后存入数据库
        //selectedCategories.clear();
        getNewType();

        createModel.getData_View(getData_View());
        createModel.getRecipeTitle(getRecipeTitle());
        createModel.getPrepTime(getCookTime());
        createModel.getCookTime(getPrepTime());
        createModel.getStepText(getStepText());
        createModel.getType(getType());
        createModel.getImageFile(getImageFile());
    }

    /**
     * To be called when the cancel Button clicked
     */
    @FXML
    private void returnButtonClicked() {
        Stage stage = (Stage) cvReturnButton.getScene().getWindow();
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

    @FXML
    private void selectTypeAction(){

    }

    public void createTableView(){
        for (int i = 0; i < 30; i++) { // 这里假设表格有10行
            data_View.add(new CreateTableRowData(0, null, null, null));
        }

        // 设置表格列
        TableColumn<CreateTableRowData, String> ingredientCol = new TableColumn<>("Ingredient");
        ingredientCol.setCellValueFactory(cellData -> cellData.getValue().ingredientsProperty());
        ingredientCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<CreateTableRowData, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));


        TableColumn<CreateTableRowData, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        unitCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<CreateTableRowData, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idCol.setVisible(false); // 设置id列为不可见

        cvIngredientTableView.setEditable(true); // 设置表格可编辑
        cvIngredientTableView.setItems(data_View);
        cvIngredientTableView.getColumns().addAll(idCol, ingredientCol, quantityCol, unitCol);

    }

    private ObservableList<CreateTableRowData> getData_View(){
        return data_View;
    }

    /**
     * Get the recipe title from cvRecipeTitleTextField in the create view
     * @return cvRecipeTitleTextField.getText()
     */
    private String getRecipeTitle(){
        return cvRecipeTitleTextField.getText();
    }

    /**
     * Get the cook time from cvRecipeTitleTextField in the create view
     * @return cvCookTimeTextField.getText()
     */
    private String getCookTime(){
        return cvCookTimeTextField.getText();
    }

    /**
     * Get the preparing time from cvRecipeTitleTextField in the create view
     * @return cvPrepTimeTextField.getText()
     */
    private String getPrepTime(){
        return cvPrepTimeTextField.getText();
    }

    /**
     * Get the recipe type from cvRecipeTitleTextField in the create view
     * @return cvTypeSelectSMB.getTypeSelector()
     */
    private List<String> getType(){
        return selectedCategories;//? 获取选中的选项
    }

    /**
     * Get the step detail from cvRecipeTitleTextField in the create view
     * @return cvStepTextArea.getText()
     */
    private String getStepText(){
        return cvStepTextArea.getText();
    }

    private File getImageFile(){
        return selectedImageFile;
    }
}
