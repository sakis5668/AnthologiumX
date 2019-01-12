package com.sakis.anthologium.songs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UrlData {

    private IntegerProperty song_id;
    private StringProperty url;

    public UrlData(int song_id, String url) {
        this.song_id = new SimpleIntegerProperty(song_id);
        this.url = new SimpleStringProperty(url);
    }

    public int getSong_id() {
        return song_id.get();
    }

    public IntegerProperty song_idProperty() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id.set(song_id);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }
}
