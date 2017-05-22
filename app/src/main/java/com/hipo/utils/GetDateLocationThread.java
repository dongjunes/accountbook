package com.hipo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.Profile;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.AddedListVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


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
        String listJsonData;
        try {
            Log.d("year", monthYear[0] + "," + monthYear[1]);
            listJsonData = sharingServer();
            List<AddedListVo> addedVoList = AddedListVoFunction.pasingToList(listJsonData);
            putListInMessage(addedVoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sharingServer() throws ExecutionException, InterruptedException {
        NetworkTask2 task2 = new NetworkTask2(Profile.getCurrentProfile().getId(), 8);
        Map<String, String> params = new HashMap<>();
        params.put("year", monthYear[0] + "");
        params.put("month", monthYear[1] + "");
        return task2.execute(params).get();
    }

    public void putListInMessage(List<AddedListVo> addedVoList) {
        Message msg = new Message();
        msg.obj = addedVoList;
        handler.sendMessage(msg);
    }

}
