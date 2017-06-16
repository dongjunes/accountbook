package com.hipo.model;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hipo.model.network.NetworkTask2;
import com.hipo.pojo.AddedListVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hipo.utils.AddedListVoFunction.pasingToList;

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
        Log.d("IDNullCheck", id);
        NetworkTask2 task2 = new NetworkTask2(id, 4);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        try {
            String jsonData = task2.execute(params).get();
            Log.d("GetListDataThreadJson ", jsonData);
            List<AddedListVo> list = pasingToList(jsonData);
            putMessage(list);
        } catch (Exception e) {
            Log.d("server", "서버에 문제가 있습니다.");
        }
    }

    private void putMessage(List<AddedListVo> list) {
        Message msg = new Message();
        msg.obj = list;
        handler.sendMessage(msg);
    }

}
