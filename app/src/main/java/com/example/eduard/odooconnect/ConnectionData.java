package com.example.eduard.odooconnect;

import java.io.Serializable;

/**
 * Created by Edu on 16/5/17.
 */

public class ConnectionData implements Serializable {

    private String url, // "192.168.1.228",
            db, // "odoodb2",
            username, // "odoo",
            password; //"odoodb",
    private Integer port;// 8069

    public ConnectionData() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
