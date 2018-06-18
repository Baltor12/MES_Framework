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
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.elements.Output;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that operates with Ontology
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class OntologyDataManager {

    private static final String myUrl = HostPortandConfig.ROOT_SCHEME + HostPortandConfig.ROOT_ADDRESS + ":" + HostPortandConfig.FUSEKI_DATA_ENDPOINT_PORT;
    private static final String fusekiServerEndpoint = myUrl + "/mdata";
    private static final Logger LOG = Logger.getLogger(OntologyDataManager.class.getName());
    private static long dataId = 0;

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * vales for data
     *
     * @return
     */
    public static ArrayList<String> queryData(String id) {
        try {
            ArrayList<String> functions = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryforData(id);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            sparqlHelp(queryExecution, "?value", functions);
            return functions;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to contact Ontology through SPARQL query and return the list of
     * vales for data
     *
     * @return
     */
    public static ArrayList<String> queryDataType(String id) {
        try {
            ArrayList<String> functions = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQueryforData(id);
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            sparqlHelp(queryExecution, "?type", functions);
            return functions;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    /**
     * Function to insert the data into ontology
     *
     * @param out
     */
    public static void insertData(Output out) {
        try {
            Date date = new Date();
            if (out.getValue() != null) {
                String update = SparqlQueryFactory.sparqlUpdateData("mes_data_" + dataId, out.getId(), out.getValue().toString(), out.getDataType(), date.getTime());
                UpdateRequest request = UpdateFactory.create(update);
                UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
                dataId += 1;
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }

    }

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
                ArrayList<String> value = new ArrayList<>();
                RDFNode node = soln.get(valueLabel);
                if (node != null) {
                    value.add(correctString(node.toString().replace("http://www.escop-project.eu/MES.owl#", "")));
                }
                variables.put(valueLabel, value);
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
