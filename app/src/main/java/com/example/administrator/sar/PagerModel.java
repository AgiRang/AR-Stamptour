package com.example.administrator.sar;

public class PagerModel {
    String id;
    String ImageUri;

    public PagerModel(String id, String ImageUri) {
        this.id = id;
        this.ImageUri = ImageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
