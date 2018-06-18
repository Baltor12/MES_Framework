/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.json.swagger.service;

/**
 * Class to represent the payload for services
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class ServicePayload {
    int count;
    long lastRun;

    public ServicePayload() {
    }

    public ServicePayload(int count, long lastRun) {
        this.count = count;
        this.lastRun = lastRun;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLastRun() {
        return lastRun;
    }

    public void setLastRun(long lastRun) {
        this.lastRun = lastRun;
    }
    
}
