package com.example.recipeproject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class CreateTableRowData {
    private SimpleIntegerProperty id;
    private SimpleStringProperty ingredients;
    //private DoubleProperty quantity;
    private ObjectProperty<Double> quantity;
    private SimpleStringProperty unit;
    public CreateTableRowData(int id, String ingredient, Double quantity, String unit) {
        this.id = new SimpleIntegerProperty(id);
        this.ingredients = new SimpleStringProperty(ingredient);
        this.quantity = new SimpleObjectProperty<>(quantity);
        this.unit = new SimpleStringProperty(unit);
    }
    public CreateTableRowData(){

    }

    // Getter and Setter methods

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }
    public void setId(int id) {
        this.id.set(id);
    }


    public String getIngredients() {
        return ingredients.get();
    }

    public SimpleStringProperty ingredientsProperty() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients.set(ingredients);
    }

    public Double getQuantity() {
        return quantity.get();
    }

    public ObjectProperty<Double> quantityProperty() {
        return quantity;
    }

    public void setQuantityDou(Double quantity) {
        this.quantity.set(quantity);
    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }
}