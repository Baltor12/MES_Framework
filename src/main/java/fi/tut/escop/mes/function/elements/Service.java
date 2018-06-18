/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.elements;

import fi.tut.escop.mes.service.administrator.ServiceRegistry;

/**
 * Class that used to create the service objects for services to be invoked 
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class Service {
    String id;
    String serviceId;
    String idFromOnto;
    String serviceUrl;
    Object meta;
    String serviceType;

    public Service() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getIdFromOnto() {
        return idFromOnto;
    }

    public void setIdFromOnto(String idFromOnto) {
        this.idFromOnto = idFromOnto;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    
    public void reg(){
        ServiceRegistry.services.put(this.id, this);
    }
}
