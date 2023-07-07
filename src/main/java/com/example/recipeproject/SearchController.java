package com.example.recipeproject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SearchController implements Initializable {
    @FXML
    private VBox sv_VBox;
    @FXML
    private TextField svSearchInputTextField;
    @FXML
    private ListView<String> svShowResultListView;
    @FXML
    private Button svCreateButton;
    @FXML
    private StackPane svButtonStackPane;

    private List<String> recipeName = new ArrayList<>();
    int recipeCount = 0;


    private ObservableList<String> displayedRecipe = FXCollections.observableArrayList();

    private List<String> allRecipeList;


    public int recipeIndex = 0;

    public String clickedRecipeName;

    private ObservableList<String> options;
    private ObservableList<String> filteredOptions;
    private List<String> selectedOptions = new ArrayList<>();
    private List<String> selectedCategories= new ArrayList<>();

    private SearchRecipeModel searchRecipeModel = new SearchRecipeModel();
    //private RecipeModel recipeModel = new RecipeModel();
    private ShowRecipeModel showRecipeModel;

    /**
     * Initialize the controller of searchView, restrict the character type of search input TextField
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置只能输入数字和字母的限制
        Pattern pattern = Pattern.compile("[a-zA-Z0-9 ]*");//user can only input " ","0-9","a-z","A-Z".
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        svSearchInputTextField.setTextFormatter(textFormatter);

        createCustomComboBoxListView();
    }

    public void setShowRecipeModel(ShowRecipeModel showRecipeModel) {
        this.showRecipeModel = showRecipeModel;
        //System.out.println("this.recipeModel");
        //this.recipeModel = new RecipeModel();
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

        svButtonStackPane.setOnMouseClicked(this::handleButtonClicked);
    }

    PopupControl popupControl = new PopupControl() {
        @Override
        public void show(Window window) {
            super.show(window);
            Window buttonWindow = svButtonStackPane.getScene().getWindow();
            Bounds buttonBounds = svButtonStackPane.localToScreen(svButtonStackPane.getBoundsInLocal());
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

    private void getSelectedType(){
        selectedCategories.clear();
        for(int i=0;i<selectedOptions.size();i++){
            selectedCategories.add(selectedOptions.get(i));
        }
    }

    private void handleButtonClicked(MouseEvent event) {

        dropdownPane.setPrefHeight(120);
        // 将 dropdownPane 的宽度设置为按钮宽度加上一个固定值
        dropdownPane.prefWidthProperty().bind(svButtonStackPane.widthProperty());

        popupControl.setAutoHide(true);
        popupControl.setHideOnEscape(true);
        popupControl.getScene().setRoot(dropdownPane);


        if (popupControl.isShowing()) {
            popupControl.hide();
        } else {
            popupControl.show(svButtonStackPane.getScene().getWindow());
        }
    }

    /**
     * To be called when the search Button is clicked.
     * This method get recipeList data from recipeListModel, and then show the recipes in ListView
     *
     * @throws SQLException
     */
    @FXML
    private void searchButtonClicked() throws SQLException {

        getSelectedType();

        String searchKeyword = svSearchInputTextField.getText();
        searchRecipeModel.searchRecipe(searchKeyword, selectedCategories);

        recipeCount = searchRecipeModel.recipeCount;
        recipeName = searchRecipeModel.recipeNameList;

        displayedRecipe.clear();
        for (int i = 0; i < recipeCount; i++) {
            displayedRecipe.add(recipeName.get(i));
        }
        svShowResultListView.setItems(displayedRecipe);
    }


    /**
     * To be called when the create Button is clicked.
     * Open the createView
     */
    @FXML
    private void createButton() {
        try{
            FXMLLoader createViewLoader = new FXMLLoader(getClass().getResource("createView.fxml"));
            Stage createStage = new Stage();
            createStage.setScene(new Scene(createViewLoader.load()));

            createStage.show();

            //Close recent View
            Stage stage_close = (Stage) sv_VBox.getScene().getWindow();
            stage_close.close();


        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open create view");
            alert.showAndWait();
        }
    }

    /**
     * To be called when the exit Button is clicked.
     * Exit the application
     */
    @FXML
    private void ExitButton(){
        Platform.exit();
    }

    /**
     * To be called when the recipeName in the ListView is clicked.
     * @param event
     * @throws SQLException
     */
    @FXML
    private void recipeClicked(MouseEvent event) throws SQLException {

        if (event.getClickCount() == 2) {

            recipeIndex = svShowResultListView.getSelectionModel().getSelectedIndex();

            clickedRecipeName = recipeName.get(recipeIndex);


            String selectedRecipe = svShowResultListView.getSelectionModel().getSelectedItem();
            if (selectedRecipe != null) {
                try {
                    FXMLLoader recipeViewLoader = new FXMLLoader(getClass().getResource("recipeView.fxml"));
                    Stage stage = new Stage();
                    stage.setScene(new Scene(recipeViewLoader.load()));

                    ShowRecipeController showRecipeController = recipeViewLoader.getController();
                    showRecipeController.getRecipeName(clickedRecipeName);
                    showRecipeController.getRecipeInfo();
                    showRecipeController.rvRecipeTitle.setText(clickedRecipeName);
                    showRecipeController.setIngredientTable();

                    showRecipeController.getAllRecipe(allRecipeList);

                    stage.show();
                    //Close recent View
                    Stage stage_close = (Stage) sv_VBox.getScene().getWindow();
                    stage_close.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to show fruit detail");
                    alert.showAndWait();
                }
            }
            System.out.println("更新！！！");


        }
    }
}

