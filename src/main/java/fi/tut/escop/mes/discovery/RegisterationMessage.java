/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.discovery;

/**
 * Class to form the JSON format of the registeration message to be sent to the probing device
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class RegisterationMessage {
    String url;
    String id;
    Integer cnt;

    public RegisterationMessage() {
    }

    public RegisterationMessage(String url, String id, Integer cnt) {
        this.url = url;
        this.id = id;
        this.cnt = cnt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }
    
}
