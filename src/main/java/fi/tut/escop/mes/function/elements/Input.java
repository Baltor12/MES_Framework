/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.elements;

import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to represent the objects of Input
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Input {
    String id;
    String inputId;
    String formulaId;
    String idfrmOnto;
    String ontologyId;
    MessageFormat messageFormat;
    
    String deviceId;
    String deviceType;
    String parentId;
    String parentType;
    
    String category;
    String dataType;
    String destUrl;
    String subscribedUrl;
    String url;
    Object value;
    
    ArrayList<String> ruleId = new ArrayList<>();
    HashMap<String, String> meta = new HashMap<>();   

    public Input() {
    }

    public Input(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputId() {
        return inputId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public HashMap<String, String> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, String> meta) {
        this.meta = meta;
    }

    public ArrayList<String> getRuleId() {
        return ruleId;
    }

    public void setRuleId(ArrayList<String> ruleId) {
        this.ruleId = ruleId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOntologyId() {
        return ontologyId;
    }

    public void setOntologyId(String ontologyId) {
        this.ontologyId = ontologyId;
    }

    public String getIdfrmOnto() {
        return idfrmOnto;
    }

    public void setIdfrmOnto(String idfrmOnto) {
        this.idfrmOnto = idfrmOnto;
    }

    public String getDestUrl() {
        return destUrl;
    }

    public void setDestUrl(String destUrl) {
        this.destUrl = destUrl;
    }

    public String getSubscribedUrl() {
        return subscribedUrl;
    }

    public void setSubscribedUrl(String subscribedUrl) {
        this.subscribedUrl = subscribedUrl;
    }

    public String getFormulaId() {
        return formulaId;
    }

    public void setFormulaId(String formulaId) {
        this.formulaId = formulaId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }
    
    public void reg(){
        ModuleRegistry.inputs.put(this.id, this);
    }
    
}
