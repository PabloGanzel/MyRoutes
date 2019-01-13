package com.pablo.myroutes;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Route implements Serializable {

    private static final long serialVersionUID = 6174046319194954038L;

    private String startPoint;
    private String endPoint;
    private String startTime;
    private String endTime;
    private int startKilometrage;
    private int endKilometrage;
    private int length;
    private int duration;

    private boolean isOpen;

    Route(String startPoint, String startTime, int startKilometrage) {
        isOpen = true;
        this.startPoint = startPoint;
        this.startTime = startTime;
        this.startKilometrage = startKilometrage;
    }

    void close() {
        this.isOpen = false;
    }

    String getStartPoint() {
        return startPoint;
    }

    void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    String getEndPoint() {
        return endPoint;
    }

    void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    String getStartTime() {
        return startTime;
    }

    void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    String getEndTime() {
        return endTime;
    }

    void setEndTime(String endTime) {
        this.endTime = endTime;
        this.duration = Integer.parseInt(Helper.getTimeDifference(getStartTime(), getEndTime()));
    }

    int getStartKilometrage() {
        return startKilometrage;
    }

    public void setStartKilometrage(int startKilometrage) {
        this.startKilometrage = startKilometrage;
    }

    int getEndKilometrage() {
        return endKilometrage;
    }

    void setEndKilometrage(int endKilometrage) {
        this.endKilometrage = endKilometrage;
        this.length = this.endKilometrage - this.startKilometrage;
    }

    boolean isOpen() {
        return isOpen;
    }

    int getLength() {
        return length;
    }

    void setLength(int length) {
        this.length = length;
    }

    int getDuration() {
        return this.duration;
    }
}

class RoutingDay implements Serializable{

    private static final long serialVersionUID = -2506492275938281896L;

    String date;
    private int kilometrageOnBeginningDay;
    private int kilometrageOnEndingDay;
    private ArrayList<Route> listOfRoutes;
    private boolean isOpen;

    RoutingDay(String date, int kilometrageOnBeginningDay){
        this.listOfRoutes = new ArrayList();
        this.date = date;
        this.kilometrageOnBeginningDay = kilometrageOnBeginningDay;
        this.isOpen = true;
    }

    void addRoute(Route route){
        this.listOfRoutes.add(route);
    }

    void close() {
        this.isOpen = false;
    }

    int getKilometrageOnBeginningDay() {
        return kilometrageOnBeginningDay;
    }

    public void setKilometrageOnBeginningDay(int kilometrageOnBeginningDay) {
        this.kilometrageOnBeginningDay = kilometrageOnBeginningDay;
    }

    int getKilometrageOnEndingDay() {
        return kilometrageOnEndingDay;
    }

    void setKilometrageOnEndingDay(int kilometrageOnEndingDay) {
         this.kilometrageOnEndingDay = kilometrageOnEndingDay;
    }

    List<Route> getListOfRoutes() {
        return listOfRoutes;
    }

    boolean isOpen() {
        return isOpen;
    }

    Route getLastRoute(){
        return listOfRoutes.get(listOfRoutes.size()-1);
    }

    public boolean equals(Object o) {
        RoutingDay rDay = (RoutingDay) o;
        return (rDay.date.equals(this.date)&&
                rDay.getKilometrageOnBeginningDay() == this.getKilometrageOnBeginningDay()&&
                rDay.getKilometrageOnEndingDay() == this.getKilometrageOnEndingDay()&&
                (rDay.isOpen() == this.isOpen())
        );

    }
    public int hashCode()
    {
        return 76+133*getKilometrageOnBeginningDay();
    }
}