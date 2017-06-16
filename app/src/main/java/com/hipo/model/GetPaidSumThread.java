package com.hipo.model;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.Profile;
import com.hipo.model.network.NetworkTask2;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongjune on 2017-05-26.
 */

public class GetPaidSumThread extends Thread {
    private Handler handler;
    private int[] yearMonth;

    public GetPaidSumThread(Handler handler, int yearMonth[]) {
        this.handler = handler;
        this.yearMonth = yearMonth;
    }

    @Override
    public void run() {
        super.run();
        sharingServer();
    }

    private void sharingServer() {
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 12);
        Map<String, String> params = new HashMap<>();
        params.put("year", yearMonth[0] + "");
        params.put("month", yearMonth[1] + "");
        try {
            String paidSumJson = task2.execute(params).get();
            Log.d("paidSumMoney", paidSumJson);
            String paidSum = jsonParsing(paidSumJson);
            sendHandleMessage(paidSum);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String jsonParsing(String paidSumJson) {
        try {
            JSONObject jsonObject = new JSONObject(paidSumJson);
            String paidSum = jsonObject.getString("sum");
            return paidSum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendHandleMessage(String paidSum) {
        Message msg = new Message();
        msg.obj = paidSum;
        handler.sendMessage(msg);
    }

}
