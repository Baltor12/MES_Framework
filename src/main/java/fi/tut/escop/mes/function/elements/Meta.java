/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.elements;

/**
 * Class to have the formal representation for meta
 * 
 * @author Balaji Gopalakrishnan (TUT)
 */
public class Meta {
    String id;
    String value;

    public Meta() {
    }

    public Meta(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
