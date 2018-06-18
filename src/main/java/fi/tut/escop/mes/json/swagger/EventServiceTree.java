/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger;

import fi.tut.escop.mes.json.swagger.service.ElementServiceTags;
import fi.tut.escop.mes.json.swagger.service.ServiceTags;
import fi.tut.escop.mes.json.swagger.links.LinkWithParent;
import fi.tut.escop.mes.json.swagger.links.LinkWithNotifs;
import fi.tut.escop.mes.json.swagger.links.LinkWithoutParent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.tut.escop.mes.function.elements.Function;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.function.elements.Rule;
import java.util.HashMap;

/**
 * Class to represent events and services in the simulator in RTU Json Format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class EventServiceTree {

    String id;
    Object links;
    ElementServiceTags meta;
    @JsonProperty("class")
    String group;

    HashMap<String, Object> children = new HashMap<>();

    @JsonIgnore
    String myUrl;
    @JsonIgnore
    Function fn = null;
    @JsonIgnore
    Rule rul = null;
    @JsonIgnore
    Output output = null;

    public EventServiceTree() {
    }

    public EventServiceTree(String id, String group) {
        this.id = id;
        this.group = group;
    }

    public EventServiceTree(String id, String group, String myUrl, ElementServiceTags meta) {
        this.id = id;
        this.group = group;
        this.myUrl = myUrl;
        this.meta = meta;
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

    public ElementServiceTags getMeta() {
        return meta;
    }

    public void setMeta(ElementServiceTags meta) {
        this.meta = meta;
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
     * Function to generate services for functions
     *
     */
    public void createElementServices() {
        fn = null;
        if ((fn = ModuleRegistry.functions.get(id)) != null) {
            //Rule Service
            for (String ruleKey : fn.getRules().keySet()) {
                rul = null;
                rul = fn.getRules().get(ruleKey);
                if (rul.getRuleServiceType().equals("query")) {
                    ServiceTags ruleTag = new ServiceTags(id, "MESFunction", rul.getId(), "Rule");
                    ChildrenWithTags rule = new ChildrenWithTags(rul.getServiceId(), ruleTag, rul.getRuleServiceType(), this.myUrl + "/" + rul.getServiceId());
                    rule.createLinkWithNotifs(myUrl, rul.getServiceId());
                    this.children.put(rul.getServiceId(), rule);
                }
            }

        }
    }

    /**
     * Function to generate events for functions
     *
     */
    public void createElementEvents() {
        fn = null;
        if ((fn = ModuleRegistry.functions.get(id)) != null) {
            for (String ruleKey : fn.getRules().keySet()) {
                rul = null;
                rul = fn.getRules().get(ruleKey);
                // Check if the rule generates an Event
                //TODO: try to globalize
                if (rul.getAction().equals("Event")) {
                    //Get the list of outputs in each rule to have the events
                    for (String outListKey : rul.getOutputList()) {
                        // match the outputs with the list of outputs to generate events
                        for (String outKey : ModuleRegistry.outputs.keySet()) {
                            output = null;
                            output = ModuleRegistry.outputs.get(outKey);
                            if (output.getIdfrmOnto().equals(outListKey)) {
                                ChildrenWithTags func = new ChildrenWithTags(output.getId(), output.getMeta(), "event", this.myUrl + "/" + output.getId());
                                func.createLinkWithNotifs(myUrl, output.getId());
                                this.children.put(output.getId(), func);
                            }
                        }
                    }
                }
            }
        }
    }
}
