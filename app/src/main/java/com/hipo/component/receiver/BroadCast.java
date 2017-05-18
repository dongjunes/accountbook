package com.hipo.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;

import com.facebook.Profile;
import com.google.gson.Gson;
import com.hipo.component.service.MyService;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.ListVo;
import com.hipo.model.pojo.UserVo;
import com.hipo.utils.GetMyLocationThread;
import com.hipo.utils.MessageParsingThread;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongjune on 2017-04-25.
 */

public class BroadCast extends BroadcastReceiver {
    private Handler messageHandler, locationHandler;
    private String userId;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Log.d("BroadCast", "문자가 왔어요 ㅎ");
            String message = messageContent(intent);
            Log.d("BroadCast", "문자 데이터 :" + message);

            messageHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    String arr[] = (String[]) msg.obj;
                    for (int i = 0; i < arr.length; i++) {
                        Log.d("BroadCast 확인중" + i, arr[i]);
                    }
                    sharingServer(arr);
                }
            };
            MessageParsingThread dataThread = new MessageParsingThread(messageHandler, message);
            dataThread.start();

        }
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("BroadCast", "전원이 켜졌습니다.");
            context.startService(new Intent(context.getApplicationContext(), MyService.class));
        }
        if (Intent.ACTION_SCREEN_ON == intent.getAction()) {
            Log.d("BroadCast", "screen on.");
        }
        if (Intent.ACTION_SCREEN_OFF == intent.getAction()) {
            Log.d("BroadCast", "screen off");
        }

    }

    private String messageContent(Intent intent) {
        Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int i = 0; i < smsMessage.length; i++) {
            smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
        }
        Date curDate = new Date(smsMessage[0].getTimestampMillis());
        Log.d("now time", curDate.toString());

        String message = smsMessage[0].getMessageBody().toString();
        Log.d("receive meesage", message);

        return message;
    }

    private void sharingServer(String arr[]) {
        NetworkTask2 task2 = new NetworkTask2(userId, 3);
        Map<String, String> params = new HashMap<String, String>();
        String listVoJson = convertJson(arr);
        params.put("ListVo", listVoJson);
        task2.execute(params);
    }

    private ListVo setListVo(String arr[]) {
        final ListVo listVo = new ListVo();
        listVo.setId(userId = Profile.getCurrentProfile().getId());
        listVo.setBank(arr[0]);
        listVo.setMoney(arr[1]);
        listVo.setDay(arr[2]);
        listVo.setName(arr[3]);
        listVo.setPaid("카드");
        listVo.setCategory("생활비");
        listVo.setOperations("-");

        locationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                double[] latLngMsg = (double[]) msg.obj;
                listVo.setLocationX(String.valueOf(latLngMsg[0]));
                listVo.setLocationY(String.valueOf(latLngMsg[1]));
            }
        };
        GetMyLocationThread locationThread = new GetMyLocationThread(context, locationHandler);
        locationThread.run();
        return listVo;
    }

    private String convertJson(String arr[]) {
        Gson gson = new Gson();
        String listVoJson = gson.toJson(setListVo(arr), ListVo.class);
        return listVoJson;
    }
//spring에서 자동저장되는 부분수정하기
}
