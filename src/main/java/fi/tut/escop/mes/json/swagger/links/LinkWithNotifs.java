/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.links;

/**
 *  Class to create the Json links format with notifs link.
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class LinkWithNotifs {
    String self;
    String info;
    String notifs;

    public LinkWithNotifs() {
    }

    public LinkWithNotifs(String self, String info, String notifs) {
        this.self = self;
        this.info = info;
        this.notifs = notifs;
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

    public String getNotifs() {
        return notifs;
    }

    public void setNotifs(String notifs) {
        this.notifs = notifs;
    }
    
    
}
