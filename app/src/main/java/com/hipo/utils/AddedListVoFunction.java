package com.hipo.utils;


import android.util.Log;

import com.hipo.model.pojo.AddedListVo;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dongjune on 2017-05-02.
 */

public class AddedListVoFunction {

    public int convertMoney(AddedListVo addedListVo) {
        Log.d("vo의 상세정보", addedListVo.toString());
        StringBuilder sb = new StringBuilder();
        StringTokenizer token = new StringTokenizer(addedListVo.getMoney(), ",");
        while (token.hasMoreTokens()) {
            sb.append(token.nextToken());
        }
        Pattern p = null;
        Matcher m;
        p = Pattern.compile("(.*?)원");
        m = p.matcher(sb);
        String mon = "";
        if (m.find()) {
            mon = m.group(1);
        }
        return Integer.parseInt(mon);
    }

    public int[] hourMin(AddedListVo addedListVo) {
        StringTokenizer tokenizer = new StringTokenizer(addedListVo.getTime(), ":");
        int times[] = new int[2];
        times[0] = Integer.parseInt(tokenizer.nextToken());
        times[1] = Integer.parseInt(tokenizer.nextToken());
        return times;
    }

    public int convertDateNum(AddedListVo addedListVo) {
        String data = addedListVo.getDay();
        String temp[] = new String[2];
        String dateString[] = new String[5];
        int dateInt[] = new int[dateString.length];
        int sum = 0;
        StringTokenizer tokenizer = new StringTokenizer(" ");
        int j = 0;
        while (tokenizer.hasMoreTokens()) {
            temp[j] = tokenizer.nextToken();
            System.out.println(temp[j]);
            j++;
        }
        System.out.println("data : " + data);
        Pattern p = null;
        Matcher m;
        for (int i = 0; i < dateString.length; i++) {
            switch (i) {
                case 0:
                    p = Pattern.compile("[0-9][0-9](.*?)-[0-9][0-9]-");//년
                    break;
                case 1:
                    p = Pattern.compile("-(.*?)-");//일
                    break;
                case 2:
                    p = Pattern.compile("-[0-9][0-9]-(.*?) ");//일
                    break;
                case 3:
                    p = Pattern.compile("-[0-9][0-9] (.*?):");//시간
                    break;
                case 4:
                    p = Pattern.compile("-[0-9][0-9] [0-9][0-9]:(.*?):");//시간
                    break;
            }
            m = p.matcher(data);
            if (m.find()) {
                dateString[i] = m.group(1);
            }
        }
        for (int i = 0; i < dateString.length; i++) {
            dateInt[i] = Integer.parseInt(dateString[i]);
            Log.d("dateInt", dateInt[i] + "");
        }
        dateInt[0] *= 2592000;
        dateInt[1] *= 111601;
        dateInt[2] *= 3600;
        dateInt[3] *= 60;

        for (int i = 0; i < dateInt.length; i++) {
            sum += dateInt[i];
        }

        return sum;
    }
}
