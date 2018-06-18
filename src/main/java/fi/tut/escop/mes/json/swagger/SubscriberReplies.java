/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger;

/**
 * Class to represent the event payload in JSON format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class SubscriberReplies {
    String id;
    String senderID;
    long lastEmit;
    Object payload;
    String clientData;

    public SubscriberReplies() {
    }
    
    public SubscriberReplies(String id, String senderID, long lastEmit, Object payload, String clientData) {
        this.id = id;
        this.senderID = senderID;
        this.lastEmit = lastEmit;
        this.payload = payload;
        this.clientData = clientData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getLastEmit() {
        return lastEmit;
    }

    public void setLastEmit(long lastEmit) {
        this.lastEmit = lastEmit;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getClientData() {
        return clientData;
    }

    public void setClientData(String clientData) {
        this.clientData = clientData;
    }
    
    
}
