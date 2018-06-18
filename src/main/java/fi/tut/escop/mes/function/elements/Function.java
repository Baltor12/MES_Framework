/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.elements;

import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import java.util.HashMap;

/**
 * Class to hold all the details with respect to a function
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Function implements Runnable {

    String id;
    HashMap<String, Rule> rules = new HashMap();
    String serviceId;

    public Function() {
    }

    public Function(String id) {
        this.id = id;
        this.serviceId = id + "_Trigger";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Rule> getRules() {
        return rules;
    }

    public void setRules(HashMap<String, Rule> rules) {
        this.rules = rules;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    // Method to put the function into registry
    public void reg() {
        ModuleRegistry.functions.put(id, this);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
