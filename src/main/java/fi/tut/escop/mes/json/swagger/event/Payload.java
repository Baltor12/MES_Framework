/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.event;

/**
 * Class to represent payload in JSON format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Payload {

    Object value;
    String type;
    String state;
    String quality;
    Object delta;

    public Payload() {
    }

    public Payload(Object value, String type) {
        this.value = value;
        this.type = type;
    }

    public Payload(Object value, String type, String state) {
        this.value = value;
        this.type = type;
        this.state = state;
    }

    public Payload(Object value, String type, Object delta, String quality) {
        this.value = value;
        this.type = type;
        this.delta = delta;
        this.quality = quality;
    }

    public Payload(Object value, String type, String state, String quality, Object delta) {
        this.value = value;
        this.type = type;
        this.state = state;
        this.quality = quality;
        this.delta = delta;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Object getDelta() {
        return delta;
    }

    public void setDelta(Object delta) {
        this.delta = delta;
    }

}
