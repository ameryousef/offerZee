package com.offerzee.util;

import android.os.Environment;

public class Constants {

	public final static int SPLASH_TIME_OUT = 2000;

	// public final static String SERVICE_HOST_NAME = "dev.offerzee.com";

	public final static String SERVICE_HOST_NAME = "52.25.69.15";

	public final static String SERVICE_PORT = "8080";

	public final static String HTTP_PROTOCOL = "http";

	public final static String SERVICE_URL = HTTP_PROTOCOL + "://"
			+ SERVICE_HOST_NAME + ":" + SERVICE_PORT + "/service";

	public final static String COUNTRIES_FOLDER_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/offerz/countries";

	public final static String COMPANIES_FOLDER_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/offerz/companies";

	public final static String OFFERS_FOLDER_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/offerz/offers";

	public static final String HTTP_GET = "GET";

	public static final String HTTP_POST = "POST";

	public static final String HTTP_PUT = "PUT";

	public static final int HTTP_TIMEOUT_LENGTH_MILL_SECONDS = 5000;

	public static final String REDIRECTED_FROM_GENDER_EXTRA_MSG = "REDIRECTED_FROM_GENDER_EXTRA_MSG";

	public static final String SELECTED_OFFER_ID_EXTRA_MSG = "SELECTED_OFFER_ID_EXTRA_MSG";

	public static final String SELECTED_CATEGORY_INDEX_EXTRA_MSG = "SELECTED_CATEGORY_INDEX_EXTRA_MSG";
	
	public static final String SELECTED_BANNER_COMPANY_INDEX_EXTRA_MSG = "SELECTED_BANNER_COMPANY_INDEX_EXTRA_MSG";

	public static final String NOTIFICATION_OFFER_ID_EXTRA_MSG = "NOTIFICATION_OFFER_ID_EXTRA_MSG";

	public static final String PROPERTY_APP_VERSION = "appVersion";

	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static final String SENDER_ID = "573995210654";

	public static final String SPLITTER = "|";

	public static final String EMPTY_STRING = "";

	public static final String APPLIANCE_TYPE_ANDROID = "ANDROID";

	public static enum Gender {
		MALE("MALE"), FEMALE("FEMALE");
		public String code;
		Gender(String code) {
			this.code = code;
		}
	};

	public static enum DeviceType {
		MOBILE("MOBILE"), TABLET("TABLET");
		public String code;
		DeviceType(String code) {
			this.code = code;
		}
	};

	public static enum Category {
		AUTOMATIVE("AUT"), ENTERTAINMENT("ENT"), FASHION("FSH"), FOOD("FD"), SUPERMARKETS(
				"SUP"), HEALTH("HB"), COMPUTER("IT"), TELECOM("TEL"), ELCTRONICS(
				"ELC"), HOME("HG"), REALESTATE("RE"), TRAVEL("TT"), EDUCATION(
				"EDU"), LEGAL_SERVICES("LEG"), FINANCIAL_SERVICES("FIN"), CONSTRUCTION(
				"CC"), HOTELS("HTL");
		public String code;
		Category(String code) {
			this.code = code;
		}
	};

	public static enum AvailableUpdatesStatus {
		UPDATE_FORCE("UPDATE_FORCE"), UPDATE_OPTIONAL("UPDATE_OPTIONAL"), UPDATE_NONE(
				"UPDATE_NONE");
		public String code;
		AvailableUpdatesStatus(String code) {
			this.code = code;
		}
	};

	public static enum Language {
		ARABIC("AR"), ENGLISH("EN");
		public String code;
		Language(String code) {
			this.code = code;
		}
	};

	public static enum DownloadImageType {
		OFFER, COMPANY, COUNTRY
	};

	// Shared preferences

	public static final String SHARED_PREF_NAME = "OFFERZEE";

	public static final String PREFS_REGISTERED_REGION_IDS = "PREFS_REGISTERED_REGION_IDS";

	public static final String PREFS_REFRESH_OFFERS_FLAG = "PREFS_REFRESH_OFFERS_FLAG";

	public static final String PREFS_COUNTRIES_WITH_FLAGS_IDS = "PREFS_COUNTRIES_WITH_FLAGS_IDS";

	public final static String PREFS_GENDER = "PREFS_GENDER";

	public static final String PREFS_REG_ID = "PREFS_REG_ID";

	public static final String PREFS_SHOW_OPTIONAL_UPDATE_DIALOG_FLAG = "PREFS_SHOW_OPTIONAL_UPDATE_DIALOG_FLAG";

	public static final String PREFS_LATEST_UPDATED_VERSION = "PREFS_LATEST_UPDATED_VERSION";

	public static final String PREFS_IS_DEVICE_REGISTERED = "PREFS_IS_DEVICE_REGISTERED";

	public static final String PREFS_REFUSED_OPTIONAL_UPDATE_VERSION = "PREFS_REFUSED_OPTIONAL_UPDATE_VERSION";

	public static final long DEFAULT_NOTIFICATION_OFFER_ID = -1;

	public static final String PREFS_TIMEZONE = "PREFS_TIMEZONE";

	public static final String PREFS_LOCALE = "PREFS_LOCALE";

}
