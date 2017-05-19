package com.hipo.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dongjune on 2017-05-18.
 */

public class GetMyLocationThread extends Thread {
    private LocationManager locationManager = null;
    private Handler handler;
    private Context context;

    public GetMyLocationThread(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void run() {
        super.run();
        // GPS 프로바이더 사용가능여부
        checkingSensor();

        // Register the listener with the Location Manager to receive location updates
        updateLocation();
        // 수동으로 위치 구하기
        //getManualLocation();
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Double latLng[] = new Double[2];
            latLng[0] = location.getLatitude();
            latLng[1] = location.getLongitude();
            sendMessageToFragment(latLng);
            locationManager.removeUpdates(locationListener);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public void checkingSensor() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("getLocation", "isGPSEnabled=" + isGPSEnabled);
        Log.d("getLocation", "isNetworkEnabled=" + isNetworkEnabled);
    }

    public void checkingPermission() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

        {
            //
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "gps권한을 주셔야 실행 가능합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void updateLocation() {
        Handler getPointsHandler = new Handler(Looper.getMainLooper());
        getPointsHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 내용
                checkingPermission();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }, 0);

    }

    public void getManualLocation() {
        String locationProvider = LocationManager.GPS_PROVIDER;
        checkingPermission();
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null)

        {
            Double lng = lastKnownLocation.getLatitude();
            Double lat = lastKnownLocation.getLatitude();
            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
        }
    }

    public void sendMessageToFragment(Double[] latLng) {
        Message msg = new Message();
        msg.obj = latLng;
        handler.sendMessage(msg);
    }

}
