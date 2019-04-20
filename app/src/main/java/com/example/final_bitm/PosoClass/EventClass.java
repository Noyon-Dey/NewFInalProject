package com.example.final_bitm.PosoClass;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nizam Uddin Shamrat on 1/30/2018.
 */

public class EventClass implements Serializable {

    String eventId;
    String eventName;
    String startingLocation;
    String destination;
    String departureDate;
    String startedDate;
    int daysLeft;
    double budget;


    public EventClass() {
    }

    public EventClass(String eventId, String eventName, String startingLocation, String destination, String departureDate, String startedDate, int daysLeft, double budget) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.startingLocation = startingLocation;
        this.destination = destination;
        this.departureDate = departureDate;
        this.startedDate = startedDate;
        this.daysLeft = daysLeft;
        this.budget = budget;
    }

    public EventClass(String eventId, String eventName, String startingLocation, String destination, String departureDate, String startedDate, double budget) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.startingLocation = startingLocation;
        this.destination = destination;
        this.departureDate = departureDate;
        this.startedDate = startedDate;
        this.budget = budget;
    }

    public EventClass(String eventName, String startingLocation, String destination, String departureDate, String startedDate, double budget) {
        this.eventName = eventName;
        this.startingLocation = startingLocation;
        this.destination = destination;
        this.departureDate = departureDate;
        this.startedDate = startedDate;
        this.budget = budget;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(String startedDate) {
        this.startedDate = startedDate;
    }

    public int getDaysLeft() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM, yyyy");
        String dateInString = departureDate;
        Date date = null;
        try {
            date = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        long depDateMillis=calendar.getTimeInMillis();
        long currentTimeMillis= System.currentTimeMillis();
        daysLeft=(int) (Math.ceil((depDateMillis-currentTimeMillis)/(60.0*60.0*24.0*1000.0)));
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
