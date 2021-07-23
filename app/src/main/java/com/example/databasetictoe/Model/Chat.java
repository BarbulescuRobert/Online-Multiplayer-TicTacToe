package com.example.databasetictoe.Model;

public class Chat {

    private String sender, receiver, msg;

    public Chat (String sender, String receiver, String msg)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
    }
    public Chat()
    {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
