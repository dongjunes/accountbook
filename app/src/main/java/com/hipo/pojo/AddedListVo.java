package com.hipo.pojo;

import java.io.Serializable;

/**
 * Created by dongjune on 2017-05-02.
 */

public class AddedListVo implements Serializable {
    private String listId;
    private String id;
    private String paid;
    private String bank;
    private String operations;
    private String money;
    private String name;
    private String category;
    private String day;
    private String locationX;
    private String locationY;
    private String date_ym;
    private String date_day;
    private String time;

    @Override
    public String toString() {
        return "ListVo{" +
                "listId='" + listId + '\'' +
                ", id='" + id + '\'' +
                ", paid='" + paid + '\'' +
                ", bank='" + bank + '\'' +
                ", operations='" + operations + '\'' +
                ", money='" + money + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", day='" + day + '\'' +
                ", locationX='" + locationX + '\'' +
                ", locationY='" + locationY + '\'' +
                ", date_ym='" + date_ym + '\'' +
                ", date_day='" + date_day + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getDate_ym() {
        return date_ym;
    }

    public void setDate_ym(String date_ym) {
        this.date_ym = date_ym;
    }


    public String getDate_day() {
        return date_day;
    }

    public void setDate_day(String date_day) {
        this.date_day = date_day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}
