package com.hipo.model;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.Profile;
import com.hipo.model.network.NetworkTask2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by dongjune on 2017-05-25.
 */

public class GetCategoryListThread extends Thread {
    private Handler handler;

    public GetCategoryListThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        String categoryListString = getCategoryListFromServer();
        int length = countCategoryNum(categoryListString);
        ArrayList<String> categoryList = jsonParsing(categoryListString, length);
        sendMessage(categoryList);
    }

    public String getCategoryListFromServer() {
        String categoryListString = null;
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 10);
        Map<String, String> params = new HashMap<>();
        params.put("id", Profile.getCurrentProfile().getId());
        try {
            categoryListString = task2.execute(params).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryListString;
    }

    public int countCategoryNum(String categoryListString) {
        StringTokenizer st = new StringTokenizer(categoryListString, ":");
        int length = st.countTokens();
        return length;
    }

    public ArrayList<String> jsonParsing(String categoryListString, int length) {
        ArrayList<String> categoryList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(categoryListString);
            for (int i = 0; i < length; i++) {
                categoryList.add(i, jsonObject.getString("category" + (i + 1)));
                Log.d("categoryList", categoryList.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    public void sendMessage(ArrayList<String> categoryList) {
        Message msg = new Message();
        msg.obj = categoryList;
        handler.sendMessage(msg);
    }

}