/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

/**
 * Class used during the post to have the subscriber details registered
 *
 * @author Balaji Gopalakrishnan
 */
public class SubscriberInputs {
    String id;
    HashMap<String,String> links = new HashMap<>();
    
    @JsonProperty("class")
    String group;
    
    String eventId;
    String destUrl;
    String clientData;

    public SubscriberInputs() {
    }

    public SubscriberInputs(String id, HashMap<String,String> links, String group, String eventId, String destUrl, String clientData) {
        this.id = id;
        this.links = links;
        this.group = group;
        this.eventId = eventId;
        this.destUrl = destUrl;
        this.clientData = clientData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(HashMap<String,String> links) {
        this.links = links;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDestUrl() {
        return destUrl;
    }

    public void setDestUrl(String destUrl) {
        this.destUrl = destUrl;
    }

    public String getClientData() {
        return clientData;
    }

    public void setClientData(String clientData) {
        this.clientData = clientData;
    }  
//    
//    public void reg(){
//        Registry.subscribers.put(this.id, this);
//    }
}
