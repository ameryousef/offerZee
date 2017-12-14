package com.offerzee.model;

import com.google.gson.annotations.SerializedName;

public class SubscribeRequest {
	
	@SerializedName("applianceId")
	private String applianceId;
	
	@SerializedName("pushId")
	private String pushId;
	
	@SerializedName("applianceType")
	private String applianceType;
	
	@SerializedName("timezone")
	private String timezone;
	
	@SerializedName("regions")
	private Long[] regionIds;
	
	@SerializedName("gender")
	private String gender;
	
	@SerializedName("applianceOS")
	private String applianceOS;
	
	@SerializedName("locale")
	private String locale;
	
	@SerializedName("appVersion")
	private String appVersion;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getApplianceOS() {
		return applianceOS;
	}

	public void setApplianceOS(String applianceOS) {
		this.applianceOS = applianceOS;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getApplianceId() {
		return applianceId;
	}

	public void setApplianceId(String applianceId) {
		this.applianceId = applianceId;
	}

	public String getPushId() {
		return pushId;
	}

	public void setPushId(String pushId) {
		this.pushId = pushId;
	}

	public String getApplianceType() {
		return applianceType;
	}

	public void setApplianceType(String applianceType) {
		this.applianceType = applianceType;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Long[] getRegionIds() {
		return regionIds;
	}

	public void setRegionIds(Long[] regionIds) {
		this.regionIds = regionIds;
	}

}
