package com.sakis.anthologium.photos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PhotoActorData {
    private final IntegerProperty actor_photo_id;
    private final IntegerProperty photo_id;
    private final IntegerProperty actor_id;
    private final StringProperty lastname;
    private final StringProperty firstname;

    public PhotoActorData(int actor_photo_id, int photo_id, int actor_id, String lastname, String firstname) {
        this.actor_photo_id = new SimpleIntegerProperty(actor_photo_id);
        this.photo_id = new SimpleIntegerProperty(photo_id);
        this.actor_id = new SimpleIntegerProperty(actor_id);
        this.lastname = new SimpleStringProperty(lastname);
        this.firstname = new SimpleStringProperty(firstname);
    }

    public int getActor_photo_id() {
        return actor_photo_id.get();
    }

    public IntegerProperty actor_photo_idProperty() {
        return actor_photo_id;
    }

    public void setActor_photo_id(int actor_photo_id) {
        this.actor_photo_id.set(actor_photo_id);
    }

    public int getPhoto_id() {
        return photo_id.get();
    }

    public IntegerProperty photo_idProperty() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id.set(photo_id);
    }

    public int getActor_id() {
        return actor_id.get();
    }

    public IntegerProperty actor_idProperty() {
        return actor_id;
    }

    public void setActor_id(int actor_id) {
        this.actor_id.set(actor_id);
    }

    public String getLastname() {
        return lastname.get();
    }

    public StringProperty lastnameProperty() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname.set(lastname);
    }

    public String getFirstname() {
        return firstname.get();
    }

    public StringProperty firstnameProperty() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname.set(firstname);
    }
}
