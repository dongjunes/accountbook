package com.hipo.model.pojo;

public class AndroidCategoryChartVo {

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

}
