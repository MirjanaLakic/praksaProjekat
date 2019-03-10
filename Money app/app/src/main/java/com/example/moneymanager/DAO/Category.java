package com.example.moneymanager.DAO;

public class Category {

    private String name;
    private int photo;
    private Type type;

    public Category() {
    }

    public Category(String name, int photo, Type type) {
        this.name = name;
        this.photo = photo;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getPhoto() {
        return photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

}
