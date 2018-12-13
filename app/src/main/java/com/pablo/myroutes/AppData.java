package com.pablo.myroutes;

import java.util.ArrayList;

public class AppData {

    static final String DEFAULT_POINT_ADDRESS = "проспект Ломоносова, 183к1";

    public static final String DAYS_LIST_TAG = "list_of_day";
    public static final String CURRENT_DAY_TAG = "current_day";
    public static final String CURRENT_ROUTE_TAG = "current_route";
    public static final String ADDRESS_LIST_TAG = "list_of_addresses";

    public static RoutingDay routingDay;
    public static Route route;
    public static ArrayList<RoutingDay> routingDaysList;
    public static ArrayList<String> addressList;

}
