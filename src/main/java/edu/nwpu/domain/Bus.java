package edu.nwpu.domain;

import java.io.Serializable;

public class Bus implements Serializable {

    private String PlateId;
    private String Date;
    private float Latitude;
    private float Longitude;
    private float Speed;

    public Bus(String plateId, String date, float latitude, float longitude, float speed) {
        PlateId = plateId;
        Date = date;
        Latitude = latitude;
        Longitude = longitude;
        Speed = speed;
    }

    public Bus() {
    }

    public String getPlateId() {
        return PlateId;
    }

    public void setPlateId(String plateId) {
        PlateId = plateId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public float getLatitude() {
        return Latitude;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float speed) {
        Speed = speed;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "PlateId='" + PlateId + '\'' +
                ", Date='" + Date + '\'' +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                ", Speed=" + Speed +
                '}';
    }
}
