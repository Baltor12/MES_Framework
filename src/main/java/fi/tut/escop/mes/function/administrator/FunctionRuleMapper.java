/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.administrator;

import fi.tut.escop.mes.function.elements.Device;
import fi.tut.escop.mes.function.elements.Function;
import fi.tut.escop.mes.function.elements.Meta;
import fi.tut.escop.mes.function.elements.Rule;
import fi.tut.escop.mes.ontology.OntologyManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that enquires the ontology, forms and maps the Function objects in MES
 * frame work
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class FunctionRuleMapper {

    private static final Logger LOG = Logger.getLogger(FunctionRuleMapper.class.getName());

    public FunctionRuleMapper() {
    }

    public boolean functionAndRuleMapping() {
        boolean reply = false;
        try {
            // Query list of functions from ontology
            ArrayList<String> functions = OntologyManager.queryMESFunctions();
            for (String function : functions) {
                //Creating function objects
                Function func = new Function(function);

                //Query list of rules for each function from ontology
                HashMap<String, HashMap<String, String>> rules = OntologyManager.queryRulesforFunction(function);
                for (String key : rules.keySet()) {
                    //Create rule objects
                    Rule rul = new Rule(rules.get(key).get("rule"));
                    rul.setFormula(rules.get(key).get("formula"));
                    rul.setAction(rules.get(key).get("action"));
                    rul.setServiceFromOnto(rules.get(key).get("service"));
                    rul.setFunctionId(function);
                    
                    // set the type of service for the rule according to the ontology
                    switch(rul.getAction()){
                        case "Event":
                            rul.setRuleServiceType("process");
                            break;
                        case "OntologySave":
                            rul.setRuleServiceType("operation");
                            break;
                        case "ServiceInvoke":
                            rul.setRuleServiceType("operation");
                            break;
                        case "ServiceReply":
                            rul.setRuleServiceType("query");
                            break;
                        default:
                            //TODO: error message in UI that the action is not correctly set for the 
                            break;
                    }

                    //Get list of outputs for the rule from ontology and assign to the outputs in rule object
                    rul.setOutputList(OntologyManager.queryOutputsForeachRule(rules.get(key).get("rule")));

                    //Get list of outputs for the rule from ontology and assign to the outputs in rule object
                    rul.setInputList(OntologyManager.queryInputsForeachRule(rules.get(key).get("rule")));

                    //Get list of meta for the rule from ontology and assign to the meta in rule object
                    HashMap<String, HashMap<String, String>> meta = OntologyManager.queryMetasforRule(rules.get(key).get("rule"));
                    for (String metaKey : meta.keySet()) {

                        //Create Meta object
                        Meta tag = new Meta(meta.get(metaKey).get("metaId"), meta.get(metaKey).get("metaVal"));

                        // Assign to rule object
                        // While assigning the meta key are given as 'tag_n' just for distinction key purpose and it has no use further.
                        rul.getMeta().put("tag_" + rul.getMeta().size(), tag);
                    }
                    rul.reg();
                    func.getRules().put(rul.getId(), rul);
                }
                LOG.log(Level.INFO, "registered Function : {0}", func.getId());
                func.reg();
            }
            ArrayList<String> devices = new ArrayList<>();

            // Get the meta for all rules
            for (String ruleKey : ModuleRegistry.rules.keySet()) {
                Rule rule = ModuleRegistry.rules.get(ruleKey);
                for (String tagKey : rule.getMeta().keySet()) {
                    Meta tag = rule.getMeta().get(tagKey);
                    //Sort the meta with deviceType
                    if (tag.getId().equals("deviceType")) {
                        //Store it in an array
                        if (!devices.contains(tag.getValue())) {
                            devices.add(tag.getValue());
                        }
                    }
                }
            }

            //TODO: Check devices from RPl for the tags. If it does not exist search if events are available for the device else services.
            // Get the meta for all rules
            for (String ruleKey : ModuleRegistry.rules.keySet()) {
                Rule rule = ModuleRegistry.rules.get(ruleKey);
                for (String tagKey : rule.getMeta().keySet()) {
                    Meta tag = rule.getMeta().get(tagKey);
                    //Sort the meta with deviceType
                    if (tag.getId().equals("deviceType")) {
                        //Store the repetiton number to the rules as per the number of devices from the RPL
                        rule.setDeviceType(tag.getValue());
                        for (Device dev : ModuleRegistry.devices.values()) {
                            if (dev.getDeviceType().equals(tag.getValue())) {
                                int newRepetion = rule.getRepetition() + 1;
                                rule.setRepetition(newRepetion);
                                rule.getDeviceIds().add(dev.getId());
                            }
                        }
                    }
                }
                LOG.log(Level.INFO, "registered Rule : {0}", rule.getId());
                rule.reg();
            }
            reply = true;
        } catch (Exception e) {
            reply = false;
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, FunctionRuleMapper.class.getName());
        }
        return reply;
    }
}
