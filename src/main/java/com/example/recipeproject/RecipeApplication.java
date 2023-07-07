package com.example.recipeproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RecipeApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader homeFxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("homeView.fxml"));
        FXMLLoader searchFxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("searchView.fxml"));
        //FXMLLoader recipeFxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("recipeView.fxml"));

        //Scene homeScene = new Scene(homeFxmlLoader.load(), 320, 240);
        Scene searchScene = new Scene(searchFxmlLoader.load());
        //Scene recipeScene = new Scene(recipeFxmlLoader.load(), 320, 240);

        //stage.setTitle("Recipe");

        //stage.setScene(homeScene);
        stage.setScene(searchScene);
//        stage.setScene(searchScene);
//        stage.setScene(recipeScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

        ShowRecipeModel recipeModel = new ShowRecipeModel();
        //HomeController homeController = new HomeController();
        SearchController searchController = new SearchController();
        //RecipeController recipeController = new RecipeController();


        System.out.println("Main_controller");
        searchController.setShowRecipeModel(recipeModel);
        //searchController.setRecipeController(recipeController);
        //recipeController.setRecipeModel(recipeModel);
    }
}