package com.hipo.utils;

import com.hipo.model.pojo.AddedListVo;

/**
 * Created by dongjune on 2017-04-27.
 */

public class Heap {
    private int useNum = 0;
    private AddedListVo arr[];
    private int div = 0;
    private AddedListVoFunction function;

    public Heap(int length, int div) {
        arr = new AddedListVo[length + 1];
        this.div = div;
        function = new AddedListVoFunction();
    }

    public void insert(AddedListVo data) {
        int i = ++useNum;
        if (div == 1) {
            while (i != 1 && function.convertMoney(data) > function.convertMoney(arr[i / 2])) {
                arr[i] = arr[i / 2];
                i /= 2;
            }
            arr[i] = data;
        } else if (div == 2) {
            while (i != 1 && function.convertMoney(data) < function.convertMoney(arr[i / 2])) {
                arr[i] = arr[i / 2];
                i /= 2;
            }
            arr[i] = data;
        }

    }

    public AddedListVo delete() {
        int parent, child;
        AddedListVo data, temp;
        data = arr[1];
        temp = arr[useNum--];
        parent = 1;
        child = 2;
        if (div == 1) {
            while (child <= useNum) {
                if (child < useNum && function.convertMoney(arr[child]) < function.convertMoney(arr[child + 1])) {
                    child++;
                }
                if (function.convertMoney(temp) >= function.convertMoney(arr[child])) {
                    break;
                }

                arr[parent] = arr[child];
                parent = child;
                child *= 2;
            }
        } else if (div == 2) {
            while (child <= useNum) {
                if (child < useNum && function.convertMoney(arr[child]) > function.convertMoney(arr[child + 1])) {
                    child++;
                }
                if (function.convertMoney(temp) <= function.convertMoney(arr[child])) {
                    break;
                }

                arr[parent] = arr[child];
                parent = child;
                child *= 2;
            }
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