/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.controller;

import fi.tut.escop.mes.constants.HostPortandConfig;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class that acts as a REST controller for framework based RESt services
 *
 * @author Balaji Gopalakrishnan
 */
@RestController
@RequestMapping("/framework")
public class FrameworkController {

    String myUrl = HostPortandConfig.ROOT_URL;
    private static final Logger LOG = Logger.getLogger(FrameworkController.class.getName());

    /**
     * Function to Get the message format for the event messages
     *
     * @return
     */
    @RequestMapping(value = "/message/event", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getMessageFormats() {
        ResponseEntity<HashMap<String, Object>> response;
        HashMap<String, Object> meta = new HashMap<>();
        try {
            meta.put("valueFormat", "value");
            meta.put("stateFormat", "state");
            meta.put("dataTypeFormat", "type");
            meta.put("deltaFormat", "delta");
            meta.put("timeFormat", "time");
            response = new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, "");
            response = new ResponseEntity<>(meta, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
