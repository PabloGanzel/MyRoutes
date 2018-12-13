package com.pablo.myroutes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoutesListFragment extends ListFragment {

    ListAdapter singleChoiceAdapter;
    ListAdapter multiChoiceAdapter;

    private int dayId;
    private RoutingDay routingDay;
    private ArrayList<Integer> indexesSelectedObjects;
    ArrayList<Map<String, String>> routesTitleArrayList;

    public RoutesListFragment() {
        // Required empty public constructor
    }

    public static RoutesListFragment newInstance(int dayId) {
        RoutesListFragment fragment = new RoutesListFragment();
        Bundle args = new Bundle();
        args.putInt("param", dayId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            dayId = getArguments().getInt("param");
        }
        if(dayId==-1){
            routingDay=AppData.routingDay;
        }
        else {
        routingDay = AppData.routingDaysList.get(dayId);}
        routesTitleArrayList = new ArrayList<>();
        //String[] routes = new String[routingDay.getListOfRoutes().size()];
        for (int i = 0; i < routingDay.getListOfRoutes().size(); i++) {
            Map map = new HashMap<>();
            map.put("route",routingDay.getListOfRoutes().get(i).getStartPoint() + " - " + routingDay.getListOfRoutes().get(i).getEndPoint());
            map.put("info", routingDay.getListOfRoutes().get(i).getLength() + " км., " + routingDay.getListOfRoutes().get(i).getDuration() + " мин.");
            routesTitleArrayList.add(map);
        }

        singleChoiceAdapter = new SimpleAdapter(getContext(),
                routesTitleArrayList,
                android.R.layout.simple_list_item_2,
                new String[]{"route", "info"},
                new int[]{android.R.id.text1, android.R.id.text2});

        multiChoiceAdapter = new SimpleAdapter(getContext(),
                routesTitleArrayList,
                R.layout.my_list_item_multiplie_choice,
                new String[]{"route", "info"},
                new int[]{android.R.id.text1, android.R.id.text2});

        setListAdapter(singleChoiceAdapter);
        ListView l = getListView();
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //            /* / обработка долгого нажатия на элемент */
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //deleting();
                return true;
            }
        });

        getActivity().setTitle(routingDay.date);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete_export_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                return delete();
            case  R.id.action_export:
                return export();
            case R.id.action_add:
                //TODO: добавление нового маршрута
                Toast.makeText(getContext(),"не реализовано",Toast.LENGTH_SHORT).show();
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListAdapter().equals(singleChoiceAdapter)) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout,
                            EditRouteFragment.newInstance(dayId,
                                    position),
                            "EditRouteFragment")
                    .addToBackStack(null)
                    .commit();
        } else {
            //try {
            SparseBooleanArray chosen = getListView().getCheckedItemPositions();
            indexesSelectedObjects = new ArrayList<>();
            for (int i = 0; i < chosen.size(); i++) {
                if (chosen.valueAt(i)) {
                    indexesSelectedObjects.add(chosen.keyAt(i));
                }
            }
        }
        //} catch (Exception e) {
        //}
    }

    private boolean delete() {
        if (getListAdapter() == singleChoiceAdapter) {
            Toast.makeText(getContext(), R.string.choice_objects_for_deleting, Toast.LENGTH_SHORT).show();
            setListAdapter(multiChoiceAdapter);
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        } else if (getListAdapter() == multiChoiceAdapter) {

            if (indexesSelectedObjects != null && indexesSelectedObjects.size() != 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("Вы действительно хотите удалить данные?");

                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            if (dayId != -1) {
                                int index = AppData.routingDaysList.indexOf(AppData.routingDaysList.get(dayId));
                                for (int i = indexesSelectedObjects.size() - 1; i >= 0; i--) {
                                    int ifd = indexesSelectedObjects.get(i);
                                    routingDay.getListOfRoutes().remove(ifd);
                                    routesTitleArrayList.remove(ifd);
                                }
                                AppData.routingDaysList.set(index, routingDay);
                                Helper.saveObject(AppData.routingDaysList, AppData.DAYS_LIST_TAG, getContext());
                                Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = indexesSelectedObjects.size() - 1; i >= 0; i--) {
                                    int ifd = indexesSelectedObjects.get(i);
                                    //routingDay.getListOfRoutes().remove(ifd);
                                    //routesTitleArrayList.remove(ifd);
                                    AppData.routingDay.getListOfRoutes().remove(ifd);
                                    routesTitleArrayList.remove(ifd);
                                }

                                ////TODO: Решить проблему с пустым днем
                                if (AppData.routingDay.getListOfRoutes().size() == 0) {
                                    //AppData.routingDay = new RoutingDay(AppData.routingDay.date, routingDay.getKilometrageOnBeginningDay());
                                    //AppData.routingDay = null;


                                }
                                Helper.saveObject(AppData.routingDay, AppData.CURRENT_DAY_TAG, getContext());
                                Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();

                            }
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                        }
                        setListAdapter(singleChoiceAdapter);
                        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        indexesSelectedObjects = null;
                    }
                });

                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setListAdapter(singleChoiceAdapter);
                        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        indexesSelectedObjects = null;
                        // Canceled.
                    }
                });

                alert.show();
            }
            else{
                setListAdapter(singleChoiceAdapter);
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                indexesSelectedObjects = null;}
        }
        return true;
    }

    private boolean export() {

        new ExportAsyncTask().execute(routingDay);

        return true;
    }

    private class ExportAsyncTask extends AsyncTask<RoutingDay, String, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "Экспорт будет произведен в фоновом режиме", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(RoutingDay... routingDays) {
            try {
                ExportService.Export(routingDays[0]);
                return "Экспорт завершен";
            } catch (FileNotFoundException e) {
                return "Ошибка доступа к карте памяти";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
