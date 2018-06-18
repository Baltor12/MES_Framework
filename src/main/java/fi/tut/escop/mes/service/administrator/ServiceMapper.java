/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.service.administrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.function.controller.FunctionRESTTemplate;
import fi.tut.escop.mes.function.elements.MessageFormat;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.function.elements.Service;
import fi.tut.escop.mes.ontology.OntologyManager;
import fi.tut.escop.mes.service.controller.ServiceRESTTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that has the function of creating service objects as per ontology and
 * mapping them with rules
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class ServiceMapper {

    private static final Logger LOG = Logger.getLogger(ServiceMapper.class.getName());

    public ServiceMapper() {
    }

    /**
     * Method that maps services from ontology with a object created for the
     * same in MES framework
     *
     * @return
     */
    public boolean serviceMapping() {
        boolean reply = false;
        try {
            //Get the rules with the tag Invoke Service
            for (Rule rule : ModuleRegistry.rules.values()) {
                if (rule.getAction().equals("ServiceInvoke")) {
                    HashMap<String, ArrayList<String>> serviceDetails = OntologyManager.queryServiceDetails(rule.getServiceFromOnto());

                    HashMap<String, HashMap<String, String>> meta = OntologyManager.queryMetasforService(rule.getServiceFromOnto());

                    ArrayList<String> serviceUrls = new ArrayList<>();

                    String url = "";

                    // Generate URL for searching the RPL according to input and device type                
                    url = HostPortandConfig.RPL_PROBE_DC + "/service-search?";
                    int i = 0;
                    for (String metaKey : meta.keySet()) {
                        if (i == 0) {
                            url = url + "sensorType=" + serviceDetails.get("?id").get(0) + "&" + meta.get(metaKey).get("metaId") + "=" + meta.get(metaKey).get("metaVal");
                            i = 1;
                        } else {
                            url = url + "&" + meta.get(metaKey).get("metaId") + "=" + meta.get(metaKey).get("metaVal");
                        }
                    }

                    ServiceRESTTemplate restTemplate = new ServiceRESTTemplate();
                    // Search in the RPL and retrive the list of event URLs as string
                    serviceUrls = restTemplate.rplGET(url);
                    if (serviceUrls.isEmpty()) {
                        System.out.println(url);
                        //TODO: Display Error Message in UI
                    } else {
                        for (String serviceUrl : serviceUrls) {
                            String json = "";
                            json = restTemplate.phlGET(serviceUrl);

                            Map<String, Object> map = new HashMap<>();
                            Map<String, String> serviceMeta = new HashMap<>();

                            ObjectMapper mapper = new ObjectMapper();
                            map = mapper.readValue(json,
                                    new TypeReference<HashMap>() {
                                    });
                            serviceMeta = (Map<String, String>) map.get("meta");
                            
                            Service service = null;
                            //Checking if the Input already exists as an object
                            for (String serKey : ServiceRegistry.services.keySet()) {
                                if (ServiceRegistry.services.get(serKey).getId().equals(serviceDetails.get("?id").get(0) + "_" + serviceMeta.get("deviceId"))) {
                                    service = ServiceRegistry.services.get(serKey);
                                }
                            }
                            if (service == null) {
                                service = new Service();
                                service.setId(serviceDetails.get("?id").get(0) + "_" + serviceMeta.get("deviceId"));
                                service.setIdFromOnto(rule.getServiceFromOnto());
                                service.setServiceId(serviceDetails.get("?id").get(0));
                                service.setServiceUrl(serviceUrl);
                                service.setServiceType((String) map.get("class"));
                                service.setMeta(serviceMeta);
                                service.reg();
                                LOG.log(Level.INFO, "registered Service : {0}", service.getId());
                            }
                        }
                    }
                }
            }
            //Map services to rules
            for (Rule rule : ModuleRegistry.rules.values()) {
                for (Service ser : ServiceRegistry.services.values()) {
                    if (ser.getIdFromOnto().equals(rule.getServiceFromOnto())) {
                        rule.setService(ser);
                    }
                }
            }
            reply = true;
        } catch (Exception e) {
            reply = false;
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, ServiceMapper.class.getName());
        }
        return reply;
    }

}
