/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.constants;

/**
 * Class consists of the main URLs which works with the Fluid House Simulator
 *
 * @author Balaji Gopalakrishnantor
 */
//TODO it is rather constants then configuration. If you change this file the system will not be running on other port or host.
public class HostPortandConfig {

    public static String ROOT_SCHEME = "http://";
    public static String ROOT_ADDRESS = "localhost";
    public static String ROOT_PORT = "5020";    
    public static String ROOT_URL = "http://localhost:5020";
    
    public static String FUSEKI_MES_ENDPOINT_PORT = "5025";
    
    public static String FUSEKI_DATA_ENDPOINT_PORT = "5023";
    
    public static String RPL_ROOT_SCHEME = "http://";
    public static String RPL_ROOT_ADDRESS = "localhost";
    public static String RPL_ROOT_PORT = "8300";
    public static String RPL_ROOT_URL = "http://localhost:8300";
    
    public static Integer HELLO_PORT = 55555;
    public static String HELLO_GROUP = "239.0.1.1";
    
    public static Integer PROBING_PORT = 55556;
    public static String PROBING_GROUP = "239.0.0.1";
    
    public static boolean DISCOVERY = false;
    public static Integer BYE_STATUS = 0;
    
    public static boolean RPL_AVAILABLE = false;  
    
    public static boolean CONFIG_DONE = false; 
    
    public static boolean RULE_EXECUTION = false;
    
    public static String RPL_PROBE_DC = "http://localhost:8300/RPL/RTU";
    
    public static final String[] status = {"disabled", "enabled"};
    
    public static void updateRoot() {
        ROOT_URL = "http://" + ROOT_ADDRESS + ":" + ROOT_PORT;
    }
    
    public static void updateRPLRoot() {
        RPL_ROOT_URL = RPL_ROOT_SCHEME + RPL_ROOT_ADDRESS + ":" + RPL_ROOT_PORT;
    }
    
    public static void updateRPLProbe(){
        RPL_PROBE_DC = RPL_ROOT_URL + "/RPL/RTU";
    }
}
