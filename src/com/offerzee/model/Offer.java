package com.offerzee.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;
import com.offerzee.util.Constants;

public class Offer {
	
	private Bitmap imgBitmap;
	
	@SerializedName("id")
	private Long id;
	
	@SerializedName("headline")
	private String headline;
	
	@SerializedName("headlineAr")
	private String headlineAr;
	
	@SerializedName("startDate")
	private String startDate;
	
	@SerializedName("endDate")
	private String endDate;
	
	@SerializedName("activeToDate")
	private String activeToDate;
	
	@SerializedName("company")
	private Company company;
	
	@SerializedName("category")
	private String category;

	public Bitmap getImgBitmap() {
		return imgBitmap;
	}

	public void setImgBitmap(Bitmap imgBitmap) {
		this.imgBitmap = imgBitmap;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImgSrc(String deviceType) {
		return Constants.SERVICE_URL + "/offer/image/" + deviceType + "/" +  id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getHeadlineAr() {
		return headlineAr;
	}

	public void setHeadlineAr(String headlineAr) {
		this.headlineAr = headlineAr;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getActiveToDate() {
		return activeToDate;
	}

	public void setActiveToDate(String activeToDate) {
		this.activeToDate = activeToDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
