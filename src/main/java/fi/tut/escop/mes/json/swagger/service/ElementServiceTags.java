/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.service;

/**
 * Class to generate the tag object for events and services
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class ElementServiceTags {
    String deviceId;
    String deviceType;

    public ElementServiceTags() {
    }

    public ElementServiceTags(String deviceid, String deviceType) {
        this.deviceId = deviceid;
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
}
