package edu.nwpu.bean;

import edu.nwpu.domain.Bus;

import java.io.Serializable;

public class Path implements Serializable {
    private Bus startPoint;
    private Bus endPoint;
    private Bus menMinPoint;
    private Bus resMinPoint;
    private Bus uavMinPoint;

    public Path(Bus startPoint, Bus endPoint, Bus menMinPoint, Bus resMinPoint, Bus uavMinPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.menMinPoint = menMinPoint;
        this.resMinPoint = resMinPoint;
        this.uavMinPoint = uavMinPoint;
    }

    public Path(Bus startPoint, Bus endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Path() {
    }

    @Override
    public String toString() {
        return "\n\nPath{" +
                "startPoint=" + startPoint +
                ", \nendPoint=" + endPoint +
                ", \nmenMinPoint=" + menMinPoint +
                ", \nresMinPoint=" + resMinPoint +
                ", \nuavMinPoint=" + uavMinPoint +
                '}';
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

    public Bus getMenMinPoint() {
        return menMinPoint;
    }

    public void setMenMinPoint(Bus menMinPoint) {
        this.menMinPoint = menMinPoint;
    }

    public Bus getResMinPoint() {
        return resMinPoint;
    }

    public void setResMinPoint(Bus resMinPoint) {
        this.resMinPoint = resMinPoint;
    }

    public Bus getUavMinPoint() {
        return uavMinPoint;
    }

    public void setUavMinPoint(Bus uavMinPoint) {
        this.uavMinPoint = uavMinPoint;
    }
}
