/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.events.controller;

import fi.tut.escop.mes.json.swagger.event.EventPayload;
import fi.tut.escop.mes.json.swagger.event.Registeration;
import fi.tut.escop.mes.json.swagger.event.SubscriberInputs;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Class to access REST methods for the events
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class EventRESTTemplate {

    private static final Logger LOG = Logger.getLogger(EventRESTTemplate.class.getName());

    public EventRESTTemplate() {
    }

    /**
     * Function which send the REST GET request to to the RPL to get the list of
     * events
     *
     * @param uri
     * @return
     */
    public ArrayList<String> rplGET(String uri) {
        ArrayList<String> result = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            result = restTemplate.getForObject(uri, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
        return result;
    }

    /**
     * Function which subscribes to the events
     *
     * @param response
     * @param uri
     * @return
     */
    public SubscriberInputs eventSubscribePOST(Registeration response, String uri) {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(uri);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        SubscriberInputs result = restTemplate.postForObject(uri, response, SubscriberInputs.class);
        return result;
    }

    /**
     * Function that post the payload to the subscribers
     *
     * @param response
     * @param uri
     * @return
     */
    public void sensorValuePOST(EventPayload response, String uri) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            String result = restTemplate.postForObject(uri, response, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
    }
}
