package edu.asu.diging.gilesecosystem.septemberutil.service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;

public interface ISystemMessageHandler {

    public abstract void handleMessage(String msg, Exception exception, MessageType messageType);

    void handleMessage(String title, String msg, MessageType messageType);

}

