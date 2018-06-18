 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger;

import fi.tut.escop.mes.json.swagger.service.ServiceSubscriberInputs;
import fi.tut.escop.mes.json.swagger.links.LinkWithParent;
import fi.tut.escop.mes.json.swagger.links.LinkWithNotifs;
import fi.tut.escop.mes.json.swagger.links.LinkWithoutParent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.tut.escop.mes.function.elements.Function;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.events.administrator.EventRegistry;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.json.swagger.event.EventSubscriberInputs;
import fi.tut.escop.mes.service.administrator.ServiceRegistry;
import java.util.HashMap;

/**
 * Class to represent all elements in the simulator in RTU Json Format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class BasicTree {

    String id;
    Object links;
    @JsonProperty("class")
    String group;

    HashMap<String, Object> children = new HashMap<>();

    @JsonIgnore
    String myUrl;

    public BasicTree() {
    }

    public BasicTree(String id, String group) {
        this.id = id;
        this.group = group;
    }

    public BasicTree(String id, String group, String myUrl) {
        this.id = id;
        this.group = group;
        this.myUrl = myUrl;
    }

    public BasicTree(String id, Object links, String group, HashMap<String, Object> children) {
        this.id = id;
        this.links = links;
        this.group = group;
        this.children = children;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(Object links) {
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMyUrl() {
        return myUrl;
    }

    public void setMyUrl(String myUrl) {
        this.myUrl = myUrl;
    }

    /**
     * Function to generate the Links without parent link
     *
     * @param myUrl
     * @param id
     */
    public void createLinkWithoutParent(String myUrl, String id) {
        if (id != "") {
            this.links = new LinkWithoutParent(myUrl + "/" + id, myUrl + "/" + id + "/info");
        } else {
            this.links = new LinkWithoutParent(myUrl, myUrl + "/info");
        }
    }

    /**
     * Function to generate the Links with parent link
     *
     * @param myUrl
     * @param id
     */
    public void createLinkWithParent(String myUrl, String id) {
        this.links = new LinkWithParent(myUrl + "/" + id, myUrl + "/" + id + "/info", myUrl);
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

    /**
     * Function to generate the Children elements of the main RTU
     *
     */
    public void createMainChildren() {
        for (String funcKey : ModuleRegistry.functions.keySet()) {
            Function func = ModuleRegistry.functions.get(funcKey);
            Children childFunc = new Children(func.getId(), "eScopMES", this.myUrl + "/" + func.getId());
            childFunc.createLinkWithoutParent(myUrl, func.getId());
            this.children.put(func.getId(), childFunc);
        }
    }

    /**
     * Function to generate the Children elements for the individual elements of
     * the system
     *
     */
    public void createElementsChildren() {

        //data
        Children data = new Children("data", "data", this.myUrl + "/data");
        data.createLinkWithoutParent(myUrl, "data");
        this.children.put("data", data);

        //notifs
        Children notifs = new Children("notifs", "notifs", this.myUrl + "/notifs");
        notifs.createLinkWithoutParent(myUrl, "notifs");
        this.children.put("notifs", notifs);

        //events
        Children events = new Children("events", "events", this.myUrl + "/events");
        events.createLinkWithoutParent(myUrl, "events");
        this.children.put("events", events);

        //services
        Children services = new Children("services", "services", this.myUrl + "/services");
        services.createLinkWithoutParent(myUrl, "services");
        this.children.put("services", services);

    }

    /**
     * Method to generate the children elements for listing the subscribers for
     * a particular service
     *
     * @param serviceId
     */
    public void createServiceNotifs(String serviceId) {
        ServiceSubscriberInputs subscriber = null;
        //Get the list of subscribers for the current services
        for (String subKey : ServiceRegistry.subscribers.keySet()) {
            subscriber = ServiceRegistry.subscribers.get(subKey);
            if (subscriber.getServiceId().equals(serviceId)) {
                this.children.put(subscriber.getId(), subscriber);
            }
        }
    }

    /**
     * Method to generate the children elements for listing the subscribers for
     * a particular Event
     *
     * @param eventId
     */
    public void createEventNotifs(String eventId) {
        EventSubscriberInputs subscriber = null;
        //Get the list of subscribers for the current event
        for (String subKey : EventRegistry.subscribers.keySet()) {
            subscriber = EventRegistry.subscribers.get(subKey);
            if (subscriber.getEventId().equals(eventId)) {
                this.children.put(subscriber.getId(), subscriber);
            }
        }
    }

    /**
     * Function that creates the children elements for listing the data of each
     * function Data here refers to the inputs (event) of rules in a function
     *
     */
    public void createElementsData() {
        Function func = null;
        func = ModuleRegistry.functions.get(this.id);
        if (func != null) {
            for (Rule rule : func.getRules().values()) {
                for (Input input : rule.getInputs().values()) {
                    if (input.getCategory().equals("event")) {
                        Children child = new Children(input.getId(), "data", this.myUrl + "/" + input.getId());
                        child.createLinkWithoutParent(myUrl, input.getId());
                        this.children.put(input.getId(), child);
                    }
                }
            }
        }
    }
}
