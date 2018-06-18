/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger;

import fi.tut.escop.mes.json.swagger.links.LinkWithParent;
import fi.tut.escop.mes.json.swagger.links.LinkWithNotifs;
import fi.tut.escop.mes.json.swagger.links.LinkWithoutParent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to generate the swagger JSON representation for children elements with events and services.
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class ChildrenWithTags {
    String id;
    Object meta;
    Object links;
    @JsonProperty("class")
    String group;

    @JsonIgnore
    String myUrl;

    public ChildrenWithTags() {
    }

    public ChildrenWithTags(String id, String group) {
        this.id = id;
        this.group = group;
    }

    public ChildrenWithTags(String id, Object meta, String group, String myUrl) {
        this.id = id;
        this.meta = meta;
        this.group = group;
        this.myUrl = myUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
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

    public String getMyUrl() {
        return myUrl;
    }

    public void setMyUrl(String myUrl) {
        this.myUrl = myUrl;
    }
    
    public void createLinkWithoutParent(String myUrl, String id) {
        this.links = new LinkWithoutParent(myUrl + "/" + id, myUrl + "/" + id + "/info");
    }

    public void createLinkWithParent(String myUrl, String id) {
        this.links = new LinkWithParent(myUrl + "/" + id, myUrl + "/" + id + "/info", myUrl);
    }

    public void createLinkWithNotifs(String myUrl, String id) {
        this.links = new LinkWithNotifs(myUrl + "/" + id, myUrl + "/" + id + "/info", myUrl + "/" + id + "/notifs");
    }
    
    
    
}
