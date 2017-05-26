package com.hipo.utils;

import android.util.Log;

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
            Log.d("dataCheckMoney",data.getMoney());
            while (i != 1 && Integer.parseInt(data.getMoney())> Integer.parseInt(arr[i / 2].getMoney())) {
                arr[i] = arr[i / 2];
                i /= 2;
            }
            arr[i] = data;
        } else if (div == 2) {
            while (i != 1 && Integer.parseInt(data.getMoney()) < Integer.parseInt(arr[i / 2].getMoney())) {
                arr[i] = arr[i / 2];
                i /= 2;
            }
            arr[i] = data;
        } else if (div == 3) {
            while (i != 1 && function.convertDateNum(data.getDay()) > function.convertDateNum(arr[i / 2].getDay())) {
                arr[i] = arr[i / 2];
                i /= 2;
            }
            arr[i] = data;
        } else if (div == 4) {
            while (i != 1 && function.convertDateNum(data.getDay()) < function.convertDateNum(arr[i / 2].getDay())) {
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
                if (child < useNum && Integer.parseInt(arr[child].getMoney()) < Integer.parseInt(arr[child + 1].getMoney())) {
                    child++;
                }
                if (Integer.parseInt(temp.getMoney()) >= Integer.parseInt(arr[child].getMoney())) {
                    break;
                }

                arr[parent] = arr[child];
                parent = child;
                child *= 2;
            }
        } else if (div == 2) {
            while (child <= useNum) {
                if (child < useNum && Integer.parseInt(arr[child].getMoney()) > Integer.parseInt(arr[child + 1].getMoney())) {
                    child++;
                }
                if (Integer.parseInt(temp.getMoney()) <= Integer.parseInt(arr[child].getMoney())) {
                    break;
                }

                arr[parent] = arr[child];
                parent = child;
                child *= 2;
            }
        } else if (div == 3) {
            while (child <= useNum) {
                if (child < useNum && function.convertDateNum(arr[child].getDay()) < function.convertDateNum(arr[child + 1].getDay())) {
                    child++;
                }
                if (function.convertDateNum(temp.getDay()) >= function.convertDateNum(arr[child].getDay())) {
                    break;
                }

                arr[parent] = arr[child];
                parent = child;
                child *= 2;
            }
        } else if (div == 4) {
            while (child <= useNum) {
                if (child < useNum && function.convertDateNum(arr[child].getDay()) > function.convertDateNum(arr[child + 1].getDay())) {
                    child++;
                }
                if (function.convertDateNum(temp.getDay()) <= function.convertDateNum(arr[child].getDay())) {
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
}