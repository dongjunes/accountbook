package com.hipo.utils;

import android.os.Handler;

import com.facebook.Profile;
import com.hipo.model.NetworkTask2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongjune on 2017-05-19.
 */

public class GetDateLocationThread extends Thread {
    private Handler handler;
    private int[] monthYear;

    public GetDateLocationThread(Handler handler, int[] monthYear) {
        this.handler = handler;
        this.monthYear = monthYear;
    }

    @Override
    public void run() {
        super.run();

        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 8);
        Map<String, String> params = new HashMap<>();
        params.put("year", monthYear[5] + "");
        params.put("month", monthYear[1] + "");
        task2.execute(params);
        //TODO 데이터 받고 핸들메시지에 담아서 MapFragment로 데이터전달

    }
}
