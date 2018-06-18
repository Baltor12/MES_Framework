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
 * Class that holds the details of the particular device
 * More things can be added later as it to be discussed
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Device {
    
    String id;
    String deviceType;
    String parentId;
    String parentType;
    ArrayList <HashMap<String,String>> Children = new ArrayList<>();

    public Device() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }     

    public ArrayList<HashMap<String,String>> getChildren() {
        return Children;
    }

    public void setChildren(ArrayList<HashMap<String,String>> Children) {
        this.Children = Children;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    /**
     * Function/Method to register the device information 
     * 
     */    
    public void reg(){
        ModuleRegistry.devices.put(this.id, this);
    }
}
