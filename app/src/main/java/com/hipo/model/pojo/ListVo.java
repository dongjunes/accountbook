package com.hipo.model.pojo;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListVo {
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


    @Override
    public String toString() {
        return "ListVo [listId=" + listId + ", id=" + id + ", paid=" + paid + ", bank=" + bank + ", operations="
                + operations + ", money=" + money + ", name=" + name + ", category=" + category + ", locationX="
                + locationX + ", locationY=" + locationY + ", day=" + day + "]";
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

    public int convertMoney() {
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

}
