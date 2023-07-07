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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class EditController implements Initializable {

    @FXML
    public TextField evRecipeTitleTextField;

    @FXML
    private TextField evPrepTimeTextField;

    @FXML
    private TextField evCookTimeTextField;

    @FXML
    private Button evTypeSelectButton;

    @FXML
    private TextArea evStepTextArea;

    @FXML
    private Button evSaveButton;

    @FXML
    private Button evReturnButton;

    @FXML
    private StackPane cvButtonStackPane;

    @FXML
    private ImageView evImageView;

    @FXML
    private TableView<EditSaveModel.TableRowData> evIngredientTableView = new TableView<>();
    //private TableView<ObservableList<String>> evIngredientTableView = new TableView<>();

    public EditSaveModel editSaveModel = new EditSaveModel();

    private ObservableList<EditSaveModel.TableRowData> data = FXCollections.observableArrayList();

    private ShowRecipeModel showRecipeModel = new ShowRecipeModel();
    public List<String> recipeNameList = new ArrayList<>();

    private List<String> selectedCategories = new ArrayList<>();

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
        evPrepTimeTextField.setTextFormatter(prepTimeTextFormatter);
        evCookTimeTextField.setTextFormatter(cookTimeTextFormatter);

        Pattern pattern2 = Pattern.compile("[a-zA-Z0-9 ]*");
        UnaryOperator<TextFormatter.Change> filter2 = change -> {
            if (pattern2.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        TextFormatter<String> recipeTitleTextFormatter = new TextFormatter<>(filter2);
        evRecipeTitleTextField.setTextFormatter(recipeTitleTextFormatter);

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

    /**
     * Get new selected Type
     */
    private void getNewType(){
        selectedCategories.clear();
        for(int i=0;i<selectedOptions.size();i++){
            selectedCategories.add(selectedOptions.get(i));
        }
    }

    /**
     *
     * @param event
     */
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

    private String recipeName;
    private String prepTime;
    private String cookTime;
    private String stepText;
    private String imagePath;

    public void returnRecipeName(String name){
        showRecipeModel.recipeName = name;
        recipeName = name;
    }

    /**
     * Get the information data of the selected recipe from the recipeModel
     *
     * @throws SQLException
     */
    public void createRecipeInfo() throws SQLException {
        showRecipeModel.getRecipeDetail();
        showRecipeModel.getImagePath(recipeName);
        prepTime = showRecipeModel.getPrepTime();
        cookTime = showRecipeModel.getCookTime();
        stepText = showRecipeModel.getStepText();
        imagePath = showRecipeModel.getImage();

        setText();
    }

    /**
     * Set the recipe detail Text of recipeView
     */
    @FXML
    private void setText(){
        evPrepTimeTextField.setText(prepTime);
        evCookTimeTextField.setText(cookTime);
        //evTypeSelectButton.setText("Type");
        evStepTextArea.setText(stepText);

        File imageFile = new File(imagePath);
        String string = imageFile.toURI().toString();
        Image image = new Image(string);
        evImageView.setImage(image);

    }


    /**
     * Get the ingredient table from recipeModel, and then set the data to recipeView
     * @throws SQLException
     */
    public void ini_tableView() {
        // 创建表格列并绑定到数据模型

        System.out.println("---ini_tableView---");
        System.out.println("columnCount: "+editSaveModel.columnCount);
        for (int i = 2; i <= editSaveModel.columnCount; i++) {
            final int columnIndex = i;
            TableColumn<EditSaveModel.TableRowData, String> tableColumn = new TableColumn<>(editSaveModel.columnName.get(i));

            System.out.println("columnName.get(i): "+editSaveModel.columnName.get(i));
            if (columnIndex == 3) {
                tableColumn.setCellFactory(TextFieldTableCell.forTableColumn()); // 使用默认的TextFieldTableCell
                // 添加验证和错误处理逻辑
                tableColumn.setOnEditCommit(event -> {
                    String newValue = event.getNewValue();
                    if (newValue.matches("-?\\d*\\.?\\d*")) {
                        EditSaveModel.TableRowData rowData = event.getTableView().getItems().get(event.getTablePosition().getRow());
                        rowData.setQuantityStr(newValue);
                    } else {
                        // 非数字输入，报错并要求重新输入
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText("Invalid Quantity");
                        alert.setContentText("Please enter a valid numeric value for the quantity.");
                        alert.showAndWait();
                        evIngredientTableView.refresh(); // 刷新表格以显示原始值
                    }
                });
            } else {
                tableColumn.setCellFactory(TextFieldTableCell.forTableColumn()); // 允许编辑单元格
            }

            tableColumn.setCellValueFactory(cellData -> cellData.getValue().getColumnValue(columnIndex));
            evIngredientTableView.getColumns().add(tableColumn);
        }

        evIngredientTableView.setItems(editSaveModel.data);
        evIngredientTableView.setEditable(true); // 允许编辑表格

    }


    //Save-----------------

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
            evImageView.setImage(image);
        }
    }
/*
    private boolean isSameRecipeName() throws SQLException {
        editSaveModel.searchRecipeList();
        recipeNameList = editSaveModel.getAllRecipe();
        for(int i=0;i<recipeNameList.size();i++){
            if(recipeName==recipeNameList.get(i)){
                return true;
            }
        }
        return false;
    }

 */

    private String getEmptyError(){
        String errorText = "Error: ";
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
        return errorText;
    }

    /**
     * To be called when the save Button clicked,
     * then judge if the texts are entered correctly,
     * and then transmit the information data to Model
     */
    @FXML
    private void saveButtonClicked() {
        //saveInfo
        System.out.println("saveButtonClicked");
        try {

            if(isTitleEmpty() || isCookTimeEmpty() || isPrepTimeEmpty() || isStepEmpty()) {
                String errorText=getEmptyError();


                FXMLLoader errorInfoLoader = new FXMLLoader(getClass().getResource("errorInfo.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(errorInfoLoader.load()));
                ErrorInfoController errorInfoController = errorInfoLoader.getController();
                errorInfoController.setText(errorText);
                stage.show();
/*
            }else if(isSameRecipeName()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "The recipe name is existed!");
                alert.showAndWait();
*/
            }else{
                FXMLLoader saveInfoLoader = new FXMLLoader(getClass().getResource("saveInfo.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(saveInfoLoader.load()));
                stage.show();
                SaveInfoController saveInfoController = saveInfoLoader.getController();
                saveInfoController.setEditSaveModel(editSaveModel);

                updateRecipeInfo();

            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to show search result");
            alert.showAndWait();
        }
        /*catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * To be called when the cancel Button clicked
     */
    @FXML
    private void returnButtonClicked() {
        //Close recent View
        Stage stage = (Stage) evReturnButton.getScene().getWindow();
        stage.close();

        //Open the ShowRecipe View
        try{
            FXMLLoader showRecipeViewLoader = new FXMLLoader(getClass().getResource("recipeView.fxml"));
            Stage recipeStage = new Stage();
            recipeStage.setScene(new Scene(showRecipeViewLoader.load()));
            ShowRecipeController showRecipeController = showRecipeViewLoader.getController();
            showRecipeController.getRecipeName(recipeName);
            showRecipeController.getRecipeInfo();
            showRecipeController.rvRecipeTitle.setText(recipeName);
            showRecipeController.setIngredientTable();

            recipeStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open show recipe view");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String recipeNameEdited;
    public String prepTimeEdited;
    public String cookTimeEdited;
    public String typeEdited;
    public String stepTextEdited;

    @FXML
    public boolean isTitleEmpty(){
        recipeNameEdited = getRecipeTitle();
        if(recipeNameEdited.isEmpty()){
            return true;
        } else
            return false;
    }

    @FXML
    public boolean isPrepTimeEmpty(){
        prepTimeEdited = getCookTime();
        if(prepTimeEdited.isEmpty()){
            return true;
        } else
            return false;
    }

    @FXML
    public boolean isCookTimeEmpty(){
        cookTimeEdited = getPrepTime();
        if(cookTimeEdited.isEmpty()){
            return true;
        } else
            return false;
    }

    @FXML
    public boolean isStepEmpty(){
        stepTextEdited = getStepText();
        if(stepTextEdited.isEmpty()){
            return true;
        } else
            return false;
    }

    /**
     * Get the recipe title from cvRecipeTitleTextField in the create view
     * @return cvRecipeTitleTextField.getText()
     */
    private String getRecipeTitle(){
        return evRecipeTitleTextField.getText();
    }

    /**
     * Get the cook time from cvRecipeTitleTextField in the create view
     * @return cvCookTimeTextField.getText()
     */
    private String getCookTime(){
        return evCookTimeTextField.getText();
    }

    /**
     * Get the preparing time from cvRecipeTitleTextField in the create view
     * @return cvPrepTimeTextField.getText()
     */
    private String getPrepTime(){
        return evPrepTimeTextField.getText();
    }

    /**
     * Get the recipe type from cvRecipeTitleTextField in the create view
     * @return cvTypeSelectSMB.getTypeSelector()
     */
    private List<String> getType(){
        return selectedCategories;
    }

    /**
     * Get the step detail from cvRecipeTitleTextField in the create view
     * @return cvStepTextArea.getText()
     */
    private String getStepText(){
        return evStepTextArea.getText();
    }

    private File getImageFile(){
        return selectedImageFile;
    }
    /**
     * Save these data to recipeSaveModel
     */
    public void updateRecipeInfo(){
        System.out.println("updateRecipeInfo");
        //数据变量 = get...(); ->随后存入数据库
        //selectedCategories.clear();
        getNewType();

        editSaveModel.getRecipeTitle(getRecipeTitle());
        editSaveModel.getPrepTime(getCookTime());
        editSaveModel.getCookTime(getPrepTime());
        editSaveModel.getType(getType());
        editSaveModel.getStepText(getStepText());
        editSaveModel.getImageFile(getImageFile());
    }


}
