/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.discovery;

import fi.tut.escop.mes.constants.HostPortandConfig;
import fi.tut.escop.mes.function.administrator.ModuleRegistry;
import fi.tut.escop.mes.function.controller.FunctionRESTTemplate;
import java.io.IOException;

/**
 * Class to get the Probing message from the RPL
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class Probing {

    String dc;
    Integer cnt;

    String myUrl = HostPortandConfig.ROOT_URL;

    public Probing() {
    }

    public Probing(String dc, Integer cnt) {
        this.dc = dc;
        this.cnt = cnt;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public void postSwagger() throws IOException {
        if (!ModuleRegistry.functions.isEmpty()) {
            for (String funcKey : ModuleRegistry.functions.keySet()) {
                RegisterationMessage regMessageLU = new RegisterationMessage();
                regMessageLU.setCnt(this.cnt);
                regMessageLU.setId(funcKey);
                regMessageLU.setUrl(HostPortandConfig.ROOT_URL + "/framework/" + funcKey + "/api/swagger.json");
                FunctionRESTTemplate restTemplate = new FunctionRESTTemplate();
                restTemplate.probingResponsePOST(regMessageLU, dc);
            }
        }
    }
}
