/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.event;

/**
 * Class to represent the event payload in JSON format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class EventPayload {

    String id;
    String senderID;
    String functionID;
    Object payload;
    Object meta;
    String clientData;

    public EventPayload() {
    }

    public EventPayload(String id, String senderID, String functionID, Object payload, Object meta, String clientData) {
        this.id = id;
        this.senderID = senderID;
        this.functionID = functionID;
        this.payload = payload;
        this.meta = meta;
        this.clientData = clientData;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
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

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getFunctionID() {
        return functionID;
    }

    public void setFunctionID(String functionID) {
        this.functionID = functionID;
    }

    public String getClientData() {
        return clientData;
    }

    public void setClientData(String clientData) {
        this.clientData = clientData;
    }

}
