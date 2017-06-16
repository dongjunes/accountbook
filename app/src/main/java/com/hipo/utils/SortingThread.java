package com.hipo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.hipo.pojo.AddedListVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongjune on 2017-04-27.
 */

public class SortingThread extends Thread {
    private int div;
    private List<AddedListVo> AddedListVo;
    private AddedListVo arr[];
    private Handler handler;

    public SortingThread(int div, List<AddedListVo> AddedListVo, Handler handler) {
        this.div = div;
        this.AddedListVo = AddedListVo;
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

        List<AddedListVo> list = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            list.add(h.delete());
            Log.d("list", list.get(i).toString());
        }
        putHandler(list);
    }

    private void putHandler(List<AddedListVo> vo) {
        Message msg = new Message();
        msg.obj = vo;
        handler.sendMessage(msg);
    }

    private AddedListVo[] convertList() {
        AddedListVo[] vo = new AddedListVo[AddedListVo.size()];
        for (int i = 0; i < vo.length; i++) {
            vo[i] = AddedListVo.get(i);
            Log.d("vo converting", vo[i].toString());
        }
        return vo;
    }


}
