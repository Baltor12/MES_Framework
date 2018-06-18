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
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.MessageFormat;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.json.swagger.event.Registeration;
import fi.tut.escop.mes.json.swagger.event.SubscriberInputs;
import fi.tut.escop.mes.ontology.OntologyDataManager;
import fi.tut.escop.mes.ontology.OntologyManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that does the function of mapping inputs from the ontology by creating
 * objects for the same
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class InputMapper {

    private static final Logger LOG = Logger.getLogger(InputMapper.class.getName());

    public InputMapper() {
    }

    /**
     * Method that maps the inputs from ontology with a object created for the
     * same in MES framework
     *
     * @return
     */
    public boolean inputMapping() {
        boolean reply = false;
        try {
            
            ArrayList<String> inputList = new ArrayList<>();

            // Get the list of inputs from the ontology
            inputList = OntologyManager.queryInputs();

            for (String inputKey : inputList) {

                HashMap<String, ArrayList<String>> inputDetails = OntologyManager.queryInputDetails(inputKey);

                HashMap<String, HashMap<String, String>> meta = OntologyManager.queryMetasforInput(inputKey);

                // Check if the input category is event or service(query and process) and then query the events from RPL
                if ((inputDetails.get("?category").get(0).equals("event")) || (inputDetails.get("?category").get(0).equals("query")) || (inputDetails.get("?category").get(0).equals("process"))) {
                    ArrayList<String> eventUrls = new ArrayList<>();
                    String url = "";
                    String deviceType = "";
                    //If it is url then no need to have event/service discovery
                    if (inputDetails.get("?url").get(0).isEmpty()) {
                        // Getting the device type of the particular input and getting the events for it from the PHL layer
                        if (!HostPortandConfig.RPL_PROBE_DC.equals("")) {
                            url = HostPortandConfig.RPL_PROBE_DC;
                            // Generate URL for searching the RPL according to input and device type
                            int i = 0;
                            for (String metaKey : meta.keySet()) {
                                if (i == 0) {
                                    if (inputDetails.get("?category").get(0).equals("event")) {
                                        url = url + "/event-search?";
                                    } else {
                                        url = url + "/service-search?";
                                    }
                                    url = url + meta.get(metaKey).get("metaId") + "=" + meta.get(metaKey).get("metaVal");
                                    i = 1;
                                } else {
                                    url = url + "&" + meta.get(metaKey).get("metaId") + "=" + meta.get(metaKey).get("metaVal");
                                }
                                if (meta.get(metaKey).get("metaId").equals("deviceType")) {
                                    deviceType = meta.get(metaKey).get("metaVal");
                                }
                            }
                        }
                        EventRESTTemplate restTemplate = new EventRESTTemplate();
                        // Search in the RPL and retrive the list of event URLs as string
                        eventUrls = restTemplate.rplGET(url);
//                            System.out.println(eventUrls);
                        if (eventUrls.isEmpty()) {
                            System.out.println(url);
                            // TODO :  Get these in UI
                        }
                    } else {
                        eventUrls.add(inputDetails.get("?url").get(0));
                    }
                    // check if the events discovery is empty
                    //if empty just create the input without subscribing to URLs
                    if (eventUrls.isEmpty()) {
                        MessageFormat messageFormat = null;
                        if (inputDetails.get("?category").get(0).equals("event")) {
                            messageFormat = (MessageFormat) ModuleRegistry.messageFormats.get("eventDefault");
                        } else {
                            messageFormat = (MessageFormat) ModuleRegistry.messageFormats.get("serviceDefault");
                        }

                        Input in = null;
                        //Checking if the Input already exists as an object
                        for (String inKey : ModuleRegistry.inputs.keySet()) {
                            if (inputDetails.get("?category").get(0).equals("event")) {
                                if (ModuleRegistry.inputs.get(inKey).getId().equals(inputDetails.get("?id").get(0))) {
                                    in = ModuleRegistry.inputs.get(inKey);
                                }
                            } else {
                                if (ModuleRegistry.inputs.get(inKey).getId().equals("ser_" + inputDetails.get("?id").get(0))) {
                                    in = ModuleRegistry.inputs.get(inKey);
                                }
                            }
                        }
                        if (in == null) {
                            //Generate input for each device id in the rule
                            in = new Input();
                            String idDevice = "";
                            if (inputDetails.get("?category").get(0).equals("event")) {
                                in.setId(inputDetails.get("?id").get(0));
                                in.setDeviceId(idDevice);
                                in.setDeviceType(deviceType);
                            } else {
                                in.setId("ser_" + inputDetails.get("?id").get(0));
                                in.setDeviceId(idDevice);
                                in.setDeviceType(deviceType);
                            }
                            in.setInputId(inputDetails.get("?id").get(0));
                            in.setMessageFormat(messageFormat);
                            if (!(inputDetails.get("?formulaId").isEmpty()) && !(inputDetails.get("?formulaId").get(0).equals(""))) {
                                in.setFormulaId(inputDetails.get("?formulaId").get(0));
                            } else {
                                in.setFormulaId(inputDetails.get("?id").get(0));
                            }
                            in.setParentId("");
                            in.setParentType("");
                            in.setIdfrmOnto(inputKey);
                            in.setDestUrl(HostPortandConfig.ROOT_URL + "/framework/" + in.getId() + "/notifs");
                            in.setCategory(inputDetails.get("?category").get(0));
                            in.setDataType(inputDetails.get("?type").get(0));
                            in.setValue("");
                            in.setUrl("");
                            in.reg();
                            LOG.log(Level.INFO, "registered input : {0}", in.getId());
                        }
                    } else {
                        // Get the details for the events from each event urly
                        for (String eventUrl : eventUrls) {
                            String json = "";
                            Map<String, Object> map = new HashMap<>();
                            Map<String, String> eventMeta = new HashMap<>();
                            Map<String, String> payload = new HashMap<>();
                            ObjectMapper mapper = new ObjectMapper();
                            FunctionRESTTemplate funcTemplate = new FunctionRESTTemplate();

                            json = funcTemplate.phlGET(eventUrl);
                            if ((json != null) && (!json.equals(""))) {
                                map = mapper.readValue(json,
                                        new TypeReference<HashMap>() {
                                        });
                                eventMeta = (Map<String, String>) map.get("meta");
                                payload = (Map<String, String>) map.get("payload");
                            } else {
                            }

                            // Get the message format of the paylod form the descriptions associated in meta
                            // Get the URL for meeeage format form the meta
                            // Initailly check if this exists or not and if existes it is empty or not
                            //TODO: Decide if some more formats are needed.
                            MessageFormat messageFormat = null;
                            if (eventMeta != null && !eventMeta.isEmpty()) {
                                if (eventMeta.containsKey("messageFormat") && !eventMeta.get("messageFormat").equals("")) {
                                    if (ModuleRegistry.messageFormats.containsKey(eventMeta.get("messageFormat"))) {
                                        messageFormat = (MessageFormat) ModuleRegistry.messageFormats.get(eventMeta.get("messageFormat"));
                                    } else {
                                        messageFormat = mapper.readValue(funcTemplate.phlGET(eventMeta.get("messageFormat")),
                                                new TypeReference<MessageFormat>() {
                                                });
                                        messageFormat.setId(eventMeta.get("messageFormat"));
                                        messageFormat.reg();
                                    }
                                } else {
                                    //TODO : Error message in the UI and say that it will associate to default
                                }
                            } else {
                                //TODO : Error message in the UI and say that it will associate to default
                            }
                            if (messageFormat == null) {
                                if (inputDetails.get("?category").get(0).equals("event")) {
                                    messageFormat = (MessageFormat) ModuleRegistry.messageFormats.get("eventDefault");
                                } else {
                                    messageFormat = (MessageFormat) ModuleRegistry.messageFormats.get("serviceDefault");
                                }
                            }

                            Input in = null;
                            //Checking if the Input already exists as an object
                            for (String inKey : ModuleRegistry.inputs.keySet()) {
                                if (inputDetails.get("?category").get(0).equals("event")) {
                                    if (ModuleRegistry.inputs.get(inKey).getId().equals(inputDetails.get("?id").get(0) + "_" + eventMeta.get("deviceId"))) {
                                        in = ModuleRegistry.inputs.get(inKey);
                                    }
                                } else {
                                    if (ModuleRegistry.inputs.get(inKey).getId().equals("ser_" + inputDetails.get("?id").get(0) + "_" + eventMeta.get("deviceId"))) {
                                        in = ModuleRegistry.inputs.get(inKey);
                                    }
                                }
                            }
                            if (in == null) {
                                //Generate input for each device id in the rule
                                in = new Input();
                                String idDevice = "";
                                if (eventMeta != null && eventMeta.get("deviceId") != null) {
                                    idDevice = eventMeta.get("deviceId");
                                }
                                if (inputDetails.get("?category").get(0).equals("event")) {
                                    if(idDevice != "" && idDevice != null){
                                        in.setId(inputDetails.get("?id").get(0) + "_" + idDevice);                                        
                                    }else{
                                        in.setId(inputDetails.get("?id").get(0));                                       
                                    }
                                    in.setDeviceId(eventMeta.get("deviceId"));
                                    in.setDeviceType(deviceType);
                                } else {
                                    if (idDevice.equals("") || idDevice == null) {
                                        in.setId("ser_" + inputDetails.get("?id").get(0));
                                        in.setDeviceId("");
                                        in.setDeviceType("");
                                    } else {
                                        in.setId("ser_" + inputDetails.get("?id").get(0) + "_" + idDevice);
                                        in.setDeviceId(eventMeta.get("deviceId"));
                                        in.setDeviceType(deviceType);
                                    }
                                }
                                in.setInputId(inputDetails.get("?id").get(0));
                                in.setMessageFormat(messageFormat);
                                if (!(inputDetails.get("?formulaId").isEmpty()) && !(inputDetails.get("?formulaId").get(0).equals(""))) {
                                    in.setFormulaId(inputDetails.get("?formulaId").get(0));
                                } else {
                                    in.setFormulaId(inputDetails.get("?id").get(0));
                                }
                                if (eventMeta != null && eventMeta.get("parentId") != null && eventMeta.get("parentType") != null) {
                                    in.setParentId(eventMeta.get("parentId"));
                                    in.setParentType(eventMeta.get("parentType"));
                                } else {
                                    in.setParentId("");
                                    in.setParentType("");
                                }
                                in.setIdfrmOnto(inputKey);
                                in.setDestUrl(HostPortandConfig.ROOT_URL + "/framework/" + in.getId() + "/notifs");
                                in.setCategory(inputDetails.get("?category").get(0));
                                if ((inputDetails.get("?category").get(0).equals("event")) || (inputDetails.get("?category").get(0).equals("process"))) {
                                    in.setDataType(payload.get(messageFormat.getDataTypeFormat()));
                                    in.setValue(payload.get(messageFormat.getValueFormat()));
                                    in.setUrl(eventUrl + "/notifs");
                                    //Registering to the events
                                    //TODO: think about clientData
                                    Registeration register = new Registeration(in.getDestUrl(), "");
                                    //TODO: look at error messages if any
                                    SubscriberInputs sub = new SubscriberInputs();
                                    HashMap<String, String> links = new HashMap<>();
                                    if (!in.getUrl().equals("")) {
                                        EventRESTTemplate restTemp = new EventRESTTemplate();
                                        sub = restTemp.eventSubscribePOST(register, in.getUrl());
                                        links = (HashMap<String, String>) sub.getLinks();
                                        in.setSubscribedUrl(links.get("self"));
                                    }
                                } else {
                                    in.setDataType(inputDetails.get("?type").get(0));
                                    in.setValue("");
                                    in.setUrl(eventUrl);
                                }
                                in.reg();
                                LOG.log(Level.INFO, "registered input : {0}", in.getId());
                            }
                        }
                    }
                } else if (inputDetails.get("?category").get(0).equals("value")) {
                    Input in = null;
                    //Checking if the Input already exists as an object
                    for (String inKey : ModuleRegistry.inputs.keySet()) {
                        if (inputDetails.get("?id").get(0).equals(ModuleRegistry.inputs.get(inKey).getId())) {
                            in = ModuleRegistry.inputs.get(inKey);
                        }
                    }
                    if (in == null) {
                        //Generate input for each device id in the rule
                        in = new Input();
                        in.setId(inputDetails.get("?id").get(0));
                        in.setInputId(inputDetails.get("?id").get(0));
                        if (!(inputDetails.get("?formulaId").isEmpty()) && !(inputDetails.get("?formulaId").get(0).equals(""))) {
                            in.setFormulaId(inputDetails.get("?formulaId").get(0));
                        } else {
                            in.setFormulaId(inputDetails.get("?id").get(0));
                        }
                        in.setIdfrmOnto(inputKey);
                        in.setDataType(inputDetails.get("?type").get(0));
                        in.setDeviceId("all");
                        in.setParentId("");
                        in.setParentType("");
                        for (String metaKey : meta.keySet()) {
                            if (meta.get(metaKey).get("metaId").equals("deviceType")) {
                                in.setDeviceType(meta.get(metaKey).get("metaVal"));
                            }
                        }
                        in.setCategory(inputDetails.get("?category").get(0));
                        in.setUrl("");
                        in.setValue(inputDetails.get("?value").get(0));
                        in.reg();
                        LOG.log(Level.INFO, "registered input : {0}", in.getId());
                    }
                } else if (inputDetails.get("?category").get(0).equals("ontology")) {
                    boolean flag = false;
                    //Search for the outputs which are saved to ontology as per their rules
                    for (String rulKey : ModuleRegistry.rules.keySet()) {
                        Rule rul = ModuleRegistry.rules.get(rulKey);
                        if (rul.getAction().equals("OntologySave")) {                            
                            for (Output out : rul.getOutputs().values()) {
                                Input in = null;
                                //Checking if the Input already exists as an object
                                for (String inKey : ModuleRegistry.inputs.keySet()) {
                                    if ((out.getId().equals(ModuleRegistry.inputs.get(inKey).getOntologyId()))) {
                                        if (ModuleRegistry.inputs.get(inKey).getDataType().equals(inputDetails.get("?type").get(0))) {
                                            in = ModuleRegistry.inputs.get(inKey);
                                        } else {
                                            flag = true;
                                            in = ModuleRegistry.inputs.get(inKey);
                                        }
                                    }
                                }
                                if (in == null && (out.getOutputId().equals(inputDetails.get("?id").get(0)))) {
                                    //Generate input for each device id in the rule
                                    in = new Input();
                                    //If the ontology values to be got are for the same value but in different format(eg Array and single variable)
                                    // the Id is set one more _1
                                    if (flag) {
                                        in.setId("ont_" + out.getId() + "_1");
                                    } else {
                                        in.setId("ont_" + out.getId());
                                    }
                                    in.setOntologyId(out.getId());
                                    if (!(inputDetails.get("?formulaId").isEmpty()) && !(inputDetails.get("?formulaId").get(0).equals(""))) {
                                        in.setFormulaId(inputDetails.get("?formulaId").get(0));
                                    } else {
                                        in.setFormulaId(inputDetails.get("?id").get(0));
                                    }
                                    in.setInputId(inputDetails.get("?id").get(0));
                                    in.setIdfrmOnto(inputKey);
                                    in.setDataType(inputDetails.get("?type").get(0));
                                    in.setDeviceId(out.getDeviceId());
                                    in.setParentId("");
                                    in.setParentType("");
                                    for (String metaKey : meta.keySet()) {
                                        if (meta.get(metaKey).get("metaId").equals("deviceType")) {
                                            in.setDeviceType(meta.get(metaKey).get("metaVal"));
                                        }
                                    }
                                    in.setCategory(inputDetails.get("?category").get(0));
                                    in.setUrl("");
                                    in.reg();
                                    LOG.log(Level.INFO, "registered input : {0}", in.getId());
                                }
                            }
                        }
                    }
                } else {
                    Input in = null;
                    //Checking if the Input already exists as an object
                    for (String inKey : ModuleRegistry.inputs.keySet()) {
                        if (inputDetails.get("?id").get(0).equals(ModuleRegistry.inputs.get(inKey).getId())) {
                            in = ModuleRegistry.inputs.get(inKey);
                        }
                    }
                    if (in == null) {
                        //Generate input for each device id in the rule
                        in = new Input();
                        in.setId(inputDetails.get("?id").get(0));
                        in.setInputId(inputDetails.get("?id").get(0));
                        if (!(inputDetails.get("?formulaId").isEmpty()) && !(inputDetails.get("?formulaId").get(0).equals(""))) {
                            in.setFormulaId(inputDetails.get("?formulaId").get(0));
                        } else {
                            in.setFormulaId(inputDetails.get("?id").get(0));
                        }
                        in.setIdfrmOnto(inputKey);
                        in.setDataType(inputDetails.get("?type").get(0));
                        in.setDeviceId("all");
                        in.setParentId("");
                        in.setParentType("");
                        for (String metaKey : meta.keySet()) {
                            if (meta.get(metaKey).get("metaId").equals("deviceType")) {
                                in.setDeviceType(meta.get(metaKey).get("metaVal"));
                            }
                        }
                        in.setCategory(inputDetails.get("?category").get(0));
                        in.setUrl("");
                        in.reg();
                        LOG.log(Level.INFO, "registered input : {0}", in.getId());
                    }
                }
            }

            // Allocating the rules for each input and also 
            // Allocating inputs as objects to rules for easy handling of rule execution
            for (Rule rule : ModuleRegistry.rules.values()) {
                if(rule.getId().equals("oilQualityDecayRateCalculation")){
                    System.out.println();
                }
                ArrayList<String> inList = new ArrayList<>();
                inList = rule.getInputList();
                for (String inKey : inList) {
                    for (Input in : ModuleRegistry.inputs.values()) {
                        if (in.getIdfrmOnto().equals(inKey)) {
                            in.getRuleId().add(rule.getId());
                            rule.getInputs().put(in.getId(), in);
                        }
                        if (in.getValue() == null) {
                            // Assign value to null things
                            //TODO: just give a warning while value has been added.
                            switch (in.getDataType()) {
                                case "string":
                                    in.setValue("");
                                    break;
                                case "boolean":
                                    in.setValue(false);
                                    break;
                                case "long":
                                    in.setValue(0);
                                    break;
                                case "double":
                                    in.setValue(0.0);
                                    break;
                                case "integer":
                                    in.setValue(0);
                                    break;
                                case "array":
                                    ArrayList<String> empty = new ArrayList<>();
                                    in.setValue(empty);
                                    break;
                                case "map":
                                    HashMap<Object, Object> emptyHash = new HashMap<>();
                                    in.setValue(emptyHash);
                                    break;
                                default:
                                    //TODO: Error message saying that data type doesnot exist
                                    break;
                            }
                        }
                        in.reg();
                    }
                }
            }
            reply = true;
        } catch (Exception e) {
            reply = false;
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, InputMapper.class.getName());
        }
        return reply;
    }

    public Input mapInputwithOntologyData(Input in) {
        Input input = null;
        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        try {
            // Get values from ontology
            values = OntologyDataManager.queryData(in.getOntologyId());
            // Get type from ontology
            types = OntologyDataManager.queryDataType(in.getOntologyId());
            // if type is not empty then there is value in ontology
            if (!types.isEmpty()) {
                //Convert the values to desired data type
                //Check if the value needs to be Array or just value and assign respectively 
                ObjectMapper mapper = new ObjectMapper();
                switch (types.get(0)) {
                    case "double":
                        ArrayList<Double> doubleValues = new ArrayList<>();
                        for (String value : values) {
                            doubleValues.add(Double.valueOf(value));
                        }
                        Double[] doubleArr = doubleValues.toArray(new Double[doubleValues.size()]);
                        if (in.getDataType().equals("array")) {
                            in.setValue(doubleArr);
                        } else {
                            in.setValue(doubleValues.get(0));
                        }
                        break;
                    case "integer":
                        ArrayList<Integer> integerValues = new ArrayList<>();
                        for (String value : values) {
                            integerValues.add(Integer.valueOf(value));
                        }
                        Integer[] integerArr = integerValues.toArray(new Integer[integerValues.size()]);
                        if (in.getDataType().equals("array")) {
                            in.setValue(integerArr);
                        } else {
                            in.setValue(integerValues.get(0));
                        }
                        break;
                    case "long":
                        ArrayList<Long> longValues = new ArrayList<>();
                        for (String value : values) {
                            longValues.add(Long.valueOf(value));
                        }
                        Long[] longArr = longValues.toArray(new Long[longValues.size()]);
                        if (in.getDataType().equals("array")) {
                            in.setValue(longArr);
                        } else {
                            in.setValue(longValues.get(0));
                        }
                        break;
                    case "boolean":
                        ArrayList<Boolean> booleanValues = new ArrayList<>();
                        for (String value : values) {
                            booleanValues.add(Boolean.valueOf(value));
                        }
                        Boolean[] booleanArr = booleanValues.toArray(new Boolean[booleanValues.size()]);
                        if (in.getDataType().equals("array")) {
                            in.setValue(booleanArr);
                        } else {
                            in.setValue(booleanValues.get(0));
                        }
                        break;
                    case "string":
                        ArrayList<String> stringValues = new ArrayList<>();
                        for (String value : values) {
                            stringValues.add(value);
                        }
                        if (in.getDataType().equals("array")) {
                            in.setValue(stringValues);
                        } else {
                            in.setValue(values.get(0));
                        }
                        break;
                    case "map":
                        //TODO: add Nested map conversion
                        ArrayList<Map<String, String>> mapArr = new ArrayList<>();
                        for (String value : values) {
                            value = value.substring(1, value.length() - 1);           //remove curly brackets
                            String[] keyValuePairs = value.split(",");
                            //split the string to creat key-value pairs
                            Map<String, String> myMap = new HashMap<>();
                            for (String pair : keyValuePairs) //iterate over the pairs
                            {
                                String[] entry = pair.split("=");                  //split the pairs to get key and value 
                                myMap.put(entry[0].trim(), entry[1].trim());
                            }
                            mapArr.add(myMap);
                        }
                        if (in.getDataType().equals("array")) {
                            in.setValue(mapArr);
                        } else {
                            in.setValue("");
                        }
                    default:
                        //TODO:Put error message in UI
                        break;
                }
            } else {
                switch (in.getDataType()) {
                    case "string":
                        in.setValue("");
                        break;
                    case "boolean":
                        in.setValue(false);
                        break;
                    case "long":
                        in.setValue(0);
                        break;
                    case "double":
                        in.setValue(0.0);
                        break;
                    case "integer":
                        in.setValue(0);
                        break;
                    case "array":
                        ArrayList<String> empty = new ArrayList<>();
                        in.setValue(empty);
                        break;
                    case "map":
                        Map<Object, Object> emptyMap = new HashMap<>();
                        in.setValue(emptyMap);
                        break;
                    default:
                        //TODO: Error message saying that data type doesnot exist
                        break;
                }
            }
            input = in;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, InputMapper.class.getName());
        }
        return input;
    }
}
