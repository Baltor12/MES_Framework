/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.links;

/**
 * Class to create the Json links format without parent link.
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class LinkWithoutParent {
    String self;
    String info;

    public LinkWithoutParent() {
    }

    public LinkWithoutParent(String self, String info) {
        this.self = self;
        this.info = info;
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
    
}
