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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Paul on 23.11.2017.
 */

public class RoutingDaysListFragment extends ListFragment {

    private static final String ARG_PARAM = "param";

    ListAdapter singleChoiceAdapter;
    ListAdapter multiChoiceAdapter;

    private ArrayList<Integer> indexesSelectedObjects;
    private ArrayList<String> dateList;

    public static RoutingDaysListFragment newInstance() {

        return new RoutingDaysListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* / получение массива дат */
        if (AppData.routingDaysList.size() != 0) {
            dateList = new ArrayList<>();
            try {
                for (int i = 0; i < AppData.routingDaysList.size(); i++) {
                    dateList.add(AppData.routingDaysList.get(i).date);
                }
            } catch (Exception e) {
            }


            singleChoiceAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, dateList);
            multiChoiceAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.my_list_item_multiplie_choice, dateList);
            setListAdapter(singleChoiceAdapter);
        }
        final ListView listView = getListView();
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            /* / обработка долгого нажатия на элемент */
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
////                if (getListAdapter().equals(singleChoiceAdapter)) {
////                    setListAdapter(multiChoiceAdapter);
////                    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
////                    //listView.setSelection(i);
////                    listView.setItemChecked(i, true);
////                    indexesSelectedObjects.add(i);
////                }
//                //deleting();
//                return true;
//            }
//        });
        getActivity().setTitle("Редактирование");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete_export, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                return delete();
            case R.id.action_export:
                //
                return export();
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }

    /* / обработка нажатия на элемент */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (getListAdapter().equals(singleChoiceAdapter)) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout,
                            RoutesListFragment.newInstance(position),
                            //MyListFragment.newInstance(position),
                            "RoutesListFragment")
                    .addToBackStack(null)
                    .commit();
        } else {
            try {
                SparseBooleanArray chosen = getListView().getCheckedItemPositions();
                indexesSelectedObjects = new ArrayList<>();
                for (int i = 0; i < chosen.size(); i++) {
                    if (chosen.valueAt(i)) {
                        indexesSelectedObjects.add(chosen.keyAt(i));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                            for (int i = indexesSelectedObjects.size() - 1; i >= 0; i--) {
                                int ifd = indexesSelectedObjects.get(i);
                                AppData.routingDaysList.remove(ifd);
                                dateList.remove(ifd);

                            }
                            Helper.saveObject(AppData.routingDaysList, AppData.DAYS_LIST_TAG, getContext());
                            //Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        setListAdapter(singleChoiceAdapter);
                        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        indexesSelectedObjects = null;

                        getActivity().onBackPressed();
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
            else {
                setListAdapter(singleChoiceAdapter);
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                indexesSelectedObjects = null;
            }
        }
        return true;
    }

    private boolean export() {
        if (getListAdapter() == singleChoiceAdapter) {
            Toast.makeText(getContext(), R.string.choice_objects_for_exporting, Toast.LENGTH_SHORT).show();
            setListAdapter(multiChoiceAdapter);
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        } else if (getListAdapter() == multiChoiceAdapter) {

            if (indexesSelectedObjects != null && indexesSelectedObjects.size() != 0) {
                try {
                    final ArrayList<RoutingDay> rdl = new ArrayList<>();
                    for (int i = 0; i < indexesSelectedObjects.size(); i++) {
                        //for (int i = indexesSelectedObjects.size() - 1; i >= 0; i--) {
                        rdl.add(AppData.routingDaysList.get(indexesSelectedObjects.get(i)));
                    }
                    new ExportAsyncTask().execute(rdl.toArray(new RoutingDay[rdl.size()]));

                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            setListAdapter(singleChoiceAdapter);
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            indexesSelectedObjects = null;
        }
        return true;
    }
//

    private class ExportAsyncTask extends AsyncTask<RoutingDay[], String, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "Экспорт будет произведен в фоновом режиме", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(RoutingDay[]... routingDays) {
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