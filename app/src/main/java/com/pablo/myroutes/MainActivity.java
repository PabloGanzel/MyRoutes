package com.pablo.myroutes;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IFragmentsInteractionListener {

    private enum iconColor {Red, Green}

    NotificationManager notificationManager;
    NotificationChannel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            AppData.addressList = (ArrayList<String>) Helper.getObjectByTag(AppData.ADDRESS_LIST_TAG, getBaseContext());
        } catch (Exception e) {
            AppData.addressList = new ArrayList<>();
        }

        try {
            AppData.routingDaysList = (ArrayList<RoutingDay>) Helper.getObjectByTag(AppData.DAYS_LIST_TAG, getBaseContext());
            //kilometrageStartDay = AppData.routingDaysList.get(AppData.routingDaysList.size() - 1).getKilometrageOnEndingDay();
        } catch (Exception e) {
            AppData.routingDaysList = new ArrayList<>();
        }

        try {
            AppData.routingDay = (RoutingDay) Helper.getObjectByTag(AppData.CURRENT_DAY_TAG, getBaseContext());//routingDay = (RoutingDay) deSerializeObject(CURRENT_DAY_TAG);

        } catch (Exception ignored) {
        }

        try {
            AppData.route = (Route) Helper.getObjectByTag(AppData.CURRENT_ROUTE_TAG, getBaseContext()); //route = (Route) deSerializeObject(CURRENT_ROUTE_TAG);

        } catch (Exception e) {
            //routingDaysList = new ArrayList<RoutingDay>();
        }

        if (AppData.routingDay != null && AppData.routingDay.isOpen()) {
            //route = routingDay.getLastRoute();
            if (AppData.route != null && AppData.route.isOpen()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, OpenedRouteFragment.newInstance(), "OpenedRouteFragment").commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ClosedRouteFragment.newInstance(), "ClosedRouteFragment").commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ClosedDayFragment.newInstance(), "ClosedDayFragment").commit();
        }

        //////////////////////////////////////блок для теста
        //Backup.getFiles();
        //////////////////////////////////////блок для теста
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (AppData.routingDay != null) {
            if (!AppData.routingDay.isOpen()) {
                menu.getItem(0).getSubMenu().getItem(1).setEnabled(false);
            } else menu.getItem(0).getSubMenu().getItem(1).setEnabled(true);
        } else menu.getItem(0).getSubMenu().getItem(1).setEnabled(false);
        if (AppData.routingDaysList.size() == 0) {
            menu.getItem(0).getSubMenu().getItem(0).setEnabled(false);
        } else menu.getItem(0).getSubMenu().getItem(0).setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.edit_closed_days) {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("type", "RoutingDaysListFragment");
            startActivity(intent);
            return true;
        }
        if (id == R.id.edit_current_day) {

            //if (AppData.routingDay.isOpen()) {
            //item.setEnabled(true);
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("type", "RoutesListFragment");
            startActivity(intent);
            return true;
            //} else item.setEnabled(false);
        }
        if (id == R.id.edit_address_list) {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            intent.putExtra("type", "AddressListFragment");
            startActivity(intent);
            return true;
        }

        if (id == R.id.save) {
            try {
                Backup.saveData(new Object[]{AppData.routingDaysList, AppData.routingDay, AppData.route, AppData.addressList});
                Toast.makeText(getBaseContext(), "Сохранено " + Helper.getNumbericDate() + ".rbackup", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.restore) {
            final String[] filesNames = Backup.getFiles();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Выберите файл восстановления:")
                    .setItems(filesNames, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Object[] objects = Backup.restore(which);
                                if (objects[0] != null) AppData.routingDaysList = (ArrayList<RoutingDay>) objects[0];
                                if (objects[1] != null) AppData.routingDay = (RoutingDay) objects[1];
                                if (objects[2] != null) AppData.route = (Route) objects[2];
                                if (objects[3] != null) AppData.addressList = (ArrayList<String>) objects[3];

                                Helper.saveObject(AppData.routingDay, AppData.CURRENT_DAY_TAG, getBaseContext());
                                Helper.saveObject(AppData.route, AppData.CURRENT_ROUTE_TAG, getBaseContext());
                                Helper.saveObject(AppData.routingDaysList, AppData.DAYS_LIST_TAG, getBaseContext());
                                Helper.saveObject(AppData.addressList, AppData.ADDRESS_LIST_TAG, getBaseContext());
                                Toast.makeText(getBaseContext(),
                                        "Восстановлено " + filesNames[which],
                                        Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                            } catch (Exception ex) {
                                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            builder.create().show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            Helper.saveObject(AppData.routingDay, AppData.CURRENT_DAY_TAG, getBaseContext());
            Helper.saveObject(AppData.route, AppData.CURRENT_ROUTE_TAG, getBaseContext());
            Helper.saveObject(AppData.addressList, AppData.ADDRESS_LIST_TAG, getBaseContext());

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (AppData.route != null) {
            if (AppData.route.isOpen()) {
                showNotification("Маршрут не завершен", iconColor.Red);
            } else if (AppData.routingDay != null) {
                if (AppData.routingDay.isOpen()) {
                    showNotification("День не завершен", iconColor.Green);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AppData.routingDaysList = (ArrayList<RoutingDay>) Helper.getObjectByTag(AppData.DAYS_LIST_TAG, getBaseContext());
            //kilometrageStartDay = AppData.routingDaysList.get(AppData.routingDaysList.size() - 1).getKilometrageOnEndingDay();
        } catch (Exception e) {
            AppData.routingDaysList = new ArrayList<>();
        }

        try {
            AppData.routingDay = (RoutingDay) Helper.getObjectByTag(AppData.CURRENT_DAY_TAG, getBaseContext());//routingDay = (RoutingDay) deSerializeObject(CURRENT_DAY_TAG);

        } catch (Exception ignored) {
        }
        try {
            AppData.addressList = (ArrayList<String>) Helper.getObjectByTag(AppData.ADDRESS_LIST_TAG, getBaseContext());
        } catch (Exception e) {
            AppData.addressList = new ArrayList<>();
        }
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    @Override
    public void openRoutingDay(int kilometrageStartDay) {
        AppData.routingDay = new RoutingDay(Helper.getDate(), kilometrageStartDay);
        openRoute();
    }

    @Override
    public void openRoute() {
        String startPointAddress;
        int kilometrageStartRoute;
        try {
            startPointAddress = AppData.routingDay.getLastRoute().getEndPoint();
            kilometrageStartRoute = AppData.routingDay.getLastRoute().getEndKilometrage();
        } catch (Exception ex) {
            startPointAddress = AppData.DEFAULT_POINT_ADDRESS;
            kilometrageStartRoute = AppData.routingDay.getKilometrageOnBeginningDay();
        }
        AppData.route = new Route(startPointAddress, Helper.getTimeNow(), kilometrageStartRoute);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, OpenedRouteFragment.newInstance(), "OpenedRouteFragment").commit();
    }

    @Override
    public void closeRoute(String endPointAddress, int endKilometrage, String endTime) {

        AppData.route.setEndPoint(endPointAddress);
        AppData.route.setEndKilometrage(endKilometrage);
        AppData.route.setEndTime(endTime);
        AppData.route.close();

        AppData.routingDay.addRoute(AppData.route);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ClosedRouteFragment.newInstance(), "ClosedRouteFragment").commit();
    }

    //TODO: экспорт при закрытии дня
    @Override
    public void closeRoutingDay() {

        AppData.routingDay.setKilometrageOnEndingDay(AppData.routingDay.getLastRoute().getEndKilometrage());
        AppData.routingDay.close();
        //kilometrageStartDay = AppData.routingDay.getKilometrageOnEndingDay();
        AppData.routingDaysList.add(AppData.routingDay);
        try {
            Helper.saveObject(AppData.routingDaysList, AppData.DAYS_LIST_TAG, getBaseContext()); //serializeObject(routingDaysList,DAYS_LIST_TAG);
            //ExportService.Export(AppData.routingDay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, ClosedDayFragment.newInstance(), "ClosedDayFragment").commit();
    }

    private void showNotification(String message, iconColor IconColor) {

        int iconID;
        if (IconColor.equals(iconColor.Red)) {
            iconID = R.drawable.icon_l_red;
        } else {
            iconID = R.drawable.icon_l;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager == null) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }

            // There are hardcoding only for show it just strings
            String name = "channel";
            String id = "channel_ID"; // The user-visible name of the channel.
            String description = ""; // The user-visible description of the channel.

            Intent intent;
            PendingIntent pendingIntent;
            Notification.Builder builder;

            mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setDescription(description);
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new Notification.Builder(getBaseContext(), mChannel.getId());

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(message)  // required
                    .setSmallIcon(iconID) // required
                    .setContentText("")  // required
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.createNotificationChannel(mChannel);

            notificationManager.notify(1, builder.build());
        } else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(iconID)
                            .setContentTitle(message)
                            .setContentText("");

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }
}