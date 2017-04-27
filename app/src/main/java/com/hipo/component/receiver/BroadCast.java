package com.hipo.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;

import com.hipo.component.service.MyService;
import com.hipo.model.NetworkTask2;
import com.hipo.model.pojo.ListVo;
import com.hipo.model.pojo.UserVo;
import com.hipo.utils.MessageParsingThread;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongjune on 2017-04-25.
 */

public class BroadCast extends BroadcastReceiver {
    private Handler handler;
    private UserVo userVo;
    private ListVo listVo;

    public BroadCast(UserVo vo) {
        this.userVo = vo;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Log.d("BroadCast", "문자가 왔어요 ㅎ");
            String message = messageContent(intent);
            Log.d("BroadCast", "문자 데이터 :" + message);

            handler = new Handler() {
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
            MessageParsingThread dataThread = new MessageParsingThread(handler, message);
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
        NetworkTask2 task2 = new NetworkTask2(userVo.getId(), 3);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", userVo.getId());
        params.put("bank", arr[0]);
        params.put("price", arr[1]);
        params.put("date", arr[2]);
        params.put("place", arr[3]);
        params.put("paid", "카드");
        params.put("category", "생활비");
        params.put("operations", "-");
        task2.execute(params);
    }
}
