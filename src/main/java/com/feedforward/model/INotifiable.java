package com.feedforward.model;

public interface INotifiable {
    void receiveNotification(String msg);
    String getContactInfo();
}