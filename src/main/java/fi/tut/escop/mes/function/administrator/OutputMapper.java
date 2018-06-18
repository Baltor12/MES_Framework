/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.administrator;

import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.elements.KPIRange;
import fi.tut.escop.mes.function.elements.Output;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.ontology.OntologyManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that does the function of mapping outputs from the ontology by creating
 * objects for the same
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class OutputMapper {

    private static final Logger LOG = Logger.getLogger(OutputMapper.class.getName());

    public OutputMapper() {
    }

    /**
     * Method that maps the outputs from ontology with a object created for the
     * same in MES framework
     *
     * @return
     */
    public boolean outputMapping() {
        boolean reply = false;
        try {
            // Query each output for the rule from ontology and create objects for the same
            for (String ruleKey : ModuleRegistry.rules.keySet()) {
                Rule rule = ModuleRegistry.rules.get(ruleKey);
                //Get each output from list of outputs
                for (String output : rule.getOutputList()) {
                    //Get output details for each output
                    HashMap<String, ArrayList<String>> outputDetails = OntologyManager.queryOutputDetails(output);

                    Output out = null;
                    //Checking if the output already exists as an object
                    for (String outKey : ModuleRegistry.outputs.keySet()) {
                        if (outputDetails.get("?id").get(0).equals(ModuleRegistry.outputs.get(outKey).getOutputId())) {
                            out = ModuleRegistry.outputs.get(outKey);
                        }
                    }
                    if (out == null) {
                        //Mock for testing
                        if (rule.getDeviceIds().isEmpty()) {
                            out = new Output();
                            out.setOutputId(outputDetails.get("?id").get(0));
                            out.setIdfrmOnto(output);
                            out.setRuleId(rule.getId());
                            out.setDataType(outputDetails.get("?type").get(0));
                            out.setCategory(outputDetails.get("?category").get(0));
                            out.setIsKpi(Boolean.parseBoolean(outputDetails.get("?kpi").get(0)));
                            out.setMax(outputDetails.get("?max").get(0));
                            out.setMin(outputDetails.get("?min").get(0));
                            out.setNom(outputDetails.get("?nom").get(0));
                            if (!outputDetails.get("?value").get(0).equals("")) {
                                out.setValue(outputDetails.get("?value").get(0));
                            }
                            out.setDeviceId("");
                            out.setParentId("");
                            HashMap<String, Object> meta = new HashMap<>();

                            //Assign meta of Device Type and device ID for each device
                            //Sort the meta with deviceType
                            meta.put("deviceType", "framework");
                            meta.put("deviceId", "");
                            meta.put("sensorType", out.getOutputId());
                            meta.put("contextId", "TBD");
                            meta.put("messageFormat", HostPortandConfig.ROOT_URL + "/framework/message/event");
                            if (out.getParentId() != null) {
                                meta.put("parentId", out.getParentId());
                                meta.put("parentType", "");
                            }
                            out.setMeta(meta);
                            out.setId(out.getOutputId());
                            out.reg();
                            LOG.log(Level.INFO, "registered output : {0}", out.getId());
                        } else {
                            //Generate output for each device id in the rule
                            for (String idKey : rule.getDeviceIds()) {

                                out = new Output();
                                out.setOutputId(outputDetails.get("?id").get(0));
                                out.setIdfrmOnto(output);
                                out.setRuleId(rule.getId());
                                out.setDataType(outputDetails.get("?type").get(0));
                                out.setCategory(outputDetails.get("?category").get(0));
                                out.setIsKpi(Boolean.parseBoolean(outputDetails.get("?kpi").get(0)));
                                out.setMax(outputDetails.get("?max").get(0));
                                out.setMin(outputDetails.get("?min").get(0));
                                out.setNom(outputDetails.get("?nom").get(0));
                                if (!outputDetails.get("?value").get(0).equals("")) {
                                    out.setValue(outputDetails.get("?value").get(0));
                                }
                                out.setDeviceId(idKey);
                                out.setParentId(ModuleRegistry.devices.get(idKey).getParentId());
                                HashMap<String, Object> meta = new HashMap<>();

                                //Assign meta of Device Type and device ID for each device
                                //Sort the meta with deviceType
                                meta.put("deviceType", "MES");
                                meta.put("deviceId", idKey);
                                meta.put("sensorType", out.getOutputId());
                                meta.put("contextId", "TBD");
                                meta.put("messageFormat", HostPortandConfig.ROOT_URL + "/framework/message/event");
                                if (out.getParentId() != null) {
                                    meta.put("parentId", out.getParentId());
                                    meta.put("parentType", ModuleRegistry.devices.get(idKey).getParentType());
                                }
                                out.setMeta(meta);
                                out.setId(out.getOutputId() + "_" + idKey);
                                out.reg();
                                LOG.log(Level.INFO, "registered output : {0}", out.getId());
                            }
                        }
                    }
                }
            }
            for (Rule rule : ModuleRegistry.rules.values()) {
                for (String output : rule.getOutputList()) {
                    for (Output out : ModuleRegistry.outputs.values()) {
                        if (out.getIdfrmOnto().equals(output)) {
                            rule.getOutputs().put(out.getId(), out);
                        }
                    }
                }
            }
            // Set KPI details for Outputs
            HashMap<String, Output> outputRegistry = new HashMap<String, Output>(ModuleRegistry.outputs);
            for (Output out : outputRegistry.values()) {
                HashMap<String, ArrayList<String>> kpiDetails = new HashMap<>();
                if (out.isIsKpi()) {
                    kpiDetails = OntologyManager.queryOutputKPIDetails(out.getIdfrmOnto());
                    out.setKpiSymbol(kpiDetails.get("?symbol").get(0));
                    for (Output outParent : ModuleRegistry.outputs.values()) {
                        if (outParent.getIdfrmOnto().equals(kpiDetails.get("?parent").get(0)) && out.getParentId().equals(outParent.getDeviceId())) {
                            out.setKpiParent(outParent.getId());
                        }
                    }
                    for (String childKpi : kpiDetails.get("?children")) {
                        for (Output outChild : ModuleRegistry.outputs.values()) {
                            if (outChild.getIdfrmOnto().equals(childKpi) && outChild.getParentId().equals(out.getDeviceId())) {
                                out.getKpiChildren().add(outChild.getId());
                            }
                        }
                    }
                    // Check if the ID exists from Ontology else assign from output 
                    if (!kpiDetails.get("?id").get(0).equals("")) {
                        out.getMeta().put("kpiId", kpiDetails.get("?id").get(0));
                    } else {
                        out.getMeta().put("kpiId", out.getId());
                    }
                    // Check if the title exists from Ontology else assign from output 
                    if (!kpiDetails.get("?title").get(0).equals("")) {
                        out.getMeta().put("kpiTitle", kpiDetails.get("?title").get(0));
                    } else {
                        //check if device Id exists else just assign output id
                        if (!out.getDeviceId().equals("")) {
                            out.getMeta().put("kpiTitle", out.getOutputId() + " of " + out.getDeviceId());
                        } else {
                            out.getMeta().put("kpiTitle", out.getOutputId());
                        }
                    }
                    if (!kpiDetails.get("?unit").get(0).equals("")) {
                        out.getMeta().put("kpiUnits", kpiDetails.get("?unit").get(0));
                    } else {
                        out.getMeta().put("kpiUnits", "%");
                    }
                    if (!kpiDetails.get("?range").get(0).equals("")) {
                        if ((ModuleRegistry.kpiRanges.get(kpiDetails.get("?range").get(0))) != null) {
                            out.setKpiRange(ModuleRegistry.kpiRanges.get(kpiDetails.get("?range").get(0)));
                        } else {
                            HashMap<String, ArrayList<String>> kpiRangeDetails = new HashMap<>();
                            kpiRangeDetails = OntologyManager.queryKPIRangeDetails(out.getIdfrmOnto());
                            KPIRange kpiRange = new KPIRange();
                            if (!kpiRangeDetails.get("?id").get(0).equals("")) {
                                kpiRange.setId(kpiRangeDetails.get("?id").get(0));
                                kpiRange.setHh(Double.parseDouble(kpiRangeDetails.get("?hh").get(0)));
                                kpiRange.setHn(Double.parseDouble(kpiRangeDetails.get("?hn").get(0)));
                                kpiRange.setNn(Double.parseDouble(kpiRangeDetails.get("?nn").get(0)));
                                kpiRange.setNl(Double.parseDouble(kpiRangeDetails.get("?nl").get(0)));
                                kpiRange.setLl(Double.parseDouble(kpiRangeDetails.get("?ll").get(0)));
                                kpiRange.reg();
                                out.setKpiRange(kpiRange);
                            } else {
                                //TODO: Error message in UI
                            }
                        }
                    } else {
                        out.setKpiRange(ModuleRegistry.kpiRanges.get("default"));
                    }
                    HashMap<String, Object> kpiRangeHash = new HashMap<>();
                    kpiRangeHash.put("max", out.getMax());
                    kpiRangeHash.put("nom", out.getNom());
                    kpiRangeHash.put("min", out.getMin().toString());
                    if ((!out.getMax().toString().equals("")) && (out.getKpiRange() != null)) {
                        KPIRange kpiRange = out.getKpiRange();
                        kpiRangeHash.put("hh", Double.toString((Double.parseDouble(out.getMax().toString()) * kpiRange.getHh()) / 100));
                        kpiRangeHash.put("hn", Double.toString((Double.parseDouble(out.getMax().toString()) * kpiRange.getHn()) / 100));
                        kpiRangeHash.put("nn", Double.toString((Double.parseDouble(out.getMax().toString()) * kpiRange.getNn()) / 100));
                        kpiRangeHash.put("nl", Double.toString((Double.parseDouble(out.getMax().toString()) * kpiRange.getNl()) / 100));
                        kpiRangeHash.put("ll", Double.toString((Double.parseDouble(out.getMax().toString()) * kpiRange.getLl()) / 100));

                    }
                    out.getMeta().put("kpiSymbol", out.getKpiSymbol());
                    out.getMeta().put("kpiParent", out.getKpiParent());
                    out.getMeta().put("kpiChildren", out.getKpiChildren());
                    out.getMeta().put("kpiRange", kpiRangeHash);
                    out.reg();
                }
            }
            reply = true;
        } catch (Exception e) {
            reply = false;
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, OutputMapper.class.getName());
        }
        return reply;
    }
}
