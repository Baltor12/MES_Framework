/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes;

import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.discovery.MulticastListener;
import fi.tut.escop.mes.extra.DateSimulator;
import fi.tut.escop.mes.function.administrator.DeviceRegisteration;
import fi.tut.escop.mes.function.administrator.FunctionRuleMapper;
import fi.tut.escop.mes.function.administrator.InputMapper;
import fi.tut.escop.mes.function.administrator.OutputMapper;
import fi.tut.escop.mes.function.administrator.RuleTriggerRepeated;
import fi.tut.escop.mes.function.elements.KPIRange;
import fi.tut.escop.mes.function.elements.MessageFormat;
import fi.tut.escop.mes.service.administrator.ServiceMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Class for the whole Framework
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
@SpringBootApplication
public class MESLayer {

    private static final Logger LOG = Logger.getLogger(MESLayer.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {

        String property = "server.host";
        String value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.ROOT_ADDRESS = value;
            HostPortandConfig.updateRoot();
        }

        property = "server.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.ROOT_PORT = value;
            HostPortandConfig.updateRoot();
        }

        property = "fuseki.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.FUSEKI_MES_ENDPOINT_PORT = value;
        }

        property = "data.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.FUSEKI_DATA_ENDPOINT_PORT = value;
        }

        property = "rpl.host";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.RPL_ROOT_ADDRESS = value;
            HostPortandConfig.updateRPLRoot();
        }

        property = "rpl.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.RPL_ROOT_PORT = value;
            HostPortandConfig.updateRPLRoot();
        }

        property = "hello.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.HELLO_PORT = Integer.valueOf(value);
        }

        property = "hello.group";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.HELLO_GROUP = value;
        }

        property = "discovery";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.DISCOVERY = value.equalsIgnoreCase("true");
        }

        SpringApplication.run(MESLayer.class, args);

        LOG.log(Level.INFO,
                "\n*************************************************************\n"                
                + "***********************CONFIGURATION*************************\n"
                + "*************************************************************\n"
                + "1. Self: {0}\n"
                + "2. Discovery {5}\n"
                + "3. Probing config: PORT - {1}; GROUP - {2}\n"
                + "4. Hello config: PORT - {3}; GROUP - {4}\n"
                + "*************************************************************",
                new Object[]{
                    HostPortandConfig.ROOT_URL,
                    HostPortandConfig.PROBING_PORT,
                    HostPortandConfig.PROBING_GROUP,
                    HostPortandConfig.HELLO_PORT,
                    HostPortandConfig.HELLO_GROUP,
                    HostPortandConfig.status[(HostPortandConfig.DISCOVERY) ? 1 : 0]
                }
        );

        if (HostPortandConfig.DISCOVERY) {
            Thread t = new Thread(new MulticastListener());
            t.start();
        }
        
        // Starting Time Multiplier
        DateSimulator dateSim = new DateSimulator();
        dateSim.start();

        // Generating and Associating default message format for event and service payloads
        MessageFormat payloadMessageFormat = new MessageFormat();
        payloadMessageFormat.generateDefaultEventFormat();
        payloadMessageFormat.generateDefaultServiceFormat();
        KPIRange kpi = new KPIRange("default", 100.0, 50.0, 0, 100.0, 75.0, 50.0, 25.0, 0.0);
        kpi.reg();

        FunctionRuleMapper funcRuleMap = new FunctionRuleMapper();
        OutputMapper outMap = new OutputMapper();
        ServiceMapper serMap = new ServiceMapper();
        InputMapper inMap = new InputMapper();
        DeviceRegisteration device = new DeviceRegisteration();

        // mapping Rules and Functions
        if (device.RegisterDevice()) {
            if (funcRuleMap.functionAndRuleMapping()) {
                // Maping Output and service
                if (outMap.outputMapping() && serMap.serviceMapping()) {
                    //enabling swagger posting to the RPL 
                    HostPortandConfig.CONFIG_DONE = true;
                    // waiting for 5 seconds fro the framework to do the event configurations
                    // these events will be inturn used in inputs hence the wait is necessary
                    Thread.sleep(5000);
                    //Mapping inputs
                    if (inMap.inputMapping()) {
                        RuleTriggerRepeated ruleTrig = new RuleTriggerRepeated();
                        ruleTrig.start();
                        if (ruleTrig.ruleDiscovery()) {
                            // Set the trig 
                            HostPortandConfig.RULE_EXECUTION = true;
                        }
                    } else {
                        //TODO: error report in the UI                     
                    }
                } else {
                    //TODO: error report in the UI                
                }
            } else {
                //TODO: error report in the UI
            }
        } else {
            //TODO: error report in the UI
        }
    }
}
