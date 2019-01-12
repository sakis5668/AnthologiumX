package com.sakis.anthologium.actors;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActorData {

    private final IntegerProperty actor_id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty nickName;
    private final StringProperty born;
    private final StringProperty died;
    private final StringProperty information;

    /**
     * Constructor
     * @param actor_id
     * @param firstName
     * @param lastName
     * @param born
     * @param died
     * @param information
     */
    public ActorData(int actor_id, String firstName, String lastName, String nickName, String born, String died, String information) {
        this.actor_id = new SimpleIntegerProperty(actor_id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.nickName = new SimpleStringProperty(nickName);
        this.born = new SimpleStringProperty(born);
        this.died = new SimpleStringProperty(died);
        this.information = new SimpleStringProperty(information);
    }

    public int getActor_id() {
        return actor_id.get();
    }

    public IntegerProperty actor_idProperty() {
        return actor_id;
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

    public String getNickName() {
        return nickName.get();
    }

    public StringProperty nickNameProperty() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName.set(nickName);
    }

    public String getBorn() {
        return born.get();
    }

    public StringProperty bornProperty() {
        return born;
    }

    public void setBorn(String born) {
        this.born.set(born);
    }

    public String getDied() {
        return died.get();
    }

    public StringProperty diedProperty() {
        return died;
    }

    public void setDied(String died) {
        this.died.set(died);
    }

    public String getInformation() {
        return information.get();
    }

    public StringProperty informationProperty() {
        return information;
    }

    public void setInformation(String information) {
        this.information.set(information);
    }
}
