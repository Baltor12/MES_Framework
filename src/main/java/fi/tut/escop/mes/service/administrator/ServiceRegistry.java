/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.service.administrator;

import fi.tut.escop.mes.function.elements.Service;
import fi.tut.escop.mes.json.swagger.service.ServiceDetails;
import fi.tut.escop.mes.json.swagger.service.ServiceSubscriberInputs;
import java.util.HashMap;

/**
 * Registry class for holding the details of services both generated and used by
 * MES
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class ServiceRegistry {

    public static long subscriberID = 0;

    //registry for Servie Invocation Details
    public static HashMap<String, ServiceDetails> serviceExecutions = new HashMap<>();
    
    //registry for Servie Details
    public static HashMap<String, Service> services = new HashMap<>();

    //registry for Service Subscribers
    public static HashMap<String, ServiceSubscriberInputs> subscribers = new HashMap<>();

    /**
     * Function that generates id for the devices subscribed to Service
     * notifications
     *
     * @return
     */
    public static String generateSubscriberID() {
        return "ser_" + subscriberID++;
    }
}
