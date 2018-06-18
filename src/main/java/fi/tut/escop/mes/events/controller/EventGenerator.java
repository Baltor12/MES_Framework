/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.events.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.elements.Function;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.events.administrator.EventRegistry;
import fi.tut.escop.mes.function.administrator.RuleTrigger;
import fi.tut.escop.mes.json.swagger.BasicTree;
import fi.tut.escop.mes.json.swagger.EventServiceTree;
import fi.tut.escop.mes.json.swagger.RTUResponse;
import fi.tut.escop.mes.json.swagger.service.ElementServiceTags;
import fi.tut.escop.mes.json.swagger.event.EventPayload;
import fi.tut.escop.mes.json.swagger.event.EventSubscriberInputs;
import fi.tut.escop.mes.json.swagger.event.Payload;
import fi.tut.escop.mes.service.controller.ServiceGenerator;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class that acts as a controller to generate events of the MES frame work
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@RestController
@RequestMapping("/framework")
public class EventGenerator {

    String myUrl = HostPortandConfig.ROOT_URL;
    Function fn = null;
    Rule rul = null;
    Output out = null;
    private static final Logger LOG = Logger.getLogger(ServiceGenerator.class.getName());

    //---------------------------------POST Operations--------------------------
    /**
     * Getting the JSON representation of individual services
     *
     * @param fnId
     * @param eventId
     * @param json
     * @return
     */
    @RequestMapping(value = "/{fnId}/events/{eventId}/notifs", method = RequestMethod.POST)
    public ResponseEntity<EventSubscriberInputs> eventSubscriptionRequests(@PathVariable String fnId, @PathVariable String eventId, @RequestBody String json) {
        ResponseEntity<EventSubscriberInputs> response;
        String basePath = myUrl + "/framework";
        String subscriberId = "";
        String clientdata = "";
        String destUrl = "";
        String sensor = "";
        HashMap<String, String> links = new HashMap<>();
        EventSubscriberInputs subscriber = null;
        fn = null;
        out = null;
        //Initially check whether the function and event(ie.output) exists 
        if (((fn = ModuleRegistry.functions.get(fnId)) != null) && ((out = ModuleRegistry.outputs.get(eventId)) != null)) {
            try {
                Map<String, String> map = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(json,
                        new TypeReference<HashMap<String, String>>() {
                });
                subscriberId = EventRegistry.generateSubscriberID();
                if ((clientdata = map.get("clientData")) == null) {
                    clientdata = "";
                } else {
                    clientdata = map.get("clientData");
                }
                if ((destUrl = map.get("destUrl")) == null) {
                    destUrl = "";
                } else {
                    destUrl = map.get("destUrl");
                }
                links.put("self", basePath + "/" + fnId + "/events/" + eventId + "/notifs/" + subscriberId);
                //Send the response
                Payload payload = null;
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
                payload = new Payload(value, out.getDataType(), delta, out.getQuality());
                subscriber = new EventSubscriberInputs(subscriberId, links, "eventNotification", eventId, destUrl, clientdata, fnId, out.getOutputId());
                subscriber.setCurrent(payload);
                subscriber.regEvent();
                response = new ResponseEntity<>(subscriber, HttpStatus.ACCEPTED);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.log(Level.SEVERE, "Exception " + e);
                response = new ResponseEntity<>(subscriber, HttpStatus.FORBIDDEN);
            }
        } else {
            response = new ResponseEntity<>(subscriber, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @RequestMapping(value = "/{inputId}/notifs", method = RequestMethod.POST)
    public ResponseEntity<String> inputEventReceiver(@PathVariable String inputId, @RequestBody String json) throws IOException {
        ResponseEntity<String> response;
        try {
            HashMap<String, String> payload = null;
            String clientdata = "";
            Input in = null;
            if ((in = ModuleRegistry.inputs.get(inputId)) != null) {
                Map<String, Object> map = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(json,
                        new TypeReference<HashMap>() {
                });
                if ((clientdata = (String) map.get("clientData")) == null) {
                    clientdata = "";
                }
                if ((payload = (HashMap<String, String>) map.get("payload")) != null) {
                    in.setValue(payload.get(in.getMessageFormat().getValueFormat()));
                    in.reg();
                    RuleTrigger trigger = new RuleTrigger();
                    trigger.setInput(in);
                    trigger.start();
                }
                response = new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                response = new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    //---------------------------------GET Operations---------------------------
    /**
     * Getting the JSON representation of list of events in a function
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/events", method = RequestMethod.GET)
    public ResponseEntity<EventServiceTree> getDetailsofFunctionEvents(@PathVariable String fnId) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/framework";
        EventServiceTree elements = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            ElementServiceTags serviceTags = new ElementServiceTags(fnId, "MESFunction");

            // Constructing the Main tree for MES in RTU type
            elements = new EventServiceTree(fnId, "node", basePath + "/" + fnId + "/events", serviceTags);
            elements.createLinkWithParent(basePath + "/" + fnId, "events");

            // Generate the children elements
            elements.createElementEvents();
            response = new ResponseEntity<>(elements, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elements, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of events info in a function
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/events/info", method = RequestMethod.GET)
    public ResponseEntity<EventServiceTree> getDetailsofFunctionEventsInfo(@PathVariable String fnId) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/framework";
        EventServiceTree elements = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            ElementServiceTags serviceTags = new ElementServiceTags(fnId, "MESFunction");

            // Constructing the Main tree for MES in RTU type
            elements = new EventServiceTree(fnId, "node", basePath + "/" + fnId + "/events", serviceTags);
            elements.createLinkWithParent(basePath + "/" + fnId, "events");

            // Generate the children elements
            elements.createElementEvents();
            response = new ResponseEntity<>(elements, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elements, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual events
     *
     * @param fnId
     * @param eventId
     * @return
     */
    @RequestMapping(value = "/{fnId}/events/{eventId}", method = RequestMethod.GET)
    public ResponseEntity<EventPayload> getDetailsofEvents(@PathVariable String fnId, @PathVariable String eventId) {
        ResponseEntity<EventPayload> response;
        String basePath = myUrl + "/framework";
        Payload payload = null;
        EventPayload event = null;
        fn = null;
        out = null;

        //Initially check whether the function and event(ie. output) exists 
        if (((fn = ModuleRegistry.functions.get(fnId)) != null) && ((out = ModuleRegistry.outputs.get(eventId)) != null)) {
            //Get the details of event
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
            payload = new Payload(value, out.getDataType(), delta, out.getQuality());
            event = new EventPayload(eventId, out.getRuleId(), fnId, payload, out.getMeta(), "");
            response = new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(event, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual events Info
     *
     * @param fnId
     * @param eventId
     * @return
     */
    @RequestMapping(value = "/{fnId}/events/{eventId}/info", method = RequestMethod.GET)
    public ResponseEntity<EventPayload> getDetailsofEventsInfo(@PathVariable String fnId, @PathVariable String eventId) {
        ResponseEntity<EventPayload> response;
        String basePath = myUrl + "/framework";
        Payload payload = null;
        EventPayload event = null;
        fn = null;
        out = null;

        //Initially check whether the function and event(ie. output) exists 
        if (((fn = ModuleRegistry.functions.get(fnId)) != null) && ((out = ModuleRegistry.outputs.get(eventId)) != null)) {
            //Get the details of event
            payload = new Payload((String) out.getValue(), out.getDataType());
            event = new EventPayload(eventId, out.getRuleId(), fnId, payload, out.getMeta(), "");
            response = new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(event, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual events notifs
     *
     * @param fnId
     * @param eventId
     * @return
     */
    @RequestMapping(value = "/{fnId}/events/{eventId}/notifs", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getDetailsofEventsNotifs(@PathVariable String fnId, @PathVariable String eventId) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework";
        BasicTree event = null;
        fn = null;
        out = null;

        //Initially check whether the function and event exists (ie.output)
        if (((fn = ModuleRegistry.functions.get(fnId)) != null) && ((out = ModuleRegistry.outputs.get(eventId)) != null)) {
            event = new BasicTree(eventId, "notifs", basePath + "/" + fnId + "/events");
            event.createLinkWithNotifs(basePath + "/" + fnId + "/events", eventId);
            event.createEventNotifs(eventId);
            response = new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(event, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual events notifs
     *
     * @param fnId
     * @param eventId
     * @return
     */
    @RequestMapping(value = "/{fnId}/events/{eventId}/notifs/{notId}", method = RequestMethod.GET)
    public ResponseEntity<EventSubscriberInputs> getDetailsofEventsNotifsID(@PathVariable String fnId, @PathVariable String eventId, @PathVariable String notId) {
        ResponseEntity<EventSubscriberInputs> response;
        EventSubscriberInputs subscriber = null;
        //Initially check whether the function and event exists (ie.output)
        if ((subscriber = EventRegistry.subscribers.get(notId)) != null) {
            response = new ResponseEntity<>(subscriber, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(subscriber, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //-----------------------------------DELETE Operation-----------------------\
    /**
     * Method to delete the notification Subscriber
     *
     * @param notId
     * @return
     */
    @RequestMapping(value = "/{fnId}/events/{eventId}/notifs/{notId}", method = RequestMethod.DELETE)
    public ResponseEntity<RTUResponse> deleteSenIdEventsNotifs(@PathVariable String notId
    ) {
        ResponseEntity<RTUResponse> response;
        EventSubscriberInputs subscriber = null;
        RTUResponse notifier = null;
        if ((subscriber = EventRegistry.subscribers.get(notId)) != null) {
            EventRegistry.subscribers.remove(notId);
            notifier = new RTUResponse("202", "accepted", "Services is Accepted", "");
            response = new ResponseEntity<>(notifier, HttpStatus.ACCEPTED);
        } else {
            notifier = new RTUResponse("404", "Not_found", "Services is not available", "");
            response = new ResponseEntity<>(notifier, HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
