/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.administrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.events.controller.EventRESTTemplate;
import fi.tut.escop.mes.function.controller.FunctionRESTTemplate;
import fi.tut.escop.mes.function.elements.Device;
import fi.tut.escop.mes.ontology.OntologyManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that registers the devices which helps in the MES framework
 * construction.
 *
 * @author Balaji Gopalakrisnan (TUT)
 */
public class DeviceRegisteration {

    private static final Logger LOG = Logger.getLogger(DeviceRegisteration.class.getName());

    public DeviceRegisteration() {
    }

    public boolean RegisterDevice() {
        boolean reply = false;
        try {
            // Query list of Devices 
            ArrayList<String> devices = new ArrayList<>();
            devices = OntologyManager.queryMESDevices();
            System.out.println(devices);
            for (String device : devices) {
                String url = "";
                ArrayList<String> deviceUrls = new ArrayList<>();
                url = HostPortandConfig.RPL_PROBE_DC + "/device-search?deviceType=" + device;
                FunctionRESTTemplate restTemplate = new FunctionRESTTemplate();
                deviceUrls = restTemplate.rplGET(url);
                for (String deviceUrl : deviceUrls) {
                    FunctionRESTTemplate devTemplate = new FunctionRESTTemplate();
                    String json = "";
                    json = devTemplate.phlGET(deviceUrl);
                    if (!json.equals("")) {
                        Map<String, Object> map = new HashMap<>();
                        Map<String, Object> meta = new HashMap<>();
                        Device dev = new Device();
                        ObjectMapper mapper = new ObjectMapper();
                        map = mapper.readValue(json,
                                new TypeReference<HashMap>() {
                                });
                        meta = (Map<String, Object>) map.get("meta");
                        if ((meta.get("deviceId") != null)) {
                            dev.setId((String) meta.get("deviceId"));
                            if (meta.get("deviceType") != null) {
                                dev.setDeviceType((String) meta.get("deviceType"));
                            }
                            if (meta.get("parentId") != null) {
                                dev.setParentId((String) meta.get("parentId"));
                            }
                            if (meta.get("parentType") != null) {
                                dev.setParentType((String) meta.get("parentType"));
                            }
                            if (meta.get("children") != null) {
                                dev.setChildren((ArrayList<HashMap<String, String>>) meta.get("children"));
                            }
                            dev.reg();
                            LOG.log(Level.INFO, "registered Device : {0}", dev.getId());
                        } else if ((meta.get("id") != null)) {
                            dev.setId((String) meta.get("id"));
                            if (meta.get("deviceType") != null) {
                                dev.setDeviceType((String) meta.get("deviceType"));
                            }
                            if (meta.get("parentId") != null) {
                                dev.setParentId((String) meta.get("parentId"));
                            }
                            if (meta.get("parentType") != null) {
                                dev.setParentType((String) meta.get("parentType"));
                            }
                            if (meta.get("children") != null) {
                                dev.setChildren((ArrayList<HashMap<String, String>>) meta.get("children"));
                            }
                            dev.reg();
                            LOG.log(Level.INFO, "registered Device : {0}", dev.getId());
                        }
                    }
                }
            }
            // map the childrens also to the devices if they do not exist
            HashMap<String, Device> registeredDevice = new HashMap<String, Device>(ModuleRegistry.devices);
            for (Device dev : registeredDevice.values()) {
                for (HashMap<String, String> childHash : dev.getChildren()) {
                    if (childHash.get("id") != null && childHash.get("type") != null) {
                        if (!ModuleRegistry.devices.containsKey(childHash.get("id"))) {
                            Device childDev = new Device();
                            childDev.setId(childHash.get("id"));
                            childDev.setDeviceType(childHash.get("type"));
                            childDev.setParentId(dev.getId());
                            childDev.setParentType(dev.getDeviceType());
                            childDev.reg();
//                            System.out.println(childDev.getId() + " : " + childDev.getDeviceType() + " : " + childDev.getParentId() + " : " + childDev.getParentType());
                        }
                    }
                }
            }
            reply = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, DeviceRegisteration.class.getName());
            reply = false;
        }
        return reply;
    }

}
