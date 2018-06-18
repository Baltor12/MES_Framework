/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.mes.function.elements;

import fi.tut.escop.mes.function.administrator.ModuleRegistry;

/**
 * Class to represent the range format for KPI
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class KPIRange {
    String id;
    double max;
    double nom;
    double min;
    double hh;
    double hn;
    double nn;
    double nl;
    double ll;

    public KPIRange() {
    }

    public KPIRange(String id) {
        this.id = id;
    }

    public KPIRange(String id, double max, double nom, double min, double hh, double hn, double nn, double nl, double ll) {
        this.id = id;
        this.max = max;
        this.nom = nom;
        this.min = min;
        this.hh = hh;
        this.hn = hn;
        this.nn = nn;
        this.nl = nl;
        this.ll = ll;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getNom() {
        return nom;
    }

    public void setNom(double nom) {
        this.nom = nom;
    }

    public double getHh() {
        return hh;
    }

    public void setHh(double hh) {
        this.hh = hh;
    }

    public double getLl() {
        return ll;
    }

    public void setLl(double ll) {
        this.ll = ll;
    }

    public double getHn() {
        return hn;
    }

    public void setHn(double hn) {
        this.hn = hn;
    }

    public double getNn() {
        return nn;
    }

    public void setNn(double nn) {
        this.nn = nn;
    }

    public double getNl() {
        return nl;
    }

    public void setNl(double nl) {
        this.nl = nl;
    }
       
    public void reg(){
        ModuleRegistry.kpiRanges.put(this.id, this);
    }
    
}
