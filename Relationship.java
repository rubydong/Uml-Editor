/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm;

import java.util.ArrayList;

/**
 *
 * @author Ruby
 */
public class Relationship {
    ArrayList<UmlDiagram> relationship;
    double beginning;
    double middle;
    double end;
    
    public Relationship(double beginning, double middle, double end) {
        this.beginning = beginning;
        this.middle = middle;
        this.end = end;
        relationship = new ArrayList<>();
    }
    
    public Relationship() {
        
    }
    
    public void setPoints(double beginning, double middle, double end) {
        this.beginning = beginning;
        this.middle = middle;
        this.end = end;
    }
    
    public double[] getPoints() {
        double [] array = {beginning, middle, end};
        return array;
    }

}
