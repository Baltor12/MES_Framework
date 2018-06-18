/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.service.controller;

import fi.tut.escop.mes.json.swagger.SubscriberReplies;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Class to Post messages of services
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class ServiceRESTTemplate {

    private static final Logger LOG = Logger.getLogger(ServiceRESTTemplate.class.getName());

    public ServiceRESTTemplate() {
    }

    /**
     * Function to post value for the service
     *
     * @param response
     * @param uri
     */
    public void serviceValuePOST(SubscriberReplies response, String uri) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        String result = restTemplate.postForObject(uri, response, String.class);
    }

    /**
     * Function to invoke service for MES frame work
     *
     * @param value
     * @param uri
     */
    public void serviceInvokePOST(Object value, String uri) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            String result = restTemplate.postForObject(uri, value, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println(uri + " : " + value);
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
    }

    /**
     * Function which send the REST GET request to to the RPL to get the list of
     * services
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
     * Function which send the REST GET request to to the RPL to ping it alive
     * status the reply from RPL will be pong
     *
     * @param uri
     * @return
     */
    public String phlGET(String uri) {
        String result = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            result = restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
        return result;
    }

    public String queryPOST(String uri) {
        String result = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            result = restTemplate.postForObject(uri, "{}", String.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
        return result;
    }
}
