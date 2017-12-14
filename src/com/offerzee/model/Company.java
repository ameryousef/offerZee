package com.offerzee.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;
import com.offerzee.util.Constants;

public class Company {
	
	private Bitmap imgBitmap;
	
	@SerializedName("id")
	private Long id;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("nameAr")
	private String nameAr;
	
	@SerializedName("contactInfo")
	private ContactInfo contactInfo;
	
	@SerializedName("addressInfo")
	private AddressInfo addressInfo;

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	public AddressInfo getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(AddressInfo addressInfo) {
		this.addressInfo = addressInfo;
	}

	public Bitmap getImgBitmap() {
		return imgBitmap;
	}

	public void setImgBitmap(Bitmap imgBitmap) {
		this.imgBitmap = imgBitmap;
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

	public String getNameAr() {
		return nameAr;
	}

	public void setNameAr(String nameAr) {
		this.nameAr = nameAr;
	}

	public String getImgSrc() {
		return Constants.SERVICE_URL + "/company/image/" + id;
	}

}
