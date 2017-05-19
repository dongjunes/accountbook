package com.hipo.fragment;


import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.facebook.Profile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hipo.lookie.R;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.AddedListVo;
import com.hipo.utils.AddedListVoFunction;
import com.hipo.utils.GetDateLocationThread;
import com.hipo.utils.GetMyLocationThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FragmentActivity mContext;
    private Handler locHandler, getListLocHandler;
    private GetDateLocationThread getDateLocationThread;
    private int timeArr[];

    @BindView(R.id.myLocBtn)
    Button myLocBtn;
    @BindView(R.id.year_spinner)
    Spinner yearSpinner;
    @BindView(R.id.month_spinner)
    Spinner monthSpinner;

    public MapFragment() {
        // Required empty public constructor
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static MapFragment newInstance(int sectionNumber) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_map, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this, view);

        setYearSpinner();
        setMonthSpinner();
        getListLocHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //TODO 서버에서 가저온 좌표로 마크표시하기
            }
        };
        getDateLocationThread = new GetDateLocationThread(getListLocHandler, timeArr);
        getDateLocationThread.run();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        locHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Double[] latLngMsg = (Double[]) msg.obj;
                settingMap(latLngMsg);
            }
        };
        GetMyLocationThread myLocationThread = new GetMyLocationThread(getContext(), locHandler);
        myLocationThread.start();
    }

    @OnClick(R.id.myLocBtn)
    public void moveMyLocation(View v) {
        locHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Double[] latLngMsg = (Double[]) msg.obj;
                settingMap(latLngMsg);
            }
        };
        GetMyLocationThread myLocationThread = new GetMyLocationThread(getContext(), locHandler);
        myLocationThread.start();
    }

    @OnItemSelected(R.id.year_spinner)
    public void yearSpinnerSelected(Spinner spinner, int position) {
        switch (position) {
            case 0:

                break;
            case 1:

                break;
        }
    }

    @OnItemSelected(R.id.month_spinner)
    public void monthSpinnerSelected(Spinner spinner, int position) {

    }

    public void settingMap(Double[] latLng) {
        LatLng myLocation = new LatLng(latLng[0], latLng[1]);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("현재 내위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
    }

    public void setYearSpinner() {
        timeArr = AddedListVoFunction.convertDateToInt(AddedListVoFunction.inDate);
        for (int i = 0; i < timeArr.length; i++) {
            if (String.valueOf(timeArr[5]).equals(yearSpinner.getItemAtPosition(i).toString())) {
                yearSpinner.setSelection(i);
                break;
            }
        }
    }

    public void setMonthSpinner() {
        timeArr = AddedListVoFunction.convertDateToInt(AddedListVoFunction.inDate);
        for (int i = 0; i < timeArr.length; i++) {
            if (String.valueOf(timeArr[1]).equals(monthSpinner.getItemAtPosition(i).toString())) {
                monthSpinner.setSelection(i);
                break;
            }
        }
    }

}
