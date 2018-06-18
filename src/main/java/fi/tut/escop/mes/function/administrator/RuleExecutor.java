/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.administrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.mes.events.administrator.EventAdministrator;
import fi.tut.escop.mes.extra.DateSimulator;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.ontology.OntologyDataManager;
import fi.tut.escop.mes.service.administrator.ServiceHandler;
import fi.tut.escop.mes.service.controller.ServiceRESTTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;

/**
 * Class that is used to create objects for rule execution
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class RuleExecutor {

    private static final Logger LOG = Logger.getLogger(RuleExecutor.class.getName());
    Date date = new Date();

    public RuleExecutor() {
    }

    /**
     * Function to execute rules of service type (Service Query Invocation)
     * Currently there is only one rule set and it is not set to devices
     * specific.
     *
     *
     * @param rule
     * @return
     */
    public Output serviceExec(Rule rule) throws IOException {
        Output output = null;
        //the Inputs to var which will assign it for execution
        Map<String, Object> vars = new HashMap<>();
        for (Input in : rule.getInputs().values()) {
            Input input = updateValues(in);
            vars.put(input.getFormulaId(), input.getValue());
        }
        if (vars.size() == rule.getInputList().size()) {
            // Form an executable statement
            ExecutableStatement statement = (ExecutableStatement) MVEL.compileExpression(rule.getFormula());
            //TODO: later combine both of them.
            //execute the statement and generate the result
            Object result = MVEL.executeExpression(statement, vars);
            //map the result to the output
            if (result != null) {
                //Set the output of current rule from list of outputs
                for (Output out : rule.getOutputs().values()) {
                    rule.setOutput(out);
                }
                if (rule.getOutput() != null) {
                    rule.getOutput().setValue(result);
                }
                output = rule.getOutput();
            } else {
                //TODO: Convey the error message
            }
        }
        LOG.log(Level.INFO, "Executed rule : {0}", rule.getId());
        return output;
    }

    /**
     * Function to execute rule without DeviceId
     *
     * @param rule
     */
    public void execWitoutDeviceID(Rule rule) {
        try {
            Map<String, Object> vars = new HashMap<String, Object>();
            // Assign the input variables to be inserted into the executable MVEL statement
            for (Input in : rule.getInputs().values()) {
                // Update values based on input category 
                Input input = updateValues(in);
                vars.put(input.getFormulaId(), input.getValue());
            }
            if (vars.size() == rule.getInputList().size()) {
                // Form an executable statement
                ExecutableStatement statement = (ExecutableStatement) MVEL.compileExpression(rule.getFormula());
                //execute the statement and generate the result
                Object result = MVEL.executeExpression(statement, vars);
                //map the result to the output
                if (result != null) {
                    //Set the output of current rule from list of outputs
                    for (Output out : rule.getOutputs().values()) {
                        rule.setOutput(out);
                    }
                    if (rule.getOutput() != null) {
                        rule.getOutput().setValue(result);
                        LOG.log(Level.INFO, "Executed rule : {0}", rule.getId());
                        //Check the rule for the action type
                        if (rule.getAction().equals("Event")) {
                            //Generate the event only if the previous value is not the same as the current value to avoid event traffic
                            //WARN: Can be changed later if required
                            if ((rule.getOutput().getPrevValue()) != null) {
                                if (!(rule.getOutput().getPrevValue().equals(result))) {
                                    EventAdministrator eventAdmin = new EventAdministrator();
                                    eventAdmin.postEvent(rule.getOutput(), rule);
                                }
                            } else {
                                EventAdministrator eventAdmin = new EventAdministrator();
                                eventAdmin.postEvent(rule.getOutput(), rule);
                            }
                        } else if (rule.getAction().equals("OntologySave")) {
                            OntologyDataManager.insertData(rule.getOutput());
                        } else if (rule.getAction().equals("ServiceInvoke")) {
                            //Actually service Invoke output is an hashmap and it will be triggered based on the trigger condition in the output hash map.
                            // The message or payload for the service will be  also be from the output.
                            HashMap<String, Object> serviceMessage = new HashMap<>();
                            serviceMessage = (HashMap<String, Object>) rule.getOutput().getValue();
                            if (Boolean.parseBoolean(serviceMessage.get("trigger").toString())) {
                                ServiceHandler serHandle = new ServiceHandler();
                                serHandle.serviceInvocation(rule.getService(), serviceMessage.get("message"));
                            }
                        }
                        //Assigning current value to previous value 
                        rule.getOutput().setPrevValue(result);
                        rule.reg();
                    } else {
                        LOG.log(Level.INFO, "Executed rule : {0}", rule.getId());
                        System.out.println(rule.getId() + " : " + rule.getOutputs());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception in executing rule " + rule.getId() + e, RuleExecutor.class.getName());
        }
    }

    /**
     * Function to execute rule with deviceId
     *
     * @param devId
     * @param rule
     */
    public void exec(String devId, Rule rule) {
        Map<String, Object> vars = new HashMap<String, Object>();
        try {
            // Assign the input variables to be inserted into the executable MVEL statement
            for (Input in : rule.getInputs().values()) {
                if (in.getDeviceId().equals(devId)) {
                    // Update values based on input category 
                    Input input = updateValues(in);
                    vars.put(input.getFormulaId(), input.getValue());
                } else if (in.getDeviceId().equals("all")) {
                    Input input = updateValues(in);
                    vars.put(in.getFormulaId(), input.getValue());
                } else {
                    String discoverId = devId;
                    while (discoverId != null) {
                        if ((ModuleRegistry.devices.get(discoverId)) != null) {
                            if ((ModuleRegistry.devices.get(discoverId).getParentId()) != null) {
                                if (in.getDeviceId().equals(ModuleRegistry.devices.get(discoverId).getParentId())) {
                                    // Update values based on input category 
                                    Input input = updateValues(in);
                                    vars.put(input.getFormulaId(), input.getValue());
                                    discoverId = null;
                                } else {
                                    discoverId = ModuleRegistry.devices.get(discoverId).getParentId();
                                }
                            } else {
                                //TODO: device doesnot exist in the discover error message in 
                                discoverId = null;
                            }
                        } else {
                            //TODO: device doesnot exist in the discover error message in 
                            discoverId = null;
                        }
                    }
                }
            }
            ArrayList<String> ruleIds = new ArrayList<>();
            ruleIds.add("oilConsumptionRatioLubricationUnit");
            ruleIds.add("oilConsumptionRatioFlowMeter");
            ruleIds.add("oilConsumptionRatioMeasuringStation");
            ruleIds.add("oilLevelLowDetection");
            ruleIds.add("averageOilChangeCalculation");
            ruleIds.add("ontologySaveOutFlowLU");
            ruleIds.add("averageFilterChangeCalculation");
//            ruleIds.add("leakageAlarmInvoke");
//            ruleIds.add("immediateOilChangeWarning");
//            ruleIds.add("changeOilInvoke");
//            ruleIds.add("saveLastOilChangeTime");
////                        ruleIds.add("rule31");
////                        ruleIds.add("rule13");
//            ruleIds.add("generateLastFilterClogValue");
//            ruleIds.add("oilLevelLowDetection");
////            ruleIds.add("rule30");
//            ruleIds.add("ontologySaveOutFlowLU");
//            ruleIds.add("flowMeterLeakage");
//            ruleIds.add("lubricationUnitLeakage");
//            ruleIds.add("measuringStationLeakage");
//            if (ruleIds.contains(rule.getId())) {
//                System.out.println(vars);
//            }
            // this is for time being
            if ((vars.size() == rule.getInputList().size())) {
//                if(rule.getId().equals("oilQualityDecayRateCalculation")){
//                    System.out.println();
//                }
                // Form an executable statement
                ExecutableStatement statement = (ExecutableStatement) MVEL.compileExpression(rule.getFormula());
                //TODO: later combine both of them.
                //execute the statement and generate the result
                Object result = MVEL.executeExpression(statement, vars);
                //map the result to the output
                if (result != null) {
                    //Set the output of current rule from list of outputs
                    for (Output out : rule.getOutputs().values()) {
                        if (out.getDeviceId().equals(devId)) {
                            rule.setOutput(out);
                        }
                        if (rule.getOutput() == null) {
                            String discoverId = devId;
                            while (discoverId != null) {
                                if ((ModuleRegistry.devices.get(discoverId)) != null) {
                                    if ((ModuleRegistry.devices.get(discoverId).getParentId()) != null) {
                                        if (out.getDeviceId().equals(ModuleRegistry.devices.get(discoverId).getParentId())) {
                                            rule.setOutput(out);
                                            discoverId = null;
                                        } else {
                                            discoverId = ModuleRegistry.devices.get(discoverId).getParentId();
                                        }
                                    } else {
                                        //TODO: device doesnot exist in the discover error message in 
                                        discoverId = null;
                                    }
                                } else {
                                    //TODO: device doesnot exist in the discover error message in 
                                    discoverId = null;
                                }
                            }
                        }
                    }
                    if (rule.getOutput() != null) {
                        rule.getOutput().setValue(result);
//                        if (!ruleIds.contains(rule.getId())) {
//                        if (rule.getId().equals("rule27")) {
                        LOG.log(Level.INFO, "\n*************************************************************\n"
                                + "Executed rule : {0} for device {1} with Output {2} \n"
                                + "*************************************************************\n",
                                new Object[]{
                                    rule.getId(),
                                    rule.getOutput().getDeviceId(),
                                    result});
//                        System.out.println(rule.getId() + " : " + rule.getOutput().getId() + " : " + result);
//                        }
                        //Check the rule for the action type
                        if (rule.getAction().equals("Event")) {
                            //Generate the event only if the previous value is not the same as the current value to avoid event traffic
                            //WARN: Can be changed later if required
                            if ((rule.getOutput().getPrevValue()) != null) {
                                if (!(rule.getOutput().getPrevValue().equals(result))) {
                                    // Find the type of output and put the delta and decimal reduction
                                    switch (rule.getOutput().getDataType()) {
                                        case "double":
                                            rule.getOutput().setDelta(Double.parseDouble(rule.getOutput().getPrevValue().toString()) - Double.parseDouble(result.toString()));
                                            break;
                                        case "integer":
                                            rule.getOutput().setDelta(Integer.parseInt(rule.getOutput().getPrevValue().toString()) - Integer.parseInt(result.toString()));
                                            break;
                                        case "long":
                                            rule.getOutput().setDelta(Long.parseLong(rule.getOutput().getPrevValue().toString()) - Long.parseLong(result.toString()));
                                            break;
                                    }
                                    EventAdministrator eventAdmin = new EventAdministrator();
                                    eventAdmin.postEvent(rule.getOutput(), rule);
                                }
                            } else {
                                EventAdministrator eventAdmin = new EventAdministrator();
                                eventAdmin.postEvent(rule.getOutput(), rule);
                            }
                        } else if (rule.getAction().equals("OntologySave")) {
                            OntologyDataManager.insertData(rule.getOutput());
                        } else if (rule.getAction().equals("ServiceInvoke")) {
                            //Actually service Invoke output is an hashmap and it will be triggered based on the trigger condition in the output hash map.
                            // The message or payload for the service will be  also be from the output.
                            HashMap<String, Object> serviceMessage = new HashMap<>();
                            serviceMessage = (HashMap<String, Object>) rule.getOutput().getValue();
                            if (Boolean.parseBoolean(serviceMessage.get("trigger").toString())) {
                                ServiceHandler serHandle = new ServiceHandler();
                                serHandle.serviceInvocation(rule.getService(), serviceMessage.get("message"));
                            }
                        }
                        //Assigning current value to previous value 
                        rule.getOutput().setPrevValue(result);
                        rule.reg();
                    } else {
                        LOG.log(Level.INFO, "Executed rule with Null: {0}", rule.getId());
                        System.out.println(rule.getId() + " : " + devId + " : " + rule.getOutputs());
                    }
                } else {
                    System.out.println(rule.getId() + " : " + vars);
                    LOG.log(Level.INFO, "Not executed rule : {0}", rule.getId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception in executing rule " + rule.getId() + e, RuleExecutor.class.getName());
        }
    }

    public Input updateValues(Input in) throws IOException {
        Input input;
        if (in.getCategory().equals("ontology")) {
            InputMapper inMap = new InputMapper();
            input = inMap.mapInputwithOntologyData(in);
        } else if (in.getCategory().equals("time")) {
            in.setValue(date.getTime());
            input = in;
        } else if (in.getCategory().equals("query")) {
            ServiceRESTTemplate serviceTemp = new ServiceRESTTemplate();
            if (!in.getUrl().equals("")) {
                ObjectMapper mapper = new ObjectMapper();
                Map<Object, Object> map = new HashMap<>();
                map = mapper.readValue(serviceTemp.queryPOST(in.getUrl()),
                        new TypeReference<HashMap>() {
                        });

                //Segregate type of message
                if (in.getDataType().equals("map")) {;
                    in.setValue(map.get(in.getMessageFormat().getValueFormat()));
                } else if (in.getDataType().equals("array")) {
                    ArrayList<Object> valueArray = new ArrayList<>();
                    valueArray = (ArrayList<Object>) map.get(in.getMessageFormat().getValueFormat());
                    in.setValue(valueArray);
                } else {
                    in.setValue((Object) map.get(in.getMessageFormat().getValueFormat()));
                }

            } else {
                //TODO: error message in UI
            }
            input = in;
        } else {
            input = in;
        }
        if (in.getDataType().equals("boolean")) {
            in.setValue(Boolean.parseBoolean(in.getValue().toString()));
        } else if (in.getDataType().equals("double")) {
            in.setValue(Double.parseDouble(in.getValue().toString().replaceAll(",", ".")));
        }else if (in.getDataType().equals("integer")) {
            in.setValue(Integer.parseInt(in.getValue().toString()));
        }
        return input;
    }
}
