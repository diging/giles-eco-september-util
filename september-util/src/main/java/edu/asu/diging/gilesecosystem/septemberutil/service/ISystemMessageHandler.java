package edu.asu.diging.gilesecosystem.septemberutil.service;

public interface ISystemMessageHandler {

    public abstract void handleException(String msg, Exception exception, String messageType);

}

