package com.sakis.anthologium.songs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sakis.anthologium.util.DbConnector;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SongData {

    private IntegerProperty song_id;
    private StringProperty title;
    private IntegerProperty composer;
    private IntegerProperty lyrics;
    private StringProperty tone;
    private StringProperty scale;

    
    public SongData(int song_id, String title, int composer, int lyrics, String tone, String scale) {
        this.song_id = new SimpleIntegerProperty(song_id);
        this.title = new SimpleStringProperty(title);
        this.composer = new SimpleIntegerProperty(composer);
        this.lyrics = new SimpleIntegerProperty(lyrics);
        this.tone = new SimpleStringProperty(tone);
        this.scale = new SimpleStringProperty(scale);
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

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getComposer() {
        return composer.get();
    }

    public IntegerProperty composerProperty() {
        return composer;
    }

    public void setComposer(int composer) {
        this.composer.set(composer);
    }

    public int getLyrics() {
        return lyrics.get();
    }

    public IntegerProperty lyricsProperty() {
        return lyrics;
    }

    public void setLyrics(int lyrics) {
        this.lyrics.set(lyrics);
    }

    public String getTone() {
        return tone.get();
    }

    public StringProperty toneProperty() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone.set(tone);
    }

    public String getScale() {
        return scale.get();
    }

    public StringProperty scaleProperty() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale.set(scale);
    }

    public String getComposerName() {
        String sql = "SELECT * FROM actor WHERE actor_id = ?";
        String composerName = "";
        try {
            PreparedStatement preparedStatement = DbConnector.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1,getComposer());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                composerName = resultSet.getString("firstname") + " " + resultSet.getString("lastname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composerName;
    }

    public String getLyricsName() {
        String sql = "SELECT * FROM actor WHERE actor_id = ?";
        String lyricsName = "";
        try {
            PreparedStatement preparedStatement = DbConnector.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1,getLyrics());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                lyricsName = resultSet.getString("firstname") + " " + resultSet.getString("lastname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lyricsName;
    }

}
