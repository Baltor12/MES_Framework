/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.service.administrator;

import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.elements.Service;
import fi.tut.escop.mes.service.controller.ServiceRESTTemplate;
import java.util.logging.Logger;

/**
 * Class that handles the service Invocation details
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class ServiceHandler {

    private static final Logger LOG = Logger.getLogger(ServiceHandler.class.getName());

    public ServiceHandler() {
    }

    public void serviceInvocation(Service ser, Object out) {
        ServiceRESTTemplate restTemplate = new ServiceRESTTemplate();
        if (out != null && ser.getServiceUrl() != null) {
            restTemplate.serviceInvokePOST(out, ser.getServiceUrl());
        }
    }
}
