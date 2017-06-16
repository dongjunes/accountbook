package com.hipo.pojo;

import android.support.annotation.NonNull;

public class AndroidCategoryChartVo implements Comparable<AndroidCategoryChartVo> {

    private String category;
    private Integer sum;
    private String id;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AndroidCategoryChartVo [category=" + category + ", sum=" + sum + ", id=" + id + "]";
    }

    @Override
    public int compareTo(@NonNull AndroidCategoryChartVo target) {
        if (this.sum > target.sum) {
            return -1;
        } else if (this.sum < target.sum) {
            return 1;
        }
        return 0;
    }
}
