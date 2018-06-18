/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.events.administrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.mes.events.controller.EventRESTTemplate;
import fi.tut.escop.mes.function.administrator.RuleTriggerRepeated;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.json.swagger.event.EventPayload;
import fi.tut.escop.mes.json.swagger.event.EventSubscriberInputs;
import fi.tut.escop.mes.json.swagger.event.Payload;
import fi.tut.escop.mes.json.swagger.event.SubscriberInputs;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that handles all the function with respect to posting events
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class EventAdministrator {

    private static final Logger LOG = Logger.getLogger(EventAdministrator.class.getName());

    public EventAdministrator() {
    }

    /**
     * Method that handles the function of posting the sensor values to events
     *
     * @param out
     * @param rule
     */
    public void postEvent(Output out, Rule rule) {
        try {
            HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs>(EventRegistry.subscribers);;
            for (EventSubscriberInputs subscriber : subscriberList.values()) {
                if (subscriber.getEventId().equals(out.getId())) {
                    Object value;
                    Object delta;
                    DecimalFormat f = new DecimalFormat("0.##E0");
                    if (out.getDataType().equals("double") && (out.getValue() != null) && (out.getDelta() != null)) {
                        value = f.format(Double.parseDouble(out.getValue().toString()));
                        delta = f.format(Double.parseDouble(out.getDelta().toString()));
                    } else {
                        value = out.getValue();
                        delta = out.getDelta();
                    }
                    Payload payload = new Payload(value, out.getDataType(), out.getState(), out.getQuality(), delta);
                    EventPayload eventPayload = new EventPayload(out.getId(), out.getRuleId(), rule.getFunctionId(), payload, out.getMeta(), subscriber.getClientData());
                    EventRESTTemplate restTemplate = new EventRESTTemplate();
                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
    }
}
