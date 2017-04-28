package edu.asu.diging.gilesecosystem.septemberutil.service.impl;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.ISystemMessageRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.SystemMessageRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.properties.Properties;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;

public class SystemMessageHandlerTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Mock
    private IPropertiesManager propertiesManager;

    @Mock
    private IRequestProducer requestProducer;

    @Mock
    private IRequestFactory<ISystemMessageRequest, SystemMessageRequest> requestFactory;

    @InjectMocks
    private SystemMessageHandler messageHandlerToTest;

    private final String KAFKA_TOPIC_SYSTEM_MESSAGES = "geco.requests.system.messages";
    private final String APPLICATION_ID = "geco.giles";
    private final String VALID_MESSAGE = "valid message";

    @Before
    public void setUp() {
        messageHandlerToTest = new SystemMessageHandler(APPLICATION_ID);
        MockitoAnnotations.initMocks(this);

        Mockito.when(propertiesManager.getProperty(Properties.KAFKA_TOPIC_SYSTEM_MESSAGES))
                .thenReturn(KAFKA_TOPIC_SYSTEM_MESSAGES);
    }

    @Test
    public void test_handleMessage_success()
            throws InstantiationException, IllegalAccessException, MessageCreationException {
        Exception ex = new Exception("Valid Exception");
        ISystemMessageRequest request = new SystemMessageRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setUploadId(null);
        request.setStatus(RequestStatus.NEW);
        Mockito.when(requestFactory.createRequest(Mockito.anyString(), Mockito.anyString())).thenReturn(request);
        messageHandlerToTest.handleMessage(VALID_MESSAGE, ex, MessageType.ERROR);
        Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(request,
                propertiesManager.getProperty(Properties.KAFKA_TOPIC_SYSTEM_MESSAGES));
    }

    @Test
    public void test_handleMessage_sendRequestException()
            throws InstantiationException, IllegalAccessException, MessageCreationException {
        Exception ex = new Exception("Valid Exception");
        ISystemMessageRequest request = new SystemMessageRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setUploadId(null);
        request.setStatus(RequestStatus.NEW);
        Mockito.when(requestFactory.createRequest(Mockito.anyString(), Mockito.anyString())).thenReturn(request);
        Mockito.doThrow(new MessageCreationException()).when(requestProducer).sendRequest(Mockito.anyObject(),
                Mockito.anyString());
        messageHandlerToTest.handleMessage(VALID_MESSAGE, ex, MessageType.ERROR);
        Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(request,
                propertiesManager.getProperty(Properties.KAFKA_TOPIC_SYSTEM_MESSAGES));
    }

    @Test
    public void test_handleMessage_createRequest_instantiationException()
            throws InstantiationException, IllegalAccessException, MessageCreationException {
        Mockito.when(requestFactory.createRequest(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new InstantiationException());
        Mockito.verify(requestProducer, Mockito.times(0)).sendRequest(Mockito.anyObject(),
                propertiesManager.getProperty(Properties.KAFKA_TOPIC_SYSTEM_MESSAGES));
    }

    @Test
    public void test_handleMessage_createRequest_illegalAccessException()
            throws InstantiationException, IllegalAccessException, MessageCreationException {
        Mockito.when(requestFactory.createRequest(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new IllegalAccessException());
        Mockito.verify(requestProducer, Mockito.times(0)).sendRequest(Mockito.anyObject(),
                propertiesManager.getProperty(Properties.KAFKA_TOPIC_SYSTEM_MESSAGES));
    }

}
