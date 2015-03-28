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
public class Message implements Serializable{
    private final String sender;
    private final String receiver;
    private final String message;

    public Message(String s, String r, String m) {
        this.sender = s;
        this.receiver = r;
        this.message = m;
    }
    
    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}

