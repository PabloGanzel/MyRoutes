package com.pablo.myroutes;

import android.app.TimePickerDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class OpenedRouteFragment extends Fragment {

    AutoCompleteTextView editTextEndPointAddress;
    EditText editTextKilometrageEnd;
    ProgressBar progressBar;
    TextView textViewProgress, editTextTimeEnd;

    Location location;
    LocationManager locationManager;
    LocationListener locationListener;

    private IFragmentsInteractionListener mListener;

    public OpenedRouteFragment() {
        // Required empty public constructor
    }

    public static OpenedRouteFragment newInstance() {
        return new OpenedRouteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            public void onLocationChanged(Location loc) {
                if (location == null) {
                    location = loc;
                    new HttpAsyncTask().execute(null, null);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(getContext(),provider+" не доступен",Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(getContext(),provider+"доступен",Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_opened_route, container, false);

        TextView textViewStartPointAddress = view.findViewById(R.id.textViewStartPointAddress);
        textViewStartPointAddress.setText(AppData.route.getStartPoint());

        final TextView textViewTimeStart = view.findViewById(R.id.textViewTimeStart);
        textViewTimeStart.setText(AppData.route.getStartTime());

        final TextView textViewKilometrageStartPoint = view.findViewById(R.id.textViewKilometrageStartPoint);
        textViewKilometrageStartPoint.setText(String.valueOf(AppData.route.getStartKilometrage()));

        textViewProgress = view.findViewById(R.id.textViewProgress);
        progressBar = view.findViewById(R.id.progressBar);

        //editTextEndPointAddress = view.findViewById(R.id.editTextEndPointAddress);
        editTextTimeEnd = view.findViewById(R.id.textViewTimeEnd);
        editTextTimeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextKilometrageEnd = view.findViewById(R.id.editTextKilometrageEnd);
        editTextKilometrageEnd.setInputType(InputType.TYPE_CLASS_NUMBER);

        editTextEndPointAddress = view.findViewById(R.id.editTextEndPointAddress);



        editTextTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] timeString = editTextTimeEnd.getText().toString().split(":");
                int hour = Integer.parseInt(timeString[0]);
                int minute = Integer.parseInt(timeString[1]);
                new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String strHour="";
                        String strMinute="";
                        if(hour<10){
                            strHour="0"+String.valueOf(hour);
                        }
                        else strHour = String.valueOf(hour);
                        if(minute<10){
                            strMinute="0"+String.valueOf(minute);
                        }
                        else {
                            strMinute = String.valueOf(minute);
                        }
                        // editTextTimeEnd.setText(hour+":"+minute);}

                        editTextTimeEnd.setText(strHour+":"+strMinute);
                    }
                },hour,minute,true).show();
            }
        });

        final Button buttonClear = view.findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextEndPointAddress.setText("");
            }
        });

        final Button buttonPlus = view.findViewById(R.id.buttonPlus);
        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextKilometrageEnd.setText(String.valueOf(Integer.parseInt(editTextKilometrageEnd.getText().toString()) + 1));
            }
        });

        final Button buttonMinus = view.findViewById(R.id.buttonMinus);
        buttonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextKilometrageEnd.setText(String.valueOf(Integer.parseInt(editTextKilometrageEnd.getText().toString()) - 1));
            }
        });

        final Button buttonPlusTen = view.findViewById(R.id.buttonPlusTen);
        buttonPlusTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextKilometrageEnd.setText(String.valueOf(Integer.parseInt(editTextKilometrageEnd.getText().toString()) + 10));
            }
        });

        final Button buttonMinusTen = view.findViewById(R.id.buttonMinusTen);
        buttonMinusTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextKilometrageEnd.setText(String.valueOf(Integer.parseInt(editTextKilometrageEnd.getText().toString()) - 10));
            }
        });

        //pattern = Pattern.compile("([01]?\\d|2[0-4]):([0-5]\\d)");
        final Button button = view.findViewById(R.id.buttonEndRoute);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equals("OK")) {

                    if(editTextEndPointAddress.getText().toString().equals("")){
                        Toast.makeText(getContext(),"Не указан адрес прибытия", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!AppData.addressList.contains(editTextEndPointAddress.getText().toString())) {
                        AppData.addressList.add(editTextEndPointAddress.getText().toString());

                        // update the autocomplete words

                        editTextEndPointAddress.setAdapter(new ArrayAdapter<>(
                                getContext(),
                                R.layout.my_dropdown_item,
                                //android.R.layout.simple_dropdown_item_1line,
                                AppData.addressList
                                //Arrays.asList(getResources().getStringArray(R.array.addresses))
                        ));
                    }
                    if (!Helper.isKilometragePositive(textViewKilometrageStartPoint.getText().toString(),editTextKilometrageEnd.getText().toString())){
                        Toast.makeText(getContext(),"Пробег не может быть отрицательным",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!Helper.isTimeCorrect(textViewTimeStart.getText().toString(),editTextTimeEnd.getText().toString())){
                        Toast.makeText(getContext(), "Некорректное время прибытия",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mListener.closeRoute(editTextEndPointAddress.getText().toString(), Integer.parseInt(editTextKilometrageEnd.getText().toString()), editTextTimeEnd.getText().toString());

                } else {
                    editTextEndPointAddress.setVisibility(View.VISIBLE);
                    //new HttpAsyncTask().execute(null, null);
                    //editTextEndPointAddress.setText(Helper.getAddress());
                    editTextTimeEnd.setVisibility(View.VISIBLE);
                    editTextTimeEnd.setText(Helper.getTimeNow());
                    editTextKilometrageEnd.setVisibility(View.VISIBLE);
                    editTextKilometrageEnd.setText(String.valueOf(AppData.route.getStartKilometrage() + 2));
                    buttonPlus.setVisibility(View.VISIBLE);
                    buttonMinus.setVisibility(View.VISIBLE);
                    buttonPlusTen.setVisibility(View.VISIBLE);
                    buttonMinusTen.setVisibility(View.VISIBLE);
                    buttonClear.setVisibility(View.VISIBLE);
                    button.setText("OK");

                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    public void onResume() {

        editTextEndPointAddress.setAdapter(new ArrayAdapter<>(
                getContext(),
                R.layout.my_dropdown_item,
                //android.R.layout.simple_dropdown_item_1line,
                AppData.addressList
        ));

        try {
            progressBar.setVisibility(View.VISIBLE);
            textViewProgress.setText("Получаем координаты...");
            textViewProgress.setVisibility(View.VISIBLE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);}
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);}
            //locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000 * 10, 10, locationListener);
        } catch (SecurityException ex) {
        }
        super.onResume();
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

    private class HttpAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            textViewProgress.setVisibility(View.VISIBLE);
            textViewProgress.setText("Получаем адрес...");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return AddressService.getAddressFromCoords(location.getLongitude(), location.getLatitude());
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String param) {
            editTextEndPointAddress.setText(param);
            textViewProgress.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            location = null;
        }
    }
}
