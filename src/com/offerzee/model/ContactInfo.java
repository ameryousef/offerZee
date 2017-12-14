package com.offerzee.model;

import com.google.gson.annotations.SerializedName;

public class ContactInfo {
	
	@SerializedName("phone")
	private String phone;
	
	@SerializedName("website")
	private String website;
	
	@SerializedName("fbPage")
	private String fbPage;
	
	@SerializedName("email")
	private String email;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getFbPage() {
		return fbPage;
	}

	public void setFbPage(String fbPage) {
		this.fbPage = fbPage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
