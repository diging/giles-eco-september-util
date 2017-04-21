package edu.asu.diging.gilesecosystem.septemberutil.service;

public interface ISystemMessageHandler {

    public abstract void handleError(String msg, Exception exception);

    public abstract void handleWarning(String msg, Exception exception);

}

