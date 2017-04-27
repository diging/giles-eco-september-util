package edu.asu.diging.gilesecosystem.septemberutil.properties;

import edu.asu.diging.gilesecosystem.requests.ISystemMessageRequest;

/**
 * Enum class to define message types
 *
 * @author snilapwa
 *
 */
public enum MessageType {
    
    ERROR(ISystemMessageRequest.ERROR), 
    WARNING(ISystemMessageRequest.WARNING), 
    INFO(ISystemMessageRequest.INFO), 
    DEBUG(ISystemMessageRequest.DEBUG);

    private String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
