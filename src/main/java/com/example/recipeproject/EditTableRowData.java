package com.example.recipeproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class EditTableRowData {
    private int id;
    private SimpleStringProperty ingredients;
    private SimpleStringProperty quantity;
    private SimpleStringProperty unit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EditTableRowData() {
        this.id = 0;
        this.ingredients = new SimpleStringProperty();
        this.quantity = new SimpleStringProperty();
        this.unit = new SimpleStringProperty();
    }


    public ObservableValue<String> getColumnValue(int columnIndex) {
        switch (columnIndex) {
            case 2:
                return ingredients;
            case 3:
                return quantity;
            case 4:
                return unit;
            default:
                return null;
        }
    }

    public String getIngredients() {
        return ingredients.get();
    }

    public void setIngredients(String ingredients) {
        this.ingredients.set(ingredients);
    }

    public String getQuantity() {
        return quantity.get();
    }

    public void setQuantityStr(String quantity) {
        this.quantity.set(quantity);
    }


    public String getUnit() {
        return unit.get();
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

}
