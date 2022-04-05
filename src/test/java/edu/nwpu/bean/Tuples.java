package edu.nwpu.bean;

import edu.nwpu.domain.Bus;

public class Tuples {
    private Bus startPoint;
    private Bus endPoint;
    private double minTime;

    public Tuples(Bus startPoint, Bus endPoint, double minTime) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.minTime = minTime;
    }

    public Tuples() {
    }

    public Bus getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Bus startPoint) {
        this.startPoint = startPoint;
    }

    public Bus getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Bus endPoint) {
        this.endPoint = endPoint;
    }

    public double getMinTime() {
        return minTime;
    }

    public void setMinTime(double minTime) {
        this.minTime = minTime;
    }

}
