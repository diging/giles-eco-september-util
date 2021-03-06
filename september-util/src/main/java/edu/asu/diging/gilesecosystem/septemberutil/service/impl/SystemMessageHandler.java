package edu.asu.diging.gilesecosystem.septemberutil.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.ISystemMessageRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.SystemMessageRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.properties.Properties;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;

@Service
public class SystemMessageHandler implements ISystemMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String applicationId;

    @Autowired
    private IRequestProducer requestProducer;

    @Autowired
    private IRequestFactory<ISystemMessageRequest, SystemMessageRequest> requestFactory;

    @Autowired
    private IPropertiesManager propertiesManager;

    @PostConstruct
    public void setup() {
        requestFactory.config(SystemMessageRequest.class);
    }

    public SystemMessageHandler() {

    }

    public SystemMessageHandler(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public void handleMessage(String msg, Exception exception, MessageType messageType) {
        logger.info("The following message was logged: " + msg);
        if (exception != null) {
            logger.error("With exception: " + exception.getMessage(), exception);
        }
        String exceptionText = "";
        if (exception != null) {
            StringWriter sWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(sWriter));
            exceptionText = sWriter.toString();
        }
        sendMessage(msg, exception.getMessage(), exceptionText, messageType);
    }
    
    @Override
    public void handleMessage(String title, String msg, MessageType messageType) {
        logger.info("The following message was logged: " + title);
        logger.info("with content: " + msg);
        
        sendMessage(title, msg, null, messageType);
    }

    private void sendMessage(String title, String msg, String exception, MessageType messageType) {
        ISystemMessageRequest request;
        try {
            request = requestFactory.createRequest(UUID.randomUUID().toString(), null);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Could not create request.", e);
            return;
        }
        request.setApplicationId(applicationId);
        request.setTitle(title);
        request.setMessage(msg);
        request.setStackTrace(exception);
        request.setMessageType(messageType.getType());
        request.setMessageTime(ZonedDateTime.now().toString());

        try {
            requestProducer.sendRequest(request, propertiesManager.getProperty(Properties.KAFKA_TOPIC_SYSTEM_MESSAGES));
        } catch (MessageCreationException e) {
            logger.error("Could not send request.", e);
        }
    }
}
