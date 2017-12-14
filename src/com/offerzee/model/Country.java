package com.offerzee.model;

import java.util.List;

import android.graphics.Bitmap;

import com.offerzee.util.Constants;

public class Country {

	public Country(Long id, String name, List<City> cities, boolean hasIcon) {
		this.id = id;
		this.name = name;
		this.cities = cities;
		this.hasIcon = hasIcon;
	}

	public Country() {
	}

	private Bitmap imgBitmap;

	private Long id;

	private String name;

	private List<City> cities;

	private boolean hasIcon;

	public boolean isHasIcon() {
		return hasIcon;
	}

	public void setHasIcon(boolean hasIcon) {
		this.hasIcon = hasIcon;
	}

	public Bitmap getImgBitmap() {
		return imgBitmap;
	}

	public void setImgBitmap(Bitmap imgBitmap) {
		this.imgBitmap = imgBitmap;
	}

	public String getFlagImgSrc() {
		return Constants.SERVICE_URL + "/region/image/" + id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

}
