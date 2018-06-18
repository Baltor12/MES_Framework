/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.event;

/**
 * Class to generate the JSON for registering to events 
 *
 * @author Balaji Gopalakrishnna (TUT)
 */
public class Registeration {
    
    String destUrl;
    String clientData;

    public Registeration(String destUrl, String clientData) {
        this.destUrl = destUrl;
        this.clientData = clientData;
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
    
    
}
