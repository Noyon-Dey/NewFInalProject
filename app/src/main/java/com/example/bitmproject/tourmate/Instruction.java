package com.example.bitmproject.tourmate;

import java.io.Serializable;

/**
 * Created by saran on 2/6/2018.
 */

public class Instruction implements Serializable {
    String instruction;
    double lat;
    double lng;

    public Instruction(String instruction) {
        this.instruction = instruction;
    }

    public Instruction(String instruction, double lat, double lng) {
        this.instruction = instruction;
        this.lat = lat;
        this.lng = lng;
    }

    public Instruction() {
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
