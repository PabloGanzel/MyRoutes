package com.pablo.myroutes;

/**
 * Created by Paul on 24.07.2017.
 */

public interface IFragmentsInteractionListener {

    void openRoutingDay(int kilometrageStartDay);

    void openRoute();

    void closeRoute(String endPointAddress, int endKilometrage, String endTime);

    void closeRoutingDay();

}
