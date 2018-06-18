/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.service;

import fi.tut.escop.mes.json.swagger.links.LinkWithNotifs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.tut.escop.mes.service.administrator.ServiceRegistry;
import java.util.HashMap;

/**
 * Class to display details of services as JSON
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class ServiceDetails {
    String id;
    Object links;
    @JsonProperty("class")
    String group;
    
    HashMap<String, Object> children = new HashMap<>();
    int count;
    long lastRun;
    
    @JsonIgnore
    String componentId;

    public ServiceDetails() {
    }

    public ServiceDetails(String id, String group, int count, long lastRun, String componentId) {
        this.id = id;
        this.group = group;
        this.count = count;
        this.lastRun = lastRun;
        this.componentId = componentId;
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

    public void setLinks(Object links) {
        this.links = links;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public HashMap<String, Object> getChildren() {
        return children;
    }

    public void setChildren(HashMap<String, Object> children) {
        this.children = children;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLastRun() {
        return lastRun;
    }

    public void setLastRun(long lastRun) {
        this.lastRun = lastRun;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
    
    /**
     * Function to generate the Links with notifs
     *
     * @param myUrl
     * @param id
     */
    public void createLinkWithNotifs(String myUrl, String id) {
        this.links = new LinkWithNotifs(myUrl + "/" + id, myUrl + "/" + id + "/info", myUrl + "/" + id + "/notifs");
    }
    
    
    public void reg(){
        ServiceRegistry.serviceExecutions.put(id, this);
    }
    
}
