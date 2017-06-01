package com.hipo.utils;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.hipo.model.pojo.AddedListVo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dongjune on 2017-05-02.
 */

public class AddedListVoFunction {
    final public static String inDate = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date());

    final public static String convertForForm(String money) { // 2000 -> 2,000원
        boolean convertingDone = false;// String이 int로 변환여부 체크(전역으로 사용하기
        // this.converingDone=true;)
        try {
            Integer.parseInt(money);
            convertingDone = true;
        } catch (Exception e) {
            convertingDone = false;
            System.out.println(e.getMessage() + " 금액입력이 아닙니다.");
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < money.length(); i++) {
            sb.append(money.charAt(i));
            if ((money.length() - (i + 1)) % 3 == 0) {
                if (money.length() - 1 == i) {

                } else {
                    sb.append(",");
                }
            }
        }
        sb.append("원");
        return sb.toString();
    }

    public static String[] dayTimeConvert(String data) {
        StringTokenizer tokenizer = new StringTokenizer(data, " ");
        String times[] = new String[2];
        times[0] = tokenizer.nextToken();
        times[1] = tokenizer.nextToken();
        return times;
    }

    public static int convertMoney(String money) {
        Log.d("vo의 상세정보", money);
        StringBuilder sb = new StringBuilder();
        StringTokenizer token = new StringTokenizer(money, ",");
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

    public static int[] hourMin(String time) {
        StringTokenizer tokenizer = new StringTokenizer(time, ":");
        int times[] = new int[2];
        times[0] = Integer.parseInt(tokenizer.nextToken());
        times[1] = Integer.parseInt(tokenizer.nextToken());
        return times;
    }

    public static int[] convertDateToInt(String data) {
        String temp[] = new String[2];
        String dateString[] = new String[6];
        int dateInt[] = new int[dateString.length];
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
                    p = Pattern.compile("-(.*?)-");//월
                    break;
                case 2:
                    p = Pattern.compile("-[0-9][0-9]-(.*?) ");//일
                    break;
                case 3:
                    p = Pattern.compile("-[0-9][0-9] (.*?):");//시간
                    break;
                case 4:
                    p = Pattern.compile("-[0-9][0-9] [0-9][0-9]:(.*?):");//분
                    break;
                case 5:
                    p = Pattern.compile("(.*?)-[0-9][0-9]-");//년 20붙음
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
        return dateInt;
    }

    public static int convertDateNum(String time) {
        int sum = 0;
        int dateInt[] = convertDateToInt(time);
        dateInt[0] *= 2592000;
        dateInt[1] *= 111601;
        dateInt[2] *= 3600;
        dateInt[3] *= 60;

        for (int i = 0; i < dateInt.length; i++) {
            sum += dateInt[i];
        }
        return sum;
    }

    public static List<AddedListVo> pasingToList(String jsonData) {
        String[] st = jsonData.split("\"ListVo[0-9]{1,3}\":");
        Gson gson = new Gson();
        List<AddedListVo> list = new ArrayList<AddedListVo>();
        for (int i = 1; i < st.length; i++) {
            JsonReader reader = new JsonReader(new StringReader(st[i]));
            reader.setLenient(true);
            list.add((AddedListVo) gson.fromJson(reader, AddedListVo.class));
        }
        Log.d("제발성공하게해주세용", list.toString());
        return list;
    }

    final public static boolean checkInt(String searching) {
        try {
            Integer.parseInt(searching);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
