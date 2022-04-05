package edu.nwpu.bean;

import edu.nwpu.domain.Bus;

import java.io.Serializable;

public class DistanceAndTime implements Serializable {

    Bus start;
    Bus end;
    Double distance;
    Integer time;

    public DistanceAndTime(Bus start, Bus end, Double distance, int time) {
        this.start = start;
        this.end = end;
        this.distance = distance;
        this.time = time;
    }

    public DistanceAndTime() {
    }

    public Bus getStart() {
        return start;
    }

    public void setStart(Bus start) {
        this.start = start;
    }

    public Bus getEnd() {
        return end;
    }

    public void setEnd(Bus end) {
        this.end = end;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "{" +
                "startPoint=" + start +
                ", endPoint=" + end +
                ", tripDistance=" + distance +
                ", tripTime=" + time +
                "}\n\n";
    }
}
