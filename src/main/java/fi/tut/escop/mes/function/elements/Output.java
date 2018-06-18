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
 * Class that creates output objects
 *
 * @author balaji Gopalakrishnan (TUT)
 */
public class Output {
    String id;
    String outputId;
    String idfrmOnto;
    String ruleId;
    String deviceId;
    String parentId;
    
    //Used if it is a KPI
    boolean isKpi;
    String kpiSymbol;
    String kpiParent;
    ArrayList<String> kpiChildren = new ArrayList<>();
    
    String category;
    String dataType;
    long lastEmit;
    
    Object value;
    Object min;
    Object max;
    Object nom;
    Object prevValue;
    Object delta;
    String quality;
    String state;
    KPIRange kpiRange;
    
    HashMap<String, Object> meta = new HashMap<>();

    public Output() {
    }

    public Output(String id) {
        this.id = id;
    }

    public Output(String id, String outputId, String category, String dataType, Object value) {
        this.id = id;
        this.outputId = outputId;
        this.category = category;
        this.dataType = dataType;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutputId() {
        return outputId;
    }

    public void setOutputId(String outputId) {
        this.outputId = outputId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public HashMap<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Object> meta) {
        this.meta = meta;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getLastEmit() {
        return lastEmit;
    }

    public void setLastEmit(long lastEmit) {
        this.lastEmit = lastEmit;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public boolean isIsKpi() {
        return isKpi;
    }

    public void setIsKpi(boolean isKpi) {
        this.isKpi = isKpi;
    }

    public String getIdfrmOnto() {
        return idfrmOnto;
    }

    public void setIdfrmOnto(String idfrmOnto) {
        this.idfrmOnto = idfrmOnto;
    }

    public Object getPrevValue() {
        return prevValue;
    }

    public void setPrevValue(Object prevValue) {
        this.prevValue = prevValue;
    }

    public Object getDelta() {
        return delta;
    }

    public void setDelta(Object delta) {
        this.delta = delta;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public Object getNom() {
        return nom;
    }

    public void setNom(Object nom) {
        this.nom = nom;
    }

    public KPIRange getKpiRange() {
        return kpiRange;
    }

    public void setKpiRange(KPIRange kpiRange) {
        this.kpiRange = kpiRange;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getKpiSymbol() {
        return kpiSymbol;
    }

    public void setKpiSymbol(String kpiSymbol) {
        this.kpiSymbol = kpiSymbol;
    }

    public String getKpiParent() {
        return kpiParent;
    }

    public void setKpiParent(String kpiParent) {
        this.kpiParent = kpiParent;
    }

    public ArrayList<String> getKpiChildren() {
        return kpiChildren;
    }

    public void setKpiChildren(ArrayList<String> kpiChildren) {
        this.kpiChildren = kpiChildren;
    }
    
    public void reg(){
        ModuleRegistry.outputs.put(id, this);
    }
}
