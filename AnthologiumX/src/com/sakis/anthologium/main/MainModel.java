package com.sakis.anthologium.main;

import java.sql.Connection;
import java.sql.SQLException;

import com.sakis.anthologium.util.DbConnector;

public class MainModel {

    Connection connection;

    /**
     * Constructor of the LoginModel Class
     * sets this.connection and looks up whether the needed tables exist
     * if not, then create them
     */

    public MainModel() {
        try {
            this.connection = DbConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (this.connection == null) {
            System.exit(1);
        }
        if (!DbConnector.tableExists("actor")) {
            DbConnector.createTableActor();
        }
        if (!DbConnector.tableExists("photo")) {
            DbConnector.createTablePhoto();
        }
        if (!DbConnector.tableExists("actor_photo")) {
            DbConnector.createTableActorPhoto();
        }
        if (!DbConnector.tableExists("songs")) {
            DbConnector.createTableSongs();
        }
        if (!DbConnector.tableExists("song_url")) {
            DbConnector.createTableSongURLs();
        }
    }
}
