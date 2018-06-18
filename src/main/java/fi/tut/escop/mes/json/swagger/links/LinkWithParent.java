/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.links;

/**
 * Class to create the Json links format with parent link.
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class LinkWithParent {
    
    String self;
    String info;
    String parent;
    public LinkWithParent() {
    }

    public LinkWithParent(String self, String info, String parent) {
        this.self = self;
        this.info = info;
        this.parent = parent;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    
}
