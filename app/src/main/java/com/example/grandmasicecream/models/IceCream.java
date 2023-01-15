package com.example.grandmasicecream.models;

import androidx.annotation.NonNull;

public class IceCream {
    private Long id;
    private String name;
    private Status status;
    private String imageUrl;

    public enum Status {
        AVAILABLE,
        MELTED,
        UNAVAILABLE
    }

    public IceCream(Long id, String name, String status, String imageUrl) {
        this.id = id;
        this.name = name;
        setStatus(status);
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        switch (status){
            case "available":
                this.status = Status.AVAILABLE;
                break;
            case "melted":
                this.status = Status.MELTED;
                break;
            default:
                this.status = Status.UNAVAILABLE;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
