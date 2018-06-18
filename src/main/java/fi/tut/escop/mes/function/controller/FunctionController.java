/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.controller;

import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.elements.Function;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.function.elements.Input;
import fi.tut.escop.mes.json.swagger.BasicTree;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class that generates the REST web services for MES functions
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
@RestController
@RequestMapping("/framework")
public class FunctionController {

    String myUrl = HostPortandConfig.ROOT_URL;
    Function fn = null;

    //-----------------------------GET Methods----------------------------------
    /**
     * Getting the JSON representation of complete MES
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getMES() {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework";
        // Constructing the Main tree for MES in RTU type
        BasicTree mainRTU = new BasicTree("MMFramework", "node", basePath);
        mainRTU.createLinkWithoutParent(basePath, "");
        mainRTU.createMainChildren();
        response = new ResponseEntity<>(mainRTU, HttpStatus.OK);
        return response;
    }

    /**
     * Getting the JSON representation of complete MES with its Info
     *
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getMESInfo() {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework";
        // Constructing the Main tree for MES in RTU type
        BasicTree mainRTU = new BasicTree("MMFramework", "node", basePath);
        mainRTU.createLinkWithoutParent(basePath, "");
        mainRTU.createMainChildren();
        response = new ResponseEntity<>(mainRTU, HttpStatus.OK);
        return response;
    }

    /**
     * Getting the JSON representation of a function for its events and services
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getDetailsofFunction(@PathVariable String fnId) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework";
        BasicTree elementsRTU = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            // Constructing the Main tree for MES in RTU type
            elementsRTU = new BasicTree(fnId, "node", basePath + "/" + fnId);
            elementsRTU.createLinkWithParent(basePath, fnId);
            elementsRTU.createElementsChildren();
            response = new ResponseEntity<>(elementsRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementsRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a function for its events and services
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/info", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getDetailsofFunctionInfo(@PathVariable String fnId) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework";
        BasicTree elementsRTU = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            // Constructing the Main tree for MES in RTU type
            elementsRTU = new BasicTree(fnId, "node", basePath + "/" + fnId);
            elementsRTU.createLinkWithParent(basePath, fnId);
            elementsRTU.createElementsChildren();
            response = new ResponseEntity<>(elementsRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementsRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of complete MES function
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/api/swagger.json", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getApiDocs(@PathVariable String fnId
    ) throws UnsupportedEncodingException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        ResponseEntity<JSONObject> response;
        String basePath = "/framework/" + fnId;
        String json = "";
        HashMap<String, String> meta = new HashMap<>();
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream stream = classLoader.getResourceAsStream("min_swagger.json");
            JSONObject jsonObject = (JSONObject) parser.parse(
                    new InputStreamReader(stream, "UTF-8"));
            jsonObject.put("host", HostPortandConfig.ROOT_ADDRESS + ":" + HostPortandConfig.ROOT_PORT);
            jsonObject.put("basePath", basePath);
            JSONObject infoObject = (JSONObject) jsonObject.get("info");
            meta.put("deviceType", "MMFramework");
            meta.put("deviceId", fnId);
            infoObject.put("x-meta", meta);
            response = new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a function for its data
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/data", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getFunctionData(@PathVariable String fnId) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework" + "/" + fnId;
        BasicTree elementsRTU = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            // Constructing the Main tree for MES in RTU type
            elementsRTU = new BasicTree(fnId, "data", basePath + "/data");
            elementsRTU.createLinkWithParent(basePath, "data");
            elementsRTU.createElementsData();
            response = new ResponseEntity<>(elementsRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementsRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a function for its data
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/data/info", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getFunctionDataInfo(@PathVariable String fnId) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/framework";
        BasicTree elementsRTU = null;
        fn = null;
        if ((fn = ModuleRegistry.functions.get(fnId)) != null) {
            // Constructing the Main tree for MES in RTU type
            elementsRTU = new BasicTree(fnId, "data", basePath + "/data");
            elementsRTU.createLinkWithParent(basePath, "data");
            elementsRTU.createElementsData();
            response = new ResponseEntity<>(elementsRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementsRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a details in data
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/data/{dataId}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getParticularData(@PathVariable String fnId, @PathVariable String dataId) {
        ResponseEntity<HashMap<String, Object>> response;
        HashMap<String, Object> sensorValue = new HashMap<>();
        sensorValue = sensorData(fnId, dataId);
        if (!sensorValue.isEmpty()) {
            response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(sensorValue, HttpStatus.BAD_REQUEST);
        }
        return response;
    }
    
     /**
     * Getting the JSON representation of a details in data
     *
     * @param fnId
     * @return
     */
    @RequestMapping(value = "/{fnId}/data/{dataId}/info", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getParticularDataInfo(@PathVariable String fnId, @PathVariable String dataId) {
        ResponseEntity<HashMap<String, Object>> response;
        HashMap<String, Object> sensorValue = new HashMap<>();
        sensorValue = sensorData(fnId, dataId);
        if (!sensorValue.isEmpty()) {
            response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(sensorValue, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    public HashMap<String, Object> sensorData(String funcId, String senId) {
        HashMap<String, Object> sensorValue = new HashMap<>();
        HashMap<String, String> nestedMap = new HashMap<>();
        Input in = null;
        if ((in = ModuleRegistry.inputs.get(senId)) != null) {
            nestedMap.put("destUrl", in.getDestUrl());
            nestedMap.put("type", in.getDataType());
            sensorValue.put("id", senId);
            sensorValue.put("payload", nestedMap);            
        }
        return sensorValue;
    }
}
