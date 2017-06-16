package com.hipo.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hipo.lookie.R;
import com.hipo.pojo.AddedListVo;
import com.hipo.utils.AddedListVoFunction;
import com.hipo.model.GetListVoByDateThread;
import com.hipo.model.GetMyLocationThread;

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
    private GetListVoByDateThread getListVoByDateThread;
    private Marker purchasingMarker[];
    private boolean modifyDone = true;
    private static View view;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
            // 구글맵 View가 이미 inflate되어 있는 상태이므로, 에러를 무시합니다.
            Log.d("error무시", e.getMessage());
        }

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
                setPurchasingMarker(msg);
            }
        };
        getListVoByDateThread = new GetListVoByDateThread(getListLocHandler, yearMonth);
        getListVoByDateThread.start();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (modifyDone) {
            Toast.makeText(getContext(), "gps를 켜지 않을 시 지도를 이용한 서비스를 사용하실 수 없습니다.", Toast.LENGTH_SHORT).show();
            modifyDone = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }

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
                setPurchasingMarker(msg);
            }
        };
        getListVoByDateThread = new GetListVoByDateThread(getListLocHandler, yearMonth);
        getListVoByDateThread.start();
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
                setPurchasingMarker(msg);
            }
        };
        getListVoByDateThread = new GetListVoByDateThread(getListLocHandler, yearMonth);
        getListVoByDateThread.start();
    }


    public void settingMap(Double[] latLng) {
        LatLng myLocation = new LatLng(latLng[0], latLng[1]);
        //mMap.addMarker(new MarkerOptions().position(myLocation).title("현재 내위치"));
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

    public void setPurchasingMarker(Message msg) {
        mMap.clear();
        final List<AddedListVo> addedVoList = (List<AddedListVo>) msg.obj;
        Log.d("addedVoList was fine?", addedVoList.toString());
        purchasingMarker = new Marker[addedVoList.size()];
        for (int i = 0; i < purchasingMarker.length; i++) {
            Log.d("purchasing success", "Marker" + addedVoList.get(i).getLocationX() + " " + addedVoList.get(i).getLocationY());

            try {
                purchasingMarker[i] = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(addedVoList.get(i).getLocationX()), Double.parseDouble(addedVoList.get(i).getLocationY())))
                        .title(addedVoList.get(i).getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                purchasingMarker[i].setTag(i);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
                        mMap.animateCamera(center);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("상세정보");
                        AddedListVo listVo = addedVoList.get((Integer) marker.getTag());
                        dialog.setMessage(listVo.getName() + " : " + AddedListVoFunction.convertForForm(listVo.getMoney()) + "\n" + listVo.getDate_day());
                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return true;
                    }
                });
            } catch (Exception e) {
                Log.d("위치가 저장되지 않은 정보입니다.", e.getMessage());
            }
        }
    }
}
