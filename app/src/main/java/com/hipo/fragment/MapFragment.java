package com.hipo.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hipo.lookie.R;
import com.hipo.model.pojo.AddedListVo;
import com.hipo.utils.AddedListVoFunction;
import com.hipo.utils.GetDateLocationThread;
import com.hipo.utils.GetMyLocationThread;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Handler locHandler, getListLocHandler;
    private GetDateLocationThread getDateLocationThread;

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

        int yearMonth[] = settingDefalutYearMonth();

        getListLocHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<AddedListVo> addedVoList = (List<AddedListVo>) msg.obj;
                Log.d("addedVoList was fine?", addedVoList.toString());
                //TODO 가져온 vo들을 좌표에 마커찍고 마커클릭시 vo정보 보여줌.
            }
        };
        getDateLocationThread = new GetDateLocationThread(getListLocHandler, yearMonth);
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
    public void yearSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(position).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        getListLocHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<AddedListVo> addedVoList = (List<AddedListVo>) msg.obj;
                Log.d("addedVoList was fine?", addedVoList.toString());
            }
        };
        getDateLocationThread = new GetDateLocationThread(getListLocHandler, yearMonth);
        getDateLocationThread.run();
    }

    @OnItemSelected(R.id.month_spinner)
    public void monthSpinnerSelected(AdapterView<?> parent, View view, int position, long id) {
        int yearMonth[] = new int[2];
        yearMonth[0] = Integer.parseInt(yearSpinner.getAdapter().getItem(yearSpinner.getSelectedItemPosition()).toString());
        yearMonth[1] = monthSpinner.getSelectedItemPosition() + 1;
        getListLocHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<AddedListVo> addedVoList = (List<AddedListVo>) msg.obj;
                Log.d("addedVoList was fine?", addedVoList.toString());
            }
        };
        getDateLocationThread = new GetDateLocationThread(getListLocHandler, yearMonth);
        getDateLocationThread.run();
    }

    public void settingMap(Double[] latLng) {
        LatLng myLocation = new LatLng(latLng[0], latLng[1]);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("현재 내위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
    }

    public int[] settingDefalutYearMonth() {
        int timeArr[] = AddedListVoFunction.convertDateToInt(AddedListVoFunction.inDate);
        Log.d("timeArr!", timeArr[0] + "," + timeArr[1]);
        int yearMonth[] = new int[2];
        yearMonth[0] = timeArr[5];
        yearMonth[1] = timeArr[1];
        setYearSpinner(yearMonth);
        setMonthSpinner(yearMonth);
        return yearMonth;
    }

    public void setYearSpinner(int yearMonth[]) {
        for (int i = 0; i < yearSpinner.getAdapter().getCount(); i++) {
            if (String.valueOf(yearMonth[0]).equals(yearSpinner.getItemAtPosition(i).toString())) {
                yearSpinner.setSelection(i);
                break;
            }
        }
    }

    public void setMonthSpinner(int yearMonth[]) {
        for (int i = 0; i < monthSpinner.getAdapter().getCount(); i++) {
            if (String.valueOf(yearMonth[1]).equals(monthSpinner.getItemAtPosition(i).toString())) {
                monthSpinner.setSelection(i);
                break;
            }
        }
    }

}
