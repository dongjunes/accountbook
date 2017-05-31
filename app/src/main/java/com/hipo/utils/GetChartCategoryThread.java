package com.hipo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.AddedListVo;
import com.hipo.model.pojo.AndroidCategoryChartVo;
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
 * Created by dongjune on 2017-05-31.
 */

public class GetChartCategoryThread extends Thread {
    private Handler handler;

    public GetChartCategoryThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        String parsingData = "";
        try {
            parsingData = getDataFromServer();
            Log.d("getDataFromServer", parsingData);
            settingMessage(jsonParsing(parsingData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDataFromServer() throws ExecutionException, InterruptedException {
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 13);
        return task2.execute(new HashMap<String, String>()).get();
    }

    private List<AndroidCategoryChartVo> jsonParsing(String parsingData) {
        String category;
        Integer sum;
        List<AndroidCategoryChartVo> categoryChartList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(parsingData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                categoryChartList.add(jsonObjectToVo(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categoryChartList;
    }

    private AndroidCategoryChartVo jsonObjectToVo(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), AndroidCategoryChartVo.class);
    }

    private void settingMessage(List<AndroidCategoryChartVo> list) {
        Message msg = new Message();
        msg.obj = list;
        handler.sendMessage(msg);
    }

}
