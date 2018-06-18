/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.elements.Function;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.events.administrator.EventRegistry;
import fi.tut.escop.mes.extra.DateSimulator;
import fi.tut.escop.mes.function.administrator.RuleExecutor;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.json.swagger.BasicTree;
import fi.tut.escop.mes.json.swagger.service.ElementServiceTags;
import fi.tut.escop.mes.json.swagger.EventServiceTree;
import fi.tut.escop.mes.json.swagger.RTUResponse;
import fi.tut.escop.mes.json.swagger.service.MESServiceResponse;
import fi.tut.escop.mes.json.swagger.service.ServiceDetails;
import fi.tut.escop.mes.json.swagger.service.ServicePayload;
import fi.tut.escop.mes.json.swagger.service.ServiceSubscriberInputs;
import fi.tut.escop.mes.json.swagger.SubscriberReplies;
import fi.tut.escop.mes.service.administrator.ServiceRegistry;
import java.io.IOException;
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
 * Class that generates services for functions and rules in the functions
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@RestController
@RequestMapping("/framework")
public class ServiceGenerator {

    String myUrl = HostPortandConfig.ROOT_URL;
    Function fn = null;
    Rule rul = null;
    Date date = new Date();
    private static final Logger LOG = Logger.getLogger(ServiceGenerator.class.getName());

