package com.pablo.myroutes;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 24.07.2017.
 */

class Route implements Serializable {

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

    void close(){
        this.isOpen = false;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        this.duration = Integer.parseInt(Helper.getTimeDifference(getStartTime(),getEndTime()));
    }

    public int getStartKilometrage() {
        return startKilometrage;
    }

    public void setStartKilometrage(int startKilometrage) {
        this.startKilometrage = startKilometrage;
    }

    public int getEndKilometrage() {
        return endKilometrage;
    }

    public void setEndKilometrage(int endKilometrage) {
        this.endKilometrage = endKilometrage;
        this.length = this.endKilometrage - this.startKilometrage;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getLength() {
        return length;
    }
    public void setLength(int length){
        this.length = length;
    }
    public int getDuration(){
        return this.duration;
    }
}

class RoutingDay implements Serializable{

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

    public int getKilometrageOnBeginningDay() {
        return kilometrageOnBeginningDay;
    }

    public void setKilometrageOnBeginningDay(int kilometrageOnBeginningDay) {
        this.kilometrageOnBeginningDay = kilometrageOnBeginningDay;
    }

    public int getKilometrageOnEndingDay() {
        return kilometrageOnEndingDay;
    }

    public void setKilometrageOnEndingDay(int kilometrageOnEndingDay) {
        this.kilometrageOnEndingDay = kilometrageOnEndingDay;
    }

    public List<Route> getListOfRoutes() {
        return listOfRoutes;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Route getLastRoute(){
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