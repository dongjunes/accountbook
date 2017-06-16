package com.hipo.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.hipo.model.network.NetworkTask2;
import com.hipo.pojo.AndroidCategoryChartVo;

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
    private int yearMonth[];
    private Context context;

    public GetChartCategoryThread(Handler handler, int yearMonth[], Context context) {
        this.yearMonth = yearMonth;
        this.handler = handler;
        this.context = context;
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
            Toast.makeText(context, "가계부내용을 추가하시면 이용가능합니다.", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    private String getDataFromServer() throws ExecutionException, InterruptedException {
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 13);
        Map<String, String> params = new HashMap<>();
        params.put("year", yearMonth[0] + "");
        params.put("month", yearMonth[1] + "");
        return task2.execute(params).get();
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
