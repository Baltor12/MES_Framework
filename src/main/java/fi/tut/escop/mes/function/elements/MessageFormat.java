/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.elements;

import fi.tut.escop.mes.function.administrator.ModuleRegistry;

/**
 * Class that has the representation for message format to be stored in registry
 *
 * @author Balaji Gopalakrishnan
 */
public class MessageFormat {

    String id;
    String valueFormat;
    String timeFormat;
    String deltaFormat;
    String stateFormat;
    String dataTypeFormat;
    String qualityFormat;

    public MessageFormat() {
    }

    public MessageFormat(String valueFormat, String timeFormat, String deltaFormat, String stateFormat, String dataTypeFormat, String qualityFormat) {
        this.valueFormat = valueFormat;
        this.timeFormat = timeFormat;
        this.deltaFormat = deltaFormat;
        this.stateFormat = stateFormat;
        this.dataTypeFormat = dataTypeFormat;
        this.qualityFormat = qualityFormat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValueFormat() {
        return valueFormat;
    }

    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getDeltaFormat() {
        return deltaFormat;
    }

    public void setDeltaFormat(String deltaFormat) {
        this.deltaFormat = deltaFormat;
    }

    public String getStateFormat() {
        return stateFormat;
    }

    public void setStateFormat(String stateFormat) {
        this.stateFormat = stateFormat;
    }

    public String getDataTypeFormat() {
        return dataTypeFormat;
    }

    public void setDataTypeFormat(String dataTypeFormat) {
        this.dataTypeFormat = dataTypeFormat;
    }

    public String getQualityFormat() {
        return qualityFormat;
    }

    public void setQualityFormat(String qualityFormat) {
        this.qualityFormat = qualityFormat;
    }

    /**
     * Method that registers the message format to Module Registry
     *
     */
    public void reg() {
        ModuleRegistry.messageFormats.put(this.id, this);
    }

    /**
     * Method that generates the default payload for events and registers it.
     *
     */
    public void generateDefaultEventFormat() {
        this.id = "eventDefault";
        this.valueFormat = "PalletID";
        this.timeFormat = "time";
        this.qualityFormat = "quality";
        this.deltaFormat = "delta";
        this.stateFormat = "state";
        this.dataTypeFormat = "type";
        reg();
    }
    
    //TODO: Improve message formats if necessary -  think about it at last
    /**
     * Method that generates the default payload for services and registers it.
     *
     */
    public void generateDefaultServiceFormat() {
        this.id = "serviceDefault";
        this.valueFormat = "PalletID";
        this.timeFormat = "time";
        
        // the below variables may or may not be in the payload for a service
        this.qualityFormat = "quality";
        this.deltaFormat = "delta";
        this.stateFormat = "state";
        this.dataTypeFormat = "type";
        reg();
    }

}
