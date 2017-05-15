package com.hipo.utils;


import com.hipo.model.pojo.AddedListVo;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dongjune on 2017-05-02.
 */

public class AddedListVoFunction {

    public int convertMoney(AddedListVo addedListVo) {
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
}
