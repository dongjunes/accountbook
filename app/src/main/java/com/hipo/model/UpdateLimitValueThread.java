package com.hipo.model;

import android.util.Log;

import com.facebook.Profile;
import com.hipo.model.network.NetworkTask2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongjune on 2017-06-12.
 */

public class UpdateLimitValueThread extends Thread {

    private String category;
    private int limit;

    public UpdateLimitValueThread(String category, int limit) {

        this.category = category;
        this.limit = limit;
    }

    @Override
    public void run() {
        super.run();
        sendDataToServer();
        Log.d("LimitThread", "ok " + category + " " + limit);
    }

    public void sendDataToServer() {
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 15);
        Map<String, String> params = new HashMap<>();
        params.put("category", category);
        params.put("limit", limit + "");
        task2.execute(params);
    }
}
