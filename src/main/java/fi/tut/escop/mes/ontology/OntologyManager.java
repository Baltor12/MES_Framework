/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.ontology;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import fi.tut.escop.mes.constants.HostPortandConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that operates with Ontology
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class OntologyManager {

    private static final String myUrl = HostPortandConfig.ROOT_SCHEME + HostPortandConfig.ROOT_ADDRESS + ":" + HostPortandConfig.FUSEKI_MES_ENDPOINT_PORT;
    private static final String fusekiServerEndpoint = myUrl + "/mes";
    private static final Logger LOG = Logger.getLogger(OntologyManager.class.getName());

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * functions
     *
     * @return
     */
    public static ArrayList<String> queryMESFunctions() {
        try {
            ArrayList<String> functions = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryMESFunction();
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            sparqlHelp(queryExecution, "?func", functions);
            return functions;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }
    
    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * functions
     *
     * @return
     */
    public static ArrayList<String> queryMESDevices() {
        try {
            ArrayList<String> functions = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryDevices();
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            sparqlHelp(queryExecution, "?device", functions);
            return functions;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * rules for each function
     *
     * @param function
     * @return
     */
    public static HashMap<String, HashMap<String, String>> queryRulesforFunction(String function) {
        try {
            ArrayList<String> variables = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryRulesofFunction(function);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.add("?rule");
            variables.add("?action");
            variables.add("?formula");
            variables.add("?service");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * functions
     *
     * @return
     */
    public static ArrayList<String> queryInputs() {
        try {
            ArrayList<String> inputs = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryInputs();
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            sparqlHelp(queryExecution, "?inputs", inputs);
            return inputs;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the output
     * list for each rule
     *
     * @param rule
     * @return
     */
    public static ArrayList<String> queryOutputsForeachRule(String rule) {
        try {
            ArrayList<String> functions = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryOutputsofRule(rule);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            sparqlHelp(queryExecution, "?outputs", functions);
            return functions;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the output
     * list for each rule
     *
     * @param rule
     * @return
     */
    public static ArrayList<String> queryInputsForeachRule(String rule) {
        try {
            ArrayList<String> functions = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryInputsofRule(rule);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            sparqlHelp(queryExecution, "?inputs", functions);
            return functions;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * meta for each rule
     *
     * @param rule
     * @return
     */
    public static HashMap<String, HashMap<String, String>> queryMetasforRule(String rule) {
        try {
            ArrayList<String> variables = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryMetasofRule(rule);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.add("?metaId");
            variables.add("?metaVal");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the details
     * of output
     *
     * @param output
     * @return
     */
    public static HashMap<String, ArrayList<String>> queryOutputDetails(String output) {
        try {
            HashMap<String, ArrayList<String>> variables = new HashMap<>();
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> category = new ArrayList<>();
            ArrayList<String> type = new ArrayList<>();
            ArrayList<String> kpi = new ArrayList<>();
            ArrayList<String> max = new ArrayList<>();
            ArrayList<String> nom = new ArrayList<>();
            ArrayList<String> min = new ArrayList<>();
            ArrayList<String> value = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryOutputDetails(output);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.put("?id", id);
            variables.put("?category", category);
            variables.put("?type", type);
            variables.put("?kpi", kpi);
            variables.put("?max", max);
            variables.put("?min", min);
            variables.put("?nom", nom);
            variables.put("?value", value);
            sparqlMultipleOutput(queryExecution, variables);
            return variables;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }
    
    /**
     * Function to contact Ontology through SPARQL query and return the details
     * of output
     *
     * @param output
     * @return
     */
    public static HashMap<String, ArrayList<String>> queryOutputKPIDetails(String output) {
        try {
            HashMap<String, ArrayList<String>> variables = new HashMap<>();
            ArrayList<String> symbol = new ArrayList<>();
            ArrayList<String> children = new ArrayList<>();
            ArrayList<String> unit = new ArrayList<>();
            ArrayList<String> parent = new ArrayList<>();
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> range = new ArrayList<>();
            ArrayList<String> title = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryOutputKPIDetails(output);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.put("?symbol", symbol);
            variables.put("?children", children);
            variables.put("?parent", parent);
            variables.put("?unit", unit);
            variables.put("?id", id);
            variables.put("?range", range);
            variables.put("?title", title);
            sparqlMultipleOutput(queryExecution, variables);
            return variables;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }
    
    /**
     * Function to contact Ontology through SPARQL query and return the details
     * of kpiRange
     * @param kpiRange
     * @return
     */
    public static HashMap<String, ArrayList<String>> queryKPIRangeDetails(String kpiRange) {
        try {
            HashMap<String, ArrayList<String>> variables = new HashMap<>();
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> hh = new ArrayList<>();
            ArrayList<String> hn = new ArrayList<>();
            ArrayList<String> nn = new ArrayList<>();
            ArrayList<String> nl = new ArrayList<>();
            ArrayList<String> ll = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryKPIRangeDetails(kpiRange);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.put("?id", id);
            variables.put("?hh", hh);
            variables.put("?hn", hn);
            variables.put("?nn", nn);
            variables.put("?nl", nl);
            variables.put("?ll", ll);
            sparqlMultipleOutput(queryExecution, variables);
            return variables;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the details
     * of input
     *
     * @param input
     * @return
     */
    public static HashMap<String, ArrayList<String>> queryInputDetails(String input) {
        try {
            HashMap<String, ArrayList<String>> variables = new HashMap<>();
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> category = new ArrayList<>();
            ArrayList<String> type = new ArrayList<>();
            ArrayList<String> formulaId = new ArrayList<>();
            ArrayList<String> value = new ArrayList<>();
            ArrayList<String> url = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryInputDetails(input);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.put("?id", id);
            variables.put("?type", type);
            variables.put("?category", category);
            variables.put("?formulaId", formulaId);
            variables.put("?value", value);
            variables.put("?url", url);
            sparqlMultipleOutput(queryExecution, variables);
            return variables;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the details
     * of input
     *
     * @param input
     * @return
     */
    public static HashMap<String, ArrayList<String>> queryServiceDetails(String service) {
        try {
            HashMap<String, ArrayList<String>> variables = new HashMap<>();
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> category = new ArrayList<>();
            ArrayList<String> type = new ArrayList<>();
            ArrayList<String> value = new ArrayList<>();
            String serviceQuery = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryServiceDetails(service);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(serviceQuery, query);
            variables.put("?id", id);
            variables.put("?meta", type);
            variables.put("?category", category);
            sparqlMultipleOutput(queryExecution, variables);
            return variables;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * meta for each Input
     *
     * @param input
     * @return
     */
    public static HashMap<String, HashMap<String, String>> queryMetasforInput(String input) {
        try {
            ArrayList<String> variables = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryMetasofInput(input);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.add("?metaId");
            variables.add("?metaVal");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * meta for each Input
     *
     * @param service
     * @return
     */
    public static HashMap<String, HashMap<String, String>> queryMetasforService(String service) {
        try {
            ArrayList<String> variables = new ArrayList<>();
            String endPoint = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryMetasofService(service);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endPoint, query);
            variables.add("?metaId");
            variables.add("?metaVal");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

//    public static void insertFlowMeter(FlowMeter fm) {
//        try {
//            String update = SparqlQueryFactory.sparqlUpdateFlowMeter(fm.getId(), fm.getNomFlow());
//            UpdateRequest request = UpdateFactory.create(update);
//            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
//        }
//
//    }
    public static void sparqlHelp(QueryExecution queryExecution, String valueLabel, ArrayList resultArray) {
        ResultSet result = queryExecution.execSelect();
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            RDFNode node = soln.get(valueLabel);
            if (node != null) {
                resultArray.add(correctString(node.toString().replace("http://www.escop-project.eu/MES.owl#", "")));
            }
        }
        queryExecution.close();
    }

    public static void sparqlMultipleOutput(QueryExecution queryExecution, HashMap<String, ArrayList<String>> variables) {
        ResultSet result = queryExecution.execSelect();
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            for (String valueLabel : variables.keySet()) {
                RDFNode node = soln.get(valueLabel);
                if (node != null) {
                    if (!variables.get(valueLabel).contains(correctString(node.toString().replace("http://www.escop-project.eu/MES.owl#", "")))) {
                        variables.get(valueLabel).add(correctString(node.toString().replace("http://www.escop-project.eu/MES.owl#", "")));
                    }
                }else{
                     variables.get(valueLabel).add("");
                }
            }
        }
        queryExecution.close();
    }

    public static HashMap<String, HashMap<String, String>> sparqlTable(QueryExecution queryExecution, ArrayList<String> variables) {
        ResultSet result = queryExecution.execSelect();
        HashMap<String, HashMap<String, String>> reply = new HashMap<>();
        int increment = 1;
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            HashMap<String, String> value = new HashMap<>();
            for (String valueLabel : variables) {
                RDFNode node = soln.get(valueLabel);
                if (node != null) {
                    value.put(valueLabel.replace("?", ""), correctString(node.toString().replace("http://www.escop-project.eu/MES.owl#", "")));
                } else {
                    value.put(valueLabel.replace("?", ""), "");
                }
            }
            reply.put("Key_" + increment, value);
            increment++;
        }
        queryExecution.close();
        return reply;
    }

    public static String correctString(String str) {
        if (str.indexOf('^') != -1) {
            str = str.substring(0, str.indexOf('^'));
        }
        return str;
    }

}
