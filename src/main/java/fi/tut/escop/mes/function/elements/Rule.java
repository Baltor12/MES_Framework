/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.elements;

import fi.tut.escop.mes.events.administrator.EventAdministrator;
import fi.tut.escop.mes.function.administrator.InputMapper;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.ontology.OntologyDataManager;
import fi.tut.escop.mes.service.administrator.ServiceHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;

/**
 * Class that creates a runnable object for the rule
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class Rule {

    String id;
    String functionId;
    String formula;
    String action;
    String serviceId;
    String deviceType;
    String ruleServiceType;

    //useful only when the rule has to invoke a service
    String serviceFromOnto;

    int repetition;
    ArrayList<String> deviceIds = new ArrayList<>();
    ArrayList<String> inputList = new ArrayList<>();
    ArrayList<String> outputList = new ArrayList<>();
    HashMap<String, Meta> meta = new HashMap<>();
    HashMap<String, Input> inputs = new HashMap<>();
    HashMap<String, Output> outputs = new HashMap<>();
    Output output;
    Service service;

    private static final Logger LOG = Logger.getLogger(Rule.class.getName());
//    private Thread thread;

    public Rule() {
    }

    public Rule(String id) {
        this.id = id;
        this.serviceId = id + "_Trigger";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ArrayList<String> getOutputList() {
        return outputList;
    }

    public void setOutputList(ArrayList<String> outputList) {
        this.outputList = outputList;
    }

    public HashMap<String, Meta> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Meta> meta) {
        this.meta = meta;
    }

    public int getRepetition() {
        return repetition;
    }

    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public ArrayList<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(ArrayList<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    // Method to put rule into registry
    public void reg() {
        ModuleRegistry.rules.put(id, this);
    }

    public ArrayList<String> getInputList() {
        return inputList;
    }

    public void setInputList(ArrayList<String> inputList) {
        this.inputList = inputList;
    }

    public String getServiceFromOnto() {
        return serviceFromOnto;
    }

    public void setServiceFromOnto(String serviceFromOnto) {
        this.serviceFromOnto = serviceFromOnto;
    }

    public HashMap<String, Input> getInputs() {
        return inputs;
    }

    public void setInputs(HashMap<String, Input> inputs) {
        this.inputs = inputs;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public HashMap<String, Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(HashMap<String, Output> outputs) {
        this.outputs = outputs;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getRuleServiceType() {
        return ruleServiceType;
    }

    public void setRuleServiceType(String ruleServiceType) {
        this.ruleServiceType = ruleServiceType;
    }
}
