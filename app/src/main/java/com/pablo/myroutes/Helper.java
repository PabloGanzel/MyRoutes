package com.pablo.myroutes;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class Helper {

    private static Pattern pattern = Pattern.compile("([01]?\\d|2[0-4]):([0-5]\\d)");

    static String getTimeNow() {
        final Calendar calendar = Calendar.getInstance();

        int minutes = calendar.get((Calendar.MINUTE));
        switch (minutes % 5) {
            case 1:
            case 2:
                minutes -= minutes % 5;
                break;
            case 3:
                minutes += 2;
                break;
            case 4:
                minutes += 1;
                break;
        }
        calendar.set(Calendar.MINUTE, minutes);

        return new SimpleDateFormat("HH:mm").format(calendar.getTime());
    }

    static String getDate() {
        final Calendar c = Calendar.getInstance();
        return String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + " " + c.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru")) + " " + String.valueOf(c.get(Calendar.YEAR));
    }

    static String getNumbericDate(){
        final Calendar c = Calendar.getInstance();
        return String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "_" + String.valueOf(c.get(Calendar.MONTH)+1) + "_" + String.valueOf(c.get(Calendar.YEAR));
    }

    static String getAddress() {
        return "Ломоносова 10";
    }

    static String getTimeDifference(String firstTime, String lastTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 1, 1, Integer.parseInt(firstTime.substring(0, 2)), Integer.parseInt(firstTime.substring(3, 5)));
        long _firstTime = calendar.getTime().getTime();
        calendar.set(2017, 1, 1, Integer.parseInt(lastTime.substring(0, 2)), Integer.parseInt(lastTime.substring(3, 5)));
        long _lastTime = calendar.getTime().getTime();

        return String.valueOf(((_lastTime - _firstTime) / 1000) / 60);
    }

    static Object getObjectByTag(String tag, Context context) throws Exception {
        //FileInputStream ffis = new FileInputStream(tag);
        FileInputStream fis = context.openFileInput(tag);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        ois.close();
        fis.close();
        return object;
    }

    static void saveObject(Object object, String tag, Context context) throws Exception {
        FileOutputStream fos = context.openFileOutput(tag, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    static boolean isKilometragePositive(String start, String end) {
        return Integer.parseInt(end) >= Integer.parseInt(start);
    }

    static boolean isTimeCorrect(String start, String end) {
        if (!pattern.matcher(end).matches()) {
            return false;
        }

        java.text.DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            dateFormat.parse(end);
            return !dateFormat.parse(end).before(dateFormat.parse(start));
        }
        catch (ParseException ex){
            return false;
        }
    }
}