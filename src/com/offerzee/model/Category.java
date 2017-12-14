package com.offerzee.model;

public class Category {
	
	private int drawableResource;
	
	private String code;
	
	private String description;
	
	public Category(int drawableResource, String code, String description) {
		this.drawableResource = drawableResource;
		this.code = code;
		this.description = description;
	}

	public int getDrawableResource() {
		return drawableResource;
	}

	public void setDrawableResource(int drawableResource) {
		this.drawableResource = drawableResource;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
