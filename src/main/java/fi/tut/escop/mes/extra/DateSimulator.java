/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.extra;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that simulates the time for OLS Simulators
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class DateSimulator implements Runnable {

    public static long dateTime;
    private Thread thread;
    Date date = new Date();
    private static final Logger LOG = Logger.getLogger(DateSimulator.class.getName());

    public DateSimulator() {
        DateSimulator.dateTime = date.getTime();
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        DateSimulator.dateTime = dateTime;
//        DateSimulator.dateTime = date.getTime();
    }

    @Override
    public void run() {
        try {
            while (true) {
                DateSimulator.dateTime += 60000;
//                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(DateSimulator.class.getName()).log(Level.SEVERE, "Exception", ex);
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
