package com.offerzee.model;

import com.google.gson.annotations.SerializedName;

public class Region {
	
	@SerializedName("id")
	private Long id;
	
	@SerializedName("parent")
	private Long parent;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("nameAr")
	private String nameAr;
	
	@SerializedName("hasIcon")
	private boolean hasIcon;

	public boolean isHasIcon() {
		return hasIcon;
	}

	public void setHasIcon(boolean hasIcon) {
		this.hasIcon = hasIcon;
	}

	public String getNameAr() {
		return nameAr;
	}

	public void setNameAr(String nameAr) {
		this.nameAr = nameAr;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
