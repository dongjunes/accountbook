package com.hipo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.ListVo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by dongjune on 2017-04-26.
 */

public class GetListDataThread extends Thread {

    private Handler handler;
    private String id;

    public GetListDataThread(Handler handler, String id) {
        this.handler = handler;
        this.id = id;
    }

    @Override
    public void run() {
        super.run();
        NetworkTask2 task2 = new NetworkTask2(id, 4);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        try {
            String jsonData = task2.execute(params).get();
            Log.d("GetListDataThreadJson ", jsonData);
            List<ListVo> list = pasingToList(jsonData);
            putMessage(list);
        } catch (Exception e) {
            Log.d("server", "서버에 문제가 있습니다.");
        }
    }

    private void putMessage(List<ListVo> list) {
        Message msg = new Message();
        msg.obj = list;
        handler.sendMessage(msg);
    }

    private List<ListVo> pasingToList(String jsonData) {
        String[] st = jsonData.split("\"ListVo[0-9]\":");
        Gson gson = new Gson();
        List<ListVo> list = new ArrayList<ListVo>();
        for (int i = 1; i < st.length; i++) {
            JsonReader reader = new JsonReader(new StringReader(st[i]));
            reader.setLenient(true);
            list.add((ListVo) gson.fromJson(reader, ListVo.class));
        }
        Log.d("제발성공하게해주세용", list.toString());
        return list;
    }

}
