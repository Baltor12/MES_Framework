/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.ontology;

import java.util.ArrayList;

/**
 * Class That Generates SPARQL Query/Update as per the request from Ontology
 * Manager
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class SparqlQueryFactory {

    private static final String PREFIX = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
            + "PREFIX mes:<http://www.escop-project.eu/MES.owl#>\n"
            + "PREFIX mdata:<http://www.escop-project.eu/MES-DATA.owl#>\n";

    /**
     * Function to generate the SPARQL Query for getting List of functions
     *
     * @return
     */
    public static String sparqlQueryMESFunction() {
        String query = PREFIX;
        query += "SELECT ?func\n"
                + "WHERE{\n"
                + " ?fun a mes:Function.\n"
                + " ?fun mes:id ?func.\n"
                + "}\n";
        return query;
    }
    
    /**
     * Function to generate the SPARQL Query for getting List of Devices
     *
     * @return
     */
    public static String sparqlQueryDevices() {
        String query = PREFIX;
        query += "SELECT ?device\n"
                + "WHERE{\n"
                + " ?dev a mes:Device.\n"
                + " ?dev mes:id ?device.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting List of Rules for each
     * function
     *
     * @param function
     * @return
     */
    public static String sparqlQueryRulesofFunction(String function) {
        String query = PREFIX;
        query += "SELECT ?rule ?action ?formula ?service\n"
                + "WHERE{\n"
                + " ?fun a mes:Function.\n"
                + " ?fun mes:id \"" + function + "\"^^xsd:string.\n"
                + " ?fun mes:hasRule ?rules.\n"
                + " ?rules mes:id ?rule.\n"
                + " ?rules mes:hasAction ?action.\n"
                + " ?rules mes:formula ?formula.\n"
                + " OPTIONAL {?rules mes:hasService ?service}.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting List of outputs for
     * each rule
     *
     * @param rule
     * @return
     */
    public static String sparqlQueryOutputsofRule(String rule) {
        String query = PREFIX;
        query += "SELECT ?outputs\n"
                + "WHERE{\n"
                + " ?rul a mes:Rule.\n"
                + " ?rul mes:id \"" + rule + "\"^^xsd:string.\n"
                + " ?rul mes:hasOutput ?outputs.\n"
                + "}\n";
        return query;
    }
    
    /**
     * Function to query list of inputs form ontology
     * 
     * @return 
     */
    public static String sparqlQueryInputs() {
        String query = PREFIX;
        query += "SELECT ?inputs\n"
                + "WHERE{\n"
                + " ?inputs a mes:Input.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting List of inputs for each
     * rule
     *
     * @param rule
     * @return
     */
    public static String sparqlQueryInputsofRule(String rule) {
        String query = PREFIX;
        query += "SELECT ?inputs\n"
                + "WHERE{\n"
                + " ?rul a mes:Rule.\n"
                + " ?rul mes:id \"" + rule + "\"^^xsd:string.\n"
                + " ?rul mes:hasInput ?inputs.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting List of meta for each
     * rule
     *
     * @param rule
     * @return
     */
    public static String sparqlQueryMetasofRule(String rule) {
        String query = PREFIX;
        query += "SELECT ?metaId ?metaVal \n"
                + "WHERE{\n"
                + " ?rul a mes:Rule.\n"
                + " ?rul mes:id \"" + rule + "\"^^xsd:string.\n"
                + " ?rul mes:hasMeta ?metas.\n"
                + " ?metas mes:id ?metaId.\n"
                + " ?metas mes:value ?metaVal.\n"
                + "}\n";
        return query;
    }
    
    

    /**
     * Function to generate the SPARQL Query for getting details of Particular
     * Output
     *
     * @param output
     * @return
     */
    public static String sparqlQueryOutputDetails(String output) {
        String query = PREFIX;
        query += "SELECT ?id ?category ?type ?kpi ?max ?min ?nom ?value\n"
                + "WHERE{\n"
                + " mes:" + output + " mes:id ?id.\n"
                + " mes:" + output + " mes:hasType ?type.\n"
                + " mes:" + output + " mes:hasCategory ?cate.\n"
                + " ?cate mes:id ?category.\n"
                + " OPTIONAL {mes:" + output + " mes:max ?max}.\n"
                + " OPTIONAL {mes:" + output + " mes:min ?min}.\n"
                + " OPTIONAL {mes:" + output + " mes:nom ?nom}.\n"
                + " OPTIONAL {mes:" + output + " mes:isKPI ?kpi}.\n"
                + " OPTIONAL {mes:" + output + " mes:value ?value}.\n"
                + "}\n";
        return query;
    }
    
    /**
     * Function to generate the SPARQL Query for getting details of Particular
     * Output
     *
     * @param output
     * @return
     */
    public static String sparqlQueryOutputKPIDetails(String output) {
        String query = PREFIX;
        query += "SELECT ?id ?parent ?children ?title ?symbol ?range ?unit\n"
                + "WHERE{\n"
                + " mes:" + output + " mes:hasKPISymbol ?symbol.\n"
                + " OPTIONAL {mes:" + output + " mes:kpiId ?id.}.\n"
                + " OPTIONAL {mes:" + output + " mes:kpiTitle ?title.}.\n"
                + " OPTIONAL {mes:" + output + " mes:hasKPIChildren ?children}.\n"
                + " OPTIONAL {mes:" + output + " mes:hasKPIParent ?parent}.\n"
                + " OPTIONAL {mes:" + output + " mes:hasKPIRange ?range}.\n"
                + " OPTIONAL {mes:" + output + " mes:kpiUnits ?unit}.\n"
                + "}\n";
        return query;
    }
    
    /**
     * Function to generate the SPARQL Query for getting details of Particular
     * KPI range
     *
     * @param kpiRange
     * @return
     */
    public static String sparqlQueryKPIRangeDetails(String kpiRange) {
        String query = PREFIX;
        query += "SELECT ?id ?hh ?hn ?nn ?nl ?ll\n"
                + "WHERE{\n"
                + " mes:" + kpiRange + " mes:id ?id.\n"
                + " mes:" + kpiRange + " mes:hh ?hh.\n"
                + " mes:" + kpiRange + " mes:hn ?hn.\n"
                + " mes:" + kpiRange + " mes:nn ?nn.\n"
                + " mes:" + kpiRange + " mes:nl ?nl.\n"
                + " mes:" + kpiRange + " mes:ll ?ll.\n"
                + "}\n";
        return query;
    }
    
    /**
     * Function to generate the SPARQL Query for getting details of Particular
     * service
     *
     * @param service
     * @return
     */
    public static String sparqlQueryServiceDetails(String service) {
        String query = PREFIX;
        query += "SELECT ?id ?category ?meta \n"
                + "WHERE{\n"
                + " mes:" + service + " mes:id ?id.\n"
                + " mes:" + service + " mes:hasMeta ?meta.\n"
                + " mes:" + service + " mes:hasCategory ?cate.\n"
                + " ?cate mes:id ?category.\n"
                + "}\n";
        return query;
    }
    
    /**
     * Function to generate the SPARQL Query for getting meta details of Particular
     * service
     *
     * @param service
     * @return
     */
    public static String sparqlQueryMetasofService(String service) {
        String query = PREFIX;
        query += "SELECT ?metaId ?metaVal \n"
                + "WHERE{\n"
                + " mes:" + service + " mes:hasMeta ?meta.\n"
                + " ?meta mes:id ?metaId.\n"
                + " ?meta mes:value ?metaVal.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting details of Particular
     * Input
     *
     * @param input
     * @return
     */
    public static String sparqlQueryInputDetails(String input) {
        String query = PREFIX;
        query += "SELECT ?id ?type ?category ?value ?formulaId ?url\n"
                + "WHERE{\n"
                + " mes:" + input + " mes:id ?id.\n"
                + " mes:" + input + " mes:hasType ?type.\n"
                + " mes:" + input + " mes:hasCategory ?cate.\n"
                + " ?cate mes:id ?category.\n"
                + " OPTIONAL {mes:" + input + " mes:formulaId ?formulaId}.\n"
                + " OPTIONAL {mes:" + input + " mes:value ?value}.\n"
                + " OPTIONAL {mes:" + input + " mes:url ?url}.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting List of meta for each
     * input
     *
     * @param input
     * @return
     */
    public static String sparqlQueryMetasofInput(String input) {
        String query = PREFIX;
        query += "SELECT ?metaId ?metaVal \n"
                + "WHERE{\n"
                + " mes:" + input + " mes:hasMeta ?metas.\n"
                + " ?metas mes:id ?metaId.\n"
                + " ?metas mes:value ?metaVal.\n"
                + "}\n";
        return query;
    }
    
    /**
     * Function to generate the SPARQL Query for getting List of meta for each
     * input
     *
     * @param input
     * @return
     */
    public static String sparqlQueryforData(String id) {
        String query = PREFIX;
        query += "SELECT ?type ?value ?date\n"
                + "WHERE{\n"
                + " ?data a mdata:Data.\n"
                + " ?data mdata:id \"" + id + "\"^^xsd:string.\n"
                + " ?data mdata:value ?value.\n"
                + " ?data mdata:type ?type.\n"
                + " ?data mdata:date ?date.\n"
                + "}\n"
                + "ORDER BY DESC(?date)";
        return query;
    }    
    
    /**
     * SPARQL update for updating data in ontology
     * 
     * @param ontoId
     * @param dataId
     * @param value
     * @param type
     * @param date
     * @return 
     */
    public static String sparqlUpdateData(String ontoId, String dataId, String value, String type, long date) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " mdata:" + ontoId + " a mdata:Data.\n"
                + " mdata:" + ontoId + " mdata:id \"" + dataId + "\"^^xsd:string.\n"
                + " mdata:" + ontoId + " mdata:value \"" + value + "\"^^xsd:string.\n"
                + " mdata:" + ontoId + " mdata:type \"" + type + "\"^^xsd:string.\n"
                + " mdata:" + ontoId + " mdata:date \"" + date + "\"^^xsd:long.\n";
        query += "}";
        return query;
    }
}
