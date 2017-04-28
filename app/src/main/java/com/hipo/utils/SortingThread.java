package com.hipo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hipo.model.pojo.ListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongjune on 2017-04-27.
 */

public class SortingThread extends Thread {
    private int div;
    private List<ListVo> listVo;
    private ListVo arr[];
    private Handler handler;

    public SortingThread(int div, List<ListVo> listVo, Handler handler) {
        this.div = div;
        this.listVo = listVo;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        arr = convertList();
        for (int i = 0; i < arr.length; i++) {
            Log.d("잘변햇니?", arr[i].toString());
        }
        Heap h = new Heap(arr.length, div);
        for (int i = 0; i < arr.length; i++) {
            h.insert(arr[i]);
        }

        List<ListVo> list = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            list.add(h.delete());
            Log.d("list", list.get(i).toString());
        }
        putHandler(list);
    }

    private void putHandler(List<ListVo> vo) {
        Message msg = new Message();
        msg.obj = vo;
        handler.sendMessage(msg);
    }

    private ListVo[] convertList() {
        ListVo[] vo = new ListVo[listVo.size()];
        for (int i = 0; i < vo.length; i++) {
            vo[i] = listVo.get(i);
            Log.d("vo converting", vo[i].toString());
        }
        return vo;
    }


}
