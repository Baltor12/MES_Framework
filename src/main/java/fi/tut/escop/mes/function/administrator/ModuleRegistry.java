/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.administrator;

import fi.tut.escop.mes.function.elements.Device;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.elements.Function;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.KPIRange;
import fi.tut.escop.mes.function.elements.Rule;
import java.util.HashMap;

/**
 * Registry Class to store the Module details
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class ModuleRegistry {
    //registry for Functions
    public static HashMap<String, Function> functions = new HashMap<>();
    
    //registry for Rules
    public static HashMap<String, Rule> rules = new HashMap<>();
    
    //registry for Devices
    public static HashMap<String, Device> devices = new HashMap<>();
    
    //registry for Outputs
    public static HashMap<String, Output> outputs = new HashMap<>();
    
    //registry for Inputs
    public static HashMap<String, Input> inputs = new HashMap<>();
    
    //registry for Message Formats
    // This will help in reducing the query to other layers for the message format it will supply.
    public static HashMap<String, Object> messageFormats = new HashMap<>(); 
    
    //registry for KPI range 
    public static HashMap<String, KPIRange> kpiRanges = new HashMap<>(); 
    
}
