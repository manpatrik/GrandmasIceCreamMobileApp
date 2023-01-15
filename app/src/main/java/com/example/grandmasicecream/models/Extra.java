package com.example.grandmasicecream.models;

import java.util.ArrayList;
import java.util.List;

public class Extra {
    private String type;
    private Boolean required;
    private List<Item> items;

    public Extra(String type) {
        this.type = type;
        this.required = false;
        this.items = new ArrayList<>();
    }

    public Extra(String type, Boolean required) {
        this.type = type;
        this.required = required;
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public Boolean getRequired() {
        return required;
    }

    public List<Item> getItems() {
        return items;
    }
}
