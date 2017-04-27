package com.hipo.utils;

import com.hipo.model.pojo.ListVo;

/**
 * Created by dongjune on 2017-04-27.
 */

public class Heap {
    //
    int useNum = 0;
    ListVo arr[];

    public Heap(int length) {
        arr = new ListVo[length + 1];
    }

    public void insert(ListVo data) {
        int i = ++useNum;

        while (i != 1 && data.convertMoney() < arr[i / 2].convertMoney()) {
            arr[i] = arr[i / 2];
            i /= 2;
        }
        arr[i] = data;
    }

    public ListVo delete() {
        int parent, child;
        ListVo data, temp;
        data = arr[1];
        temp = arr[useNum--];
        parent = 1;
        child = 2;
        while (child <= useNum) {
            if (child < useNum && arr[child].convertMoney() > arr[child + 1].convertMoney()) {
                child++;
            }
            if (temp.convertMoney() <= arr[child].convertMoney()) {
                break;
            }

            arr[parent] = arr[child];
            parent = child;
            child *= 2;
        }
        arr[parent] = temp;
        return data;
    }

    public void printData() {
        for (int i = 1; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }
}