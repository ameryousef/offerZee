package com.offerzee.model;

import com.google.gson.annotations.SerializedName;

public class AvailableUpdates {
	
	@SerializedName("status")
	private String status;
	
	@SerializedName("version")
	private String version;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
