/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.controller;

import fi.tut.escop.mes.discovery.RegisterationMessage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Class to access other device REST methods for functions
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class FunctionRESTTemplate {

    private static final Logger LOG = Logger.getLogger(FunctionRESTTemplate.class.getName());

    public FunctionRESTTemplate() {
    }

    /**
     * Function to post the swagger link to RPL
     *
     * @param response
     * @param uri
     */
    public void probingResponsePOST(RegisterationMessage response, String uri) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RegisterationMessage> message = new HttpEntity<>(response, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            String result = restTemplate.postForObject(uri, message, String.class);
        } catch (Exception e) {
            System.out.println(uri + " : " + response);
            LOG.log(Level.WARNING, "Exception {0}", e);
        }
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
            LOG.log(Level.SEVERE, "Exception {0}", e);
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
            LOG.log(Level.SEVERE, "Exception {0}", e);
        }
        return result;
    }
}
