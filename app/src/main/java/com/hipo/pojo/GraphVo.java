package com.hipo.pojo;

import java.sql.Date;

public class GraphVo {
	@Override
	public String toString() {
		return "GraphVo [category=" + category + ", cnt=" + cnt + ", ml=" + ml + ", lsum=" + lsum + ", day1=" + day1
				+ ", day=" + day + ", Jan=" + Jan + ", Feb=" + Feb + ", Mar=" + Mar + ", Apr=" + Apr + ", May=" + May
				+ ", Jun=" + Jun + ", Jul=" + Jul + ", Aug=" + Aug + ", Sep=" + Sep + ", Oct=" + Oct + ", Nov=" + Nov
				+ ", Dec=" + Dec + ", sumresult=" + sumresult + "]";
	}
	private String category;
	private int cnt;
	private int ml;
	private int lsum;
	private Date day1;
	private long day;
	private String Jan;
	private String Feb;
	private String Mar;
	private String Apr;
	private String May;
	private String Jun;
	private String Jul;
	private String Aug;
	private String Sep;
	private String Oct;
	private String Nov;
	private String Dec;
	private String sumresult;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public int getMl() {
		return ml;
	}
	public void setMl(int ml) {
		this.ml = ml;
	}
	public int getLsum() {
		return lsum;
	}
	public void setLsum(int lsum) {
		this.lsum = lsum;
	}
	public Date getDay1() {
		return day1;
	}
	public void setDay1(Date day1) {
		this.day1 = day1;
		this.setDay(day1.getTime());
	}
	public long getDay() {
		return day;
	}
	public void setDay(long day) {
		this.day = day;
	}
	
	public String getJan() {
		return Jan;
	}
	public void setJan(String jan) {
		Jan = jan;
	}
	public String getFeb() {
		return Feb;
	}
	public void setFeb(String feb) {
		Feb = feb;
	}
	public String getMar() {
		return Mar;
	}
	public void setMar(String mar) {
		Mar = mar;
	}
	public String getApr() {
		return Apr;
	}
	public void setApr(String apr) {
		Apr = apr;
	}
	public String getMay() {
		return May;
	}
	public void setMay(String may) {
		May = may;
	}
	public String getJun() {
		return Jun;
	}
	public void setJun(String jun) {
		Jun = jun;
	}
	public String getJul() {
		return Jul;
	}
	public void setJul(String jul) {
		Jul = jul;
	}
	public String getAug() {
		return Aug;
	}
	public void setAug(String aug) {
		Aug = aug;
	}
	public String getSep() {
		return Sep;
	}
	public void setSep(String sep) {
		Sep = sep;
	}
	public String getOct() {
		return Oct;
	}
	public void setOct(String oct) {
		Oct = oct;
	}
	public String getNov() {
		return Nov;
	}
	public void setNov(String nov) {
		Nov = nov;
	}
	public String getDec() {
		return Dec;
	}
	public void setDec(String dec) {
		Dec = dec;
	}
	
	public String getSumresult() {
		return sumresult;
	}
	public void setSumresult(String sumresult) {
		this.sumresult = sumresult;
	}
}
