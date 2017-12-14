package com.offerzee.model;

import com.google.gson.annotations.SerializedName;

public class AddressInfo {
	
	@SerializedName("address")
	private String address;
	
	@SerializedName("addressAr")
	private String addressAr;
	
	@SerializedName("latitude")
	private String latitude;
	
	@SerializedName("longitude")
	private String longitude;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressAr() {
		return addressAr;
	}

	public void setAddressAr(String addressAr) {
		this.addressAr = addressAr;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
