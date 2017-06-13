package com.hipo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.GraphVo;
import com.hipo.model.pojo.ListVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by dongjune on 2017-06-08.
 */

public class GetBarChartThread extends Thread {
    private Handler handler;
    private int[] yearMonth;

    public GetBarChartThread(Handler handler, int[] yearMonth) {
        this.handler = handler;
        this.yearMonth = yearMonth;
    }

    @Override
    public void run() {
        super.run();
        try {
            String jsonData = getListFromServer();
            putMessageHandler(jsonParsing(jsonData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getListFromServer() throws ExecutionException, InterruptedException {
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 14);
        Map<String, String> params = new HashMap<>();
        params.put("id", Profile.getCurrentProfile().getId());
        params.put("year", yearMonth[0] + "");
        params.put("month", yearMonth[1] + "");
        return task2.execute(params).get();
    }

    private List<GraphVo> jsonParsing(String jsonData) throws JSONException {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            List<GraphVo> barList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                barList.add(i, jsonObjectToVo(jsonObject));
            }
            return barList;
        } catch (Exception e) {
            return null;
        }
    }

    private GraphVo jsonObjectToVo(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), GraphVo.class);
    }

    private void putMessageHandler(Object object) {
        Message msg = new Message();
        msg.obj = object;
        handler.sendMessage(msg);
    }
}
