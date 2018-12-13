package com.pablo.myroutes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ClosedDayFragment extends Fragment {

    private IFragmentsInteractionListener mListener;

    EditText editText;

    public ClosedDayFragment() {
        // Required empty public constructor
    }

    public static ClosedDayFragment newInstance() {
        return new ClosedDayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closed_day, container, false);
        editText = view.findViewById(R.id.editTextKilometrageOnBeginningDay);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        Button b = view.findViewById(R.id.buttonBeginningDay);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    int kilometrage = Integer.parseInt(editText.getText().toString());
                    mListener.openRoutingDay(kilometrage);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentsInteractionListener) {
            mListener = (IFragmentsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){

        super.onResume();
        if (AppData.routingDaysList.size()==0) {
            editText.setText("0");
        } else {
            editText.setText(String.valueOf(AppData.routingDaysList.get(AppData.routingDaysList.size() - 1).getKilometrageOnEndingDay()));
        }
    }
}
