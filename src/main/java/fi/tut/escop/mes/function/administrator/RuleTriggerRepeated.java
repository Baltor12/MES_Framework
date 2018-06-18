/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.administrator;

import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Rule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that triggers the rule as per the event that has been generated Later
 * CEP and more complex architectures will be added in this for stream lining
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class RuleTriggerRepeated implements Runnable {

    private Thread thread;
    private static final Logger LOG = Logger.getLogger(RuleTriggerRepeated.class.getName());

    boolean trig = false;
    HashMap<String, Rule> ruleList = new HashMap<>();

    public RuleTriggerRepeated() {
    }

    public boolean isTrig() {
        return trig;
    }

    public void setTrig(boolean trig) {
        this.trig = trig;
    }

    public HashMap<String, Rule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(HashMap<String, Rule> ruleList) {
        this.ruleList = ruleList;
    }

    /**
     * Function to trigger the rules which are not triggered by input events
     *
     */
    public void repeatedTrigger() {
        for (Rule rule : this.ruleList.values()) {
            //Get the list of devices for the rule
            for (String devId : rule.getDeviceIds()) {
                //Execute the rule for each device
                RuleExecutor ruleExec = new RuleExecutor();
                ruleExec.exec(devId, rule);
            }
        }
    }

    /**
     * Method/Function that is used to discover the rules which are not
     *
     * @return
     */
    public boolean ruleDiscovery() {
        boolean reply = false;
        try {
            for (Rule rule : ModuleRegistry.rules.values()) {
                boolean flag = false;
                // Get the inputs from the rule
                for (Input in : rule.getInputs().values()) {
                    //Check if the category is event and set the flag
                    if (in.getCategory().equals("event") || rule.getAction().equals("ServiceReply")) {
                        flag = true;
                    }
                }
                // If the flag is not set then the rule is aded to rule list
                // The reason for adding this to rule list is for triggering it repeatedly since it will not be triggered by the events 
                if (!flag) {
                    this.ruleList.put(rule.getId(), rule);
                }
            }
            System.out.println(this.ruleList);
            reply = true;
        } catch (Exception e) {
            reply = false;
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, RuleTriggerRepeated.class.getName());
        }
        return reply;
    }

    @Override
    public void run() {
        LOG.log(Level.INFO, "Rule Trigger is available as thread for execution");
        while (true) {
            try {
                Thread.sleep(1000);
                if (HostPortandConfig.RULE_EXECUTION) {
                    // Trigger the rules repeatedly 
                    repeatedTrigger();
                }
            } catch (Exception ex) {
                Logger.getLogger(RuleTriggerRepeated.class.getName()).log(Level.SEVERE, "interrupted Exception", ex);
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void interrupt() {
        thread.interrupt();
        thread = null;
    }
}
