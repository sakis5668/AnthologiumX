package com.sakis.anthologium.actorlookup;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActorLookUpData {

    private final IntegerProperty actor_id;
    private final StringProperty firstName;
    private final StringProperty lastName;

    public ActorLookUpData(int actor_id, String firstName, String lastName) {
        this.actor_id = new SimpleIntegerProperty(actor_id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
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

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String toString() {
        return getLastName() + " " + getFirstName();
    }
}
