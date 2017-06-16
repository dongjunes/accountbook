package com.hipo.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hipo.lookie.R;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dongjune on 2017-04-07.
 */

public class MessageParsingThread extends Thread {

    private Handler handler;
    private String data;
    private Context context;

    public MessageParsingThread(Handler handler, String data, Context context) {
        this.handler = handler;
        this.data = data;
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        String arr[] = new String[4];
        String textData = data;
        Pattern p = null;
        Matcher m = null;
        p = Pattern.compile("\\]\n(.*?)\\ ");//상호명
        m = p.matcher(textData);

        if (m.find()) {
            arr[0] = m.group(1);
        }
        try {
            if (arr[0].equals("우체국")) {
                for (int i = 1; i < arr.length; i++) {
                    switch (i) {
                        case 1:
                            p = Pattern.compile("\\) (.*?)\\.");//금액
                            break;
                        case 2:
                            p = Pattern.compile("\\. (.*?)\\. ");//시간
                            break;
                        case 3:
                            p = Pattern.compile("[0-9]원 (.*?)$");//상호명및위치
                            break;
                    }
                    m = p.matcher(textData);
                    if (m.find()) {
                        arr[i] = m.group(1);
                        //System.out.println(arr[i]);
                    }
                }
                sendNotification(arr);
            } else if (arr[0].equals("신한체크승인")) {
                arr[0] = "신한체크";
                for (int i = 1; i < arr.length; i++) {
                    switch (i) {
                        case 1:
                            p = Pattern.compile("액\\)(.*?) ");
                            break;
                        case 2:
                            p = Pattern.compile("\\*\\) (.*?) \\(");
                            break;
                        case 3:
                            p = Pattern.compile("[0-9]원 (.*?)$");
                            break;
                    }
                    m = p.matcher(textData);
                    if (m.find()) {
                        arr[i] = m.group(1);
                        System.out.println(arr[i]);
                    }
                }
                sendNotification(arr);
            } else {
                arr[0] = whereBank(arr[0]);
                if (arr[0].equals("하나체크")) {
                    for (int i = 1; i < arr.length; i++) {
                        switch (i) {
                            case 1:
                                p = Pattern.compile("체크승인 (.*?)/");
                                break;
                            case 2:
                                p = Pattern.compile(" (.*?)$");
                                break;
                            case 3:
                                p = Pattern.compile("원/(.*?) ");
                                break;
                        }
                        m = p.matcher(textData);
                        if (m.find()) {
                            arr[i] = m.group(1);
                            System.out.println(arr[i]);
                        }
                    }
                    sendNotification(arr);
                } else if (arr[0].equals("하나")) {
                    for (int i = 1; i < arr.length - 1; i++) {
                        switch (i) {
                            case 1:
                                p = Pattern.compile("일시불 (.*?) [0-9][0-9]/");
                                break;
                            case 2:
                                p = Pattern.compile("원 (.*?) 누적");
                                break;

                        }
                        m = p.matcher(textData);
                        if (m.find()) {
                            arr[i] = m.group(1);
                            System.out.println(arr[i]);
                        }
                    }
                    StringTokenizer tokenizer = new StringTokenizer(textData, " ");
                    while (tokenizer.hasMoreTokens()) {
                        Log.d("Token", tokenizer.nextToken() + " " + tokenizer.countTokens());
                        if (tokenizer.countTokens() == 1) {
                            arr[3] = tokenizer.nextToken();
                        }
                    }
                    sendNotification(arr);
                }
            }
            putDataHandler(arr);
        } catch (Exception e) {
            Log.d("parsingMessage", "가계부관련문자가 아닙니다.");
        }
    }

    private String whereBank(String data) {
        if (data.charAt(0) == '하') {
            if (data.charAt(1) == '나') {
                if (data.charAt(2) == '카') {
                    Log.d("whereBank", "Checking 하나체크");
                    return "하나체크";
                } else {
                    Log.d("whereBank", "Checking 하나");
                    return "하나";
                }
            }
        }
        return null;
    }

    private void sendNotification(String arr[]) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.xhipo)
                .setColor(Color.parseColor("#3abdc0"))
                .setContentTitle(arr[3])
                .setContentText(arr[1]);
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(001, builder.build());
    }

    private void putDataHandler(String[] arr) {
        Message msg = new Message();
        msg.obj = arr;
        handler.sendMessage(msg);
    }

}
