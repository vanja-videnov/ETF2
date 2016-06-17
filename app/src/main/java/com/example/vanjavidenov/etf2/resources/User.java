package com.example.vanjavidenov.etf2.resources;

/**
 * Created by vanjavidenov on 6/15/16.
 */
public class User {

    private String username;
    private String password;
    private String email;
    private String phone;
    private String admin;

    public User(String un, String pass, String em, String ph, String ad){
        username = un;
        password = pass;
        email = em;
        phone = ph;
        admin = ad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
