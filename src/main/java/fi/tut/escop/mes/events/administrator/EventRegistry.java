/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.events.administrator;

import fi.tut.escop.mes.json.swagger.event.EventSubscriberInputs;
import fi.tut.escop.mes.json.swagger.service.ServiceSubscriberInputs;
import fi.tut.escop.mes.json.swagger.service.ServiceDetails;
import java.util.HashMap;

/**
 * Class that acts as a registry to store the details variables with respect to events
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class EventRegistry {
    public static long subscriberID = 0;

    //Registry for Event Subscribers
    public static HashMap<String, EventSubscriberInputs> subscribers = new HashMap<>();

    /**
     * Function that generates id for the devices subscribed to Event
     * notifications
     *
     * @return
     */
    public static String generateSubscriberID() {
        return "eve_" + subscriberID++;
    }
}