    //---------------------------------POST Operations--------------------------
    /**
     * Getting the JSON representation of individual services
     *
     * @param fnId
     * @param serId
     * @param json
     * @return
     */
    @RequestMapping(value = "/{fnId}/services/{serId}", method = RequestMethod.POST)
    public ResponseEntity<Object> serviceInvokeRequests(@PathVariable String fnId, @PathVariable String serId, @RequestBody String json) throws IOException {
        ResponseEntity<Object> response;
        String basePath = myUrl + "/framework";
        ServiceDetails service = null;
        MESServiceResponse serResponse = null;
        ServicePayload serPayload = null;
        SubscriberReplies subReply = null;
        ServiceSubscriberInputs subInputs = null;
        fn = null;
        rul = null;
        //Initially check whether the function and service exists 
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            for (String ruleKey : fn.getRules().keySet()) {
                if (serId.equals(fn.getRules().get(ruleKey).getServiceId())) {
                    rul = fn.getRules().get(ruleKey);
                }
            }
            if ((rul != null) || (fn.getServiceId().equals(serId))) {

                // Check whether the service already had been invoked
                // if yes, increase count of the service detail and save it to registry
                // if No, create new service detail and save it to registry
                if ((service = ServiceRegistry.serviceExecutions.get(serId)) != null) {
                    service.setCount(service.getCount() + 1);
                    service.setLastRun(date.getTime());
                    service.reg();
                } else {
                    service = new ServiceDetails(serId, "operation", 1,date.getTime(), fnId);
                    service.createLinkWithNotifs(basePath + "/" + fnId + "/services", serId);
                    service.reg();
                }

                //Send notification to the service subscribers 
                serPayload = new ServicePayload(service.getCount(), service.getLastRun());
                for (String subKey : ServiceRegistry.subscribers.keySet()) {
                    subInputs = null;
                    subInputs = ServiceRegistry.subscribers.get(subKey);
                    if ((subInputs.getServiceId().equals(serId)) && (subInputs.getComponentId().equals(fnId))) {
                        subReply = null;
                        subReply = new SubscriberReplies(subInputs.getId(), serId, date.getTime(), subInputs, subInputs.getClientData());
                        ServiceRESTTemplate serTemp = new ServiceRESTTemplate();
                        serTemp.serviceValuePOST(subReply, subInputs.getDestUrl());
                    }
                }
                //Convert json to Hash Map of Object 
                Map<Object, Object> map = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(json,
                        new TypeReference<HashMap>() {
                        });

                // For the time being only the service with the query will be provided 
                // Hence only those will be invoked
                // First parse the json
                //TODO: Determine if there requires an payload for the message
                for (Input in : rul.getInputs().values()) {
                    if (in.getCategory().equals("serviceInput")) {
                        in.setValue(map);
                    }
                }
                rul.reg();
                RuleExecutor ruleExecute = new RuleExecutor();
                try {
                    Output out = ruleExecute.serviceExec(rul);
                    response = new ResponseEntity<>(out.getValue(), HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    serResponse = new MESServiceResponse("500", "Internal_Server_Error", "Internal_Server_Error", "");
                    response = new ResponseEntity<>(serResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                serResponse = new MESServiceResponse("404", "Not_found", "Services is not available", "");
                response = new ResponseEntity<>(serResponse, HttpStatus.NOT_FOUND);
            }
        } else {
            serResponse = new MESServiceResponse("404", "Not_found", "Services is not available", "");
            response = new ResponseEntity<>(serResponse, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual services
     *
     * @param fnId
     * @param serId
     * @return
     */
    @RequestMapping(value = "/{fnId}/services/{serId}/notifs", method = RequestMethod.POST)
    public ResponseEntity<ServiceSubscriberInputs> serviceSubscriptionRequests(@PathVariable String fnId, @PathVariable String serId, @RequestBody String json) {
        ResponseEntity<ServiceSubscriberInputs> response;
        String basePath = myUrl + "/framework";
        String subscriberId = "";
        String clientdata = "";
        String destUrl = "";
        String sensor = "";
        HashMap<String, String> links = new HashMap<>();
        ServiceSubscriberInputs subscriber = null;
        fn = null;
        rul = null;

        //Initially check whether the function and service exists 
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            for (String ruleKey : fn.getRules().keySet()) {
                if (serId.equals(fn.getRules().get(ruleKey).getServiceId())) {
                    rul = fn.getRules().get(ruleKey);
                }
            }
            if ((rul != null) || (fn.getServiceId().equals(serId))) {
                try {
                    Map<String, String> map = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    map = mapper.readValue(json,
                            new TypeReference<HashMap<String, String>>() {
                            });
                    subscriberId = ServiceRegistry.generateSubscriberID();
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
                    links.put("self", basePath + "/" + fnId + "/services/" + serId + "/notifs/" + subscriberId);
                    // find whether it is rule or function and assign the sensor 'rule/function id'
                    if (rul != null) {
                        sensor = rul.getId();
                    } else {
                        sensor = fn.getId();
                    }
                    //Send the response
                    subscriber = new ServiceSubscriberInputs(subscriberId, links, "serviceNotification", serId, destUrl, clientdata, fnId, sensor);
                    subscriber.regService();
                    response = new ResponseEntity<>(subscriber, HttpStatus.ACCEPTED);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.log(Level.SEVERE, "Exception " + e);
                    response = new ResponseEntity<>(subscriber, HttpStatus.FORBIDDEN);
                }
            } else {
                response = new ResponseEntity<>(subscriber, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(subscriber, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //---------------------------------GET Operations---------------------------
    /**
     * Getting the JSON representation of list of services in a function
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/services", method = RequestMethod.GET)
    public ResponseEntity<EventServiceTree> getDetailsofFunctionService(@PathVariable String fnId) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/framework";
        EventServiceTree elements = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            ElementServiceTags serviceTags = new ElementServiceTags(fnId, "MESFunction");

            // Constructing the Main tree for MES in RTU type
            elements = new EventServiceTree(fnId, "node", basePath + "/" + fnId + "/services", serviceTags);
            elements.createLinkWithParent(basePath + "/" + fnId, "services");

            // generate the children elements
            elements.createElementServices();
            response = new ResponseEntity<>(elements, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elements, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of services info in a function
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/services/info", method = RequestMethod.GET)
    public ResponseEntity<EventServiceTree> getDetailsofFunctionServiceInfo(@PathVariable String fnId) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/framework";
        EventServiceTree elements = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            ElementServiceTags serviceTags = new ElementServiceTags(fnId, "MESFunction");

            // Constructing the Main tree for MES in RTU type
            elements = new EventServiceTree(fnId, "node", basePath + "/" + fnId + "/services", serviceTags);
            elements.createLinkWithParent(basePath + "/" + fnId, "services");

            // Generate the children elements
            elements.createElementServices();

            response = new ResponseEntity<>(elements, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elements, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual services
     *
     * @param fnId
     * @param serId
     * @return
     */
    @RequestMapping(value = "/{fnId}/services/{serId}", method = RequestMethod.GET)
    public ResponseEntity<ServiceDetails> getDetailsofService(@PathVariable String fnId, @PathVariable String serId) {
        ResponseEntity<ServiceDetails> response;
        String basePath = myUrl + "/framework";
        ServiceDetails service = null;
        fn = null;
        rul = null;

        //Initially check whether the function and service exists 
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            for (String ruleKey : fn.getRules().keySet()) {
                if (serId.equals(fn.getRules().get(ruleKey).getServiceId())) {
                    rul = fn.getRules().get(ruleKey);
                }
            }
            if ((rul != null) || (fn.getServiceId().equals(serId))) {
                //Get the service execution details from the registry
                service = ServiceRegistry.serviceExecutions.get(serId);
                if (service == null) {
                    service = new ServiceDetails(serId, "process", 0, 0, fnId);
                    service.createLinkWithNotifs(basePath + "/" + fnId + "/services", serId);
                    service.reg();
                }
                response = new ResponseEntity<>(service, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual services Info
     *
     * @param fnId
     * @param serId
     * @return
     */
    @RequestMapping(value = "/{fnId}/services/{serId}/info", method = RequestMethod.GET)
    public ResponseEntity<ServiceDetails> getDetailsofServiceInfo(@PathVariable String fnId, @PathVariable String serId) {
        ResponseEntity<ServiceDetails> response;
        String basePath = myUrl + "/framework";
        ServiceDetails service = null;
        fn = null;
        rul = null;

        //Initially check whether the function and service exists 
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            for (String ruleKey : fn.getRules().keySet()) {
                if (serId.equals(fn.getRules().get(ruleKey).getServiceId())) {
                    rul = fn.getRules().get(ruleKey);
                }
            }
            if ((rul != null) || (fn.getServiceId().equals(serId))) {
                //Get the service execution details from the registry
                service = ServiceRegistry.serviceExecutions.get(serId);
                if (service == null) {
                    service = new ServiceDetails(serId, "process", 0, 0, fnId);
                    service.createLinkWithNotifs(basePath + "/" + fnId + "/services", serId);
                    service.reg();
                }
                response = new ResponseEntity<>(service, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of individual services notifs
     *
     * @param fnId
     * @param serId
     * @return
     */
    @RequestMapping(value = "/{fnId}/services/{serId}/notifs", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getDetailsofServiceNotifs(@PathVariable String fnId, @PathVariable String serId) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework";
        BasicTree service = null;
        fn = null;
        rul = null;

        //Initially check whether the function and service exists 
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            for (String ruleKey : fn.getRules().keySet()) {
                if (serId.equals(fn.getRules().get(ruleKey).getServiceId())) {
                    rul = fn.getRules().get(ruleKey);
                }
            }
            if ((rul != null) || (fn.getServiceId().equals(serId))) {
                service = new BasicTree(serId, "notifs", basePath + "/" + fnId + "/services");
                service.createLinkWithNotifs(basePath + "/" + fnId + "/services", serId);
                service.createServiceNotifs(serId);
                response = new ResponseEntity<>(service, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
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
    @RequestMapping(value = "/{fnId}/services/{serId}/notifs/{notId}", method = RequestMethod.DELETE)
    public ResponseEntity<RTUResponse> deleteSenIdEventsNotifs(@PathVariable String notId
    ) {
        ResponseEntity<RTUResponse> response;
        ServiceSubscriberInputs subscriber = null;
        RTUResponse notifier = null;
        if ((subscriber = ServiceRegistry.subscribers.get(notId)) != null) {
            ServiceRegistry.subscribers.remove(notId);
            notifier = new RTUResponse("202", "accepted", "Services is Accepted", "");
            response = new ResponseEntity<>(notifier, HttpStatus.ACCEPTED);
        } else {
            notifier = new RTUResponse("404", "Not_found", "Services is not available", "");
            response = new ResponseEntity<>(notifier, HttpStatus.NOT_FOUND);
        }
        return response;
    }
}
