package edu.asu.diging.gilesecosystem.septemberutil.service.impl;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
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

    Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

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
    public void test_handleMessage_success() {
        Exception ex = new Exception("Valid Exception");
        ISystemMessageRequest request = new SystemMessageRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setUploadId(null);
        request.setStatus(RequestStatus.NEW);
        try {
            Mockito.when(requestFactory.createRequest(Mockito.anyString(), Mockito.anyString())).thenReturn(request);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Could not create request.", e);
            return;
        }
        messageHandlerToTest.handleMessage(VALID_MESSAGE, ex, MessageType.ERROR);
        try {
            Mockito.verify(requestProducer, Mockito.times(1)).sendRequest(request,
                    propertiesManager.getProperty(Properties.KAFKA_TOPIC_SYSTEM_MESSAGES));
        } catch (MessageCreationException e) {
            logger.error("Could not send request.", e);
        }
    }
}
