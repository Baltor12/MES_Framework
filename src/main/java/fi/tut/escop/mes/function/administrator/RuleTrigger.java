/*
 * To change this license header, choose License Headers input Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template input the editor.
 */
package fi.tut.escop.mes.function.administrator;

import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.function.elements.Rule;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that triggers the rule as per the event that has been generated Later
 * CEP and more complex architectures will be added input this for stream lining
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class RuleTrigger implements Runnable {

    Input input;
    private Thread thread;
    private static final Logger LOG = Logger.getLogger(RuleTrigger.class.getName());

    public RuleTrigger() {
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    /**
     * Executes the required rule for which the event has been generated
     *
     */
    public void trigger() {
        for (String ruleKey : this.input.getRuleId()) {
            boolean i = true;
            Rule rule = ModuleRegistry.rules.get(ruleKey);
            if (!rule.getAction().equals("ServiceReply")) {
                RuleExecutor ruleExec = new RuleExecutor();
                if (this.input.getDeviceId().equals("") && rule.getDeviceIds().isEmpty()) {
                    ruleExec.execWitoutDeviceID(rule);
                } else {
                    ruleExec.exec(this.input.getDeviceId(), rule);
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            if (HostPortandConfig.RULE_EXECUTION) {
                trigger();
            }
        } catch (Exception ex) {
            Logger.getLogger(RuleTrigger.class.getName()).log(Level.SEVERE, "Exception", ex);
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
