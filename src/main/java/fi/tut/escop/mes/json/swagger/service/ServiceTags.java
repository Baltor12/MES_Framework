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
public class ServiceTags {
    String deviceId;
    String deviceType;
    String actuatorId;
    String serviceType;

    public ServiceTags() {
    }

    public ServiceTags(String deviceid, String deviceType,String actuatorId, String serviceType) {
        this.deviceId = deviceid;
        this.deviceType = deviceType;
        this.serviceType = serviceType;
        this.actuatorId = actuatorId;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getActuatorId() {
        return actuatorId;
    }

    public void setActuatorId(String actuatorId) {
        this.actuatorId = actuatorId;
    }
    
}
