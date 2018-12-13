package com.pablo.myroutes;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ClosedRouteFragment extends Fragment {

    private IFragmentsInteractionListener mListener;

    TextView textViewStartPointAddress,
            textViewEndPointAddress,
            textViewTimeStart,
            textViewTimeEnd,
            textViewRouteKilometrage,
            textViewRouteTime,
            textViewKilometrage;

    public ClosedRouteFragment() {
        // Required empty public constructor
    }
    public static ClosedRouteFragment newInstance() {

        return new ClosedRouteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closed_route, container, false);

        textViewStartPointAddress = view.findViewById(R.id.textViewStartPointAddress);


        textViewEndPointAddress = view.findViewById(R.id.textViewEndPointAddress);


        textViewTimeStart = view.findViewById(R.id.textViewTimeStart);


        textViewTimeEnd = view.findViewById(R.id.textViewTimeEnd);


        textViewRouteKilometrage = view.findViewById(R.id.textViewRouteKilometrage);


        textViewRouteTime = view.findViewById(R.id.textViewRouteTime);


        textViewKilometrage = view.findViewById(R.id.textViewKilometrage);


        Button buttonNewRoute = view.findViewById(R.id.buttonOk);
        buttonNewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openRoute();
            }
        });

        Button buttonCloseDay = view.findViewById(R.id.buttonCloseDay);
        buttonCloseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("Закрыть день?");

                if (!AppData.routingDay.getLastRoute().getEndPoint().equals(AppData.DEFAULT_POINT_ADDRESS)) {
                    alert.setMessage("Внимание! Текущий адрес не совпадает с адресом стоянки!");
                    alert.setIcon(R.drawable.ic_action_name);
                }

                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mListener.closeRoutingDay();
                    }
                });

                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
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
        textViewStartPointAddress.setText(AppData.route.getStartPoint());
        textViewEndPointAddress.setText(AppData.route.getEndPoint());
        textViewTimeStart.setText(AppData.route.getStartTime());
        textViewTimeEnd.setText(AppData.route.getEndTime());
        textViewRouteKilometrage.setText(String.valueOf(AppData.route.getLength()) + " км.");
        textViewRouteTime.setText(Helper.getTimeDifference(AppData.route.getStartTime(), AppData.route.getEndTime()) + " мин.");
        textViewKilometrage.setText(String.valueOf(AppData.route.getEndKilometrage()));
    }
}
