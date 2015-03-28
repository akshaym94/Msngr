/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package msngrextras;

import java.io.Serializable;

/**
 *
 * @author Akshay Mahajan
 */
public class User implements Serializable{
    private String username;
    private String password;
    private String status;

    public String getStatus() {
        return status;
    }

    public User(String user, String pass) {
        this.username = user;
        this.password = pass;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

