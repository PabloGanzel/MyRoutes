package com.pablo.myroutes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class AddressListFragment extends ListFragment {

    ListAdapter singleChoiceAdapter;
    ListAdapter multiChoiceAdapter;

    private ArrayList<Integer> indexesSelectedObjects;

    public static AddressListFragment newInstance() {
        return new AddressListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Адреса");

        Collections.sort(AppData.addressList);

        singleChoiceAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, AppData.addressList);
        multiChoiceAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.my_list_item_multiplie_choice, AppData.addressList);

        setListAdapter(singleChoiceAdapter);
        ListView l = getListView();
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            /* / обработка долгого нажатия на элемент */
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //deleting();
                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                return delete();
            case R.id.action_add:
                return add();
            default:
                // Not one of ours. Perform default menu processing
                return super.onOptionsItemSelected(item);
        }
    }

    /* / обработка нажатия на элемент */
    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (getListAdapter().equals(singleChoiceAdapter)) {

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

            alert.setTitle("Редактировать");

            final EditText input = new EditText(getContext());
            input.setText(AppData.addressList.get(position));
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    AppData.addressList.set(position, value);
                    try {
                        Helper.saveObject(AppData.addressList, AppData.ADDRESS_LIST_TAG, getContext());
                        Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                        Collections.sort(AppData.addressList);
                        setListAdapter(singleChoiceAdapter);
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.show();
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
    }

    @Override
    public void onResume(){
        super.onResume();
        Collections.sort(AppData.addressList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
                                AppData.addressList.remove(ifd);
                            }
                            Helper.saveObject(AppData.addressList, AppData.ADDRESS_LIST_TAG, getContext());
                            Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    private boolean add(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle("Новый адрес");

        final EditText input = new EditText(getContext());
        //input.setText(AppData.addressList.get(position));
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                AppData.addressList.add(value);
                try {
                    Helper.saveObject(AppData.addressList, AppData.ADDRESS_LIST_TAG, getContext());
                    Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                    Collections.sort(AppData.addressList);
                    setListAdapter(singleChoiceAdapter);
                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();

        return true;
    }
}