package com.sakis.anthologium.photos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PhotoData {

    private final IntegerProperty photo_id;
    private Byte[] image;

    public PhotoData(int photo_id, Byte[] image) {
        this.photo_id = new SimpleIntegerProperty(photo_id);
        this.image = image;
    }

    public int getPhoto_id() {
        return photo_id.get();
    }

    public IntegerProperty photo_idProperty() {
        return photo_id;
    }

    public Byte[] getImage() {
        return image;
    }

    public void setImage(Byte[] image) {
        this.image = image;
    }
}
