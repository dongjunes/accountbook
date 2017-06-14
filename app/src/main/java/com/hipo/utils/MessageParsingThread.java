package com.hipo.utils;

import android.os.Handler;
import android.os.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dongjune on 2017-04-07.
 */

public class MessageParsingThread extends Thread {

    private Handler handler;
    private String data;

    public MessageParsingThread(Handler handler, String data) {
        this.handler = handler;
        this.data = data;
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
                        p = Pattern.compile("원 (.*?)$");//상호명및위치
                        break;
                }
                m = p.matcher(textData);
                if (m.find()) {
                    arr[i] = m.group(1);
                    //System.out.println(arr[i]);
                }
            }
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

        }
        putDataHandler(arr);
    }


    private void putDataHandler(String[] arr) {
        Message msg = new Message();
        msg.obj = arr;
        handler.sendMessage(msg);
    }

}
