package edu.nwpu.domain;

import java.io.Serializable;

public class Order implements Serializable {
    //订单编号
    private String ID;
    //起始地，即餐馆位置
    private String StartLocation;
    //目的地，即用户位置
    private String TerminalLocation;

    //无人机离开无人机站的时刻
    private String tdp;
    //无人机完成外卖拾取的时刻
    private String tpk;
    //无人机完成外卖投递的时刻
    private String tdl;
    //无人机返回无人机站的时刻
    private String trt;

    public Order(){}

    public Order(String ID, String startLocation, String terminalLocation, String tdp, String tpk, String tdl, String trt) {
        this.ID = ID;
        StartLocation = startLocation;
        TerminalLocation = terminalLocation;
        this.tdp = tdp;
        this.tpk = tpk;
        this.tdl = tdl;
        this.trt = trt;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStartLocation() {
        return StartLocation;
    }

    public void setStartLocation(String startLocation) {
        StartLocation = startLocation;
    }

    public String getTerminalLocation() {
        return TerminalLocation;
    }

    public void setTerminalLocation(String terminalLocation) {
        TerminalLocation = terminalLocation;
    }

    public String getTdp() {
        return tdp;
    }

    public void setTdp(String tdp) {
        this.tdp = tdp;
    }

    public String getTpk() {
        return tpk;
    }

    public void setTpk(String tpk) {
        this.tpk = tpk;
    }

    public String getTdl() {
        return tdl;
    }

    public void setTdl(String tdl) {
        this.tdl = tdl;
    }

    public String getTrt() {
        return trt;
    }

    public void setTrt(String trt) {
        this.trt = trt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "ID='" + ID + '\'' +
                ", StartLocation='" + StartLocation + '\'' +
                ", TerminalLocation='" + TerminalLocation + '\'' +
                ", tdp='" + tdp + '\'' +
                ", tpk='" + tpk + '\'' +
                ", tdl='" + tdl + '\'' +
                ", trt='" + trt + '\'' +
                '}';
    }
}
