package com.offerzee.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.offerzee.R;
import com.offerzee.model.AvailableUpdates;
import com.offerzee.model.BannerAd;
import com.offerzee.model.Category;
import com.offerzee.model.City;
import com.offerzee.model.Country;
import com.offerzee.model.Offer;
import com.offerzee.model.Region;
import com.offerzee.model.SubscribeRequest;
import com.offerzee.util.Constants.DownloadImageType;

public class Util {

	private static List<Offer> cachedOffers;

	private static List<Category> categoryList;

	private static List<Country> cachedCountries;

	private Util() {
	}

	public static List<Offer> getCachedOffers() {
		return cachedOffers;
	}

	public static List<Country> getCachedCountries() {
		return cachedCountries;
	}

	public static String getApplianceId(Context context) {
		return Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static List<Offer> getOffers(Context context) throws Exception {
		cachedOffers = new ArrayList<Offer>();
		List<Offer> offers = new ArrayList<Offer>();
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/offer/forappliance/" + getApplianceId(context),
				Constants.HTTP_GET, null, context);
		Reader reader = null;
		try {
			if (stream != null) {
				reader = new InputStreamReader(stream);
				Gson gson = new Gson();
				Offer[] offersFromService = gson
						.fromJson(reader, Offer[].class);
				stream.close();
				if (offersFromService != null) {
					offers = new ArrayList<Offer>(
							Arrays.asList(offersFromService));
					cachedOffers = offers;
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new Exception();
				}
			}
		}
		return offers;
	}

	public static AvailableUpdates getAvailableUpdates(Context context)
			throws Exception {
		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setApplianceOS(Constants.APPLIANCE_TYPE_ANDROID);
		subscribeRequest.setAppVersion(Util.getAppVersionName(context));
		subscribeRequest.setApplianceType(Util.getDeviceType(context));
		Gson request = new Gson();
		String json = request.toJson(subscribeRequest);
		AvailableUpdates availableUpdates = null;
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/subscriber/updates", Constants.HTTP_POST, json, context);
		Reader reader = null;
		try {
			if (stream != null) {
				reader = new InputStreamReader(stream);
				Gson gson = new Gson();
				availableUpdates = gson
						.fromJson(reader, AvailableUpdates.class);
				stream.close();
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new Exception();
				}
			}
		}
		return availableUpdates;
	}

	public static List<Country> getCountries(Context context) throws Exception {
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/region", Constants.HTTP_GET, null, context);
		List<Country> countries = null;
		Reader reader = null;
		try {
			if (stream != null) {
				countries = new ArrayList<Country>();
				reader = new InputStreamReader(stream);
				Gson gson = new Gson();
				Region[] regions = gson.fromJson(reader, Region[].class);
				Map<Long, Country> countryMap = new HashMap<Long, Country>();
				stream.close();
				if (regions != null) {
					for (int i = 0; i < regions.length; i++) {
						if (regions[i].getParent() == null) {
							List<City> cities = new ArrayList<City>();
							Country country = new Country(regions[i].getId(),
									determineLocalizedString(
											regions[i].getName(),
											regions[i].getNameAr(), context),
									cities, regions[i].isHasIcon());
							countryMap.put(country.getId(), country);
							countries.add(country);
						} else {
							Country country = countryMap.get(regions[i]
									.getParent());
							if (country != null) {
								City city = new City(
										regions[i].getId(),
										determineLocalizedString(
												regions[i].getName(),
												regions[i].getNameAr(), context));
								country.getCities().add(city);
							}
						}
					}
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new Exception();
				}
			}
		}
		cachedCountries = countries;
		return countries;
	}

	public static String determineLocalizedString(String englishString,
			String arabicString, Context context) {
		return getLocale(context).equalsIgnoreCase(
				Constants.Language.ARABIC.code) ? arabicString : englishString;
	}

	private static void register(String regId, String timezone,
			Long[] regionIds, String gender, String version, String language,
			Context context) throws Exception {
		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setApplianceId(getApplianceId(context));
		subscribeRequest.setApplianceOS(Constants.APPLIANCE_TYPE_ANDROID);
		subscribeRequest.setApplianceType(getDeviceType(context));
		subscribeRequest.setPushId(regId);
		subscribeRequest.setRegionIds(regionIds);
		subscribeRequest.setTimezone(timezone);
		subscribeRequest.setGender(gender);
		subscribeRequest.setLocale(language);
		subscribeRequest.setAppVersion(version);
		Gson request = new Gson();
		String json = request.toJson(subscribeRequest);
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/subscriber", Constants.HTTP_PUT, json, context);
		if (stream != null) {
			stream.close();
		}
	}

	public static boolean isConnectedToHttp(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	public static InputStream getHttpConnection(String urlString,
			String method, String input, Context context) throws Exception {
		if (!isConnectedToHttp(context)) {
			throw new Exception();
		}
		InputStream stream = null;
		URL url = new URL(urlString);
		URLConnection connection = null;
		connection = url.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		httpConnection.setRequestMethod(method);
		httpConnection
				.setConnectTimeout(Constants.HTTP_TIMEOUT_LENGTH_MILL_SECONDS);
		if (method.equals(Constants.HTTP_GET)) {
			httpConnection.connect();
		} else {
			httpConnection.setDoOutput(true);
			httpConnection.setRequestProperty("Content-Type",
					"application/json");
			OutputStream os = null;
			os = httpConnection.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			setRefreshOffersFlagInPreferences(context, true);
		}
		if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			stream = httpConnection.getInputStream();
		} else {
			throw new Exception(
					"Exception in getting response. Response code = "
							+ httpConnection.getResponseCode());
		}
		return stream;
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(Constants.SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
	}

	public static void saveIsDeviceRegisteredInPreferences(
			boolean isRegistered, Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(Constants.PREFS_IS_DEVICE_REGISTERED, isRegistered);
		editor.commit();
	}

	public static boolean getIsDeviceRegisteredInPreferences(Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		boolean showOptionalUpdateDialog = prefs.getBoolean(
				Constants.PREFS_IS_DEVICE_REGISTERED, false);
		return showOptionalUpdateDialog;
	}

	public static void saveShowOptionalUpdateDialogInPreferences(
			boolean isShow, Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(Constants.PREFS_SHOW_OPTIONAL_UPDATE_DIALOG_FLAG,
				isShow);
		editor.commit();
	}

	public static boolean getShowOptionalUpdateDialogInPreferences(
			Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		boolean showOptionalUpdateDialog = prefs.getBoolean(
				Constants.PREFS_SHOW_OPTIONAL_UPDATE_DIALOG_FLAG, true);
		return showOptionalUpdateDialog;
	}

	public static void saveLatestUpdatedVersionInPreferences(String version,
			Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.PREFS_LATEST_UPDATED_VERSION, version);
		editor.commit();
	}

	public static String getLatestUpdatedVersionInPreferences(Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		String showOptionalUpdateDialog = prefs.getString(
				Constants.PREFS_LATEST_UPDATED_VERSION, Constants.EMPTY_STRING);
		return showOptionalUpdateDialog;
	}

	public static Object getRefusedUpdateVersionInPreferences(Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		String refusedOptionalUpdateVersion = prefs.getString(
				Constants.PREFS_REFUSED_OPTIONAL_UPDATE_VERSION,
				Constants.EMPTY_STRING);
		return refusedOptionalUpdateVersion;
	}

	public static void saveRefusedUpdateVersionInPreferences(String version,
			Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.PREFS_REFUSED_OPTIONAL_UPDATE_VERSION,
				version);
		editor.commit();
	}

	public static void saveGenderInPreferences(String gender, Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.PREFS_GENDER, gender);
		editor.commit();
	}

	public static String getSavedGenderInPreferences(Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		String gender = prefs.getString(Constants.PREFS_GENDER,
				Constants.EMPTY_STRING);
		return gender;
	}

	public static void saveTimezoneInPreferences(String timezone,
			Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.PREFS_TIMEZONE, timezone);
		editor.commit();
	}

	public static String getSavedTimezoneInPreferences(Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		String gender = prefs.getString(Constants.PREFS_TIMEZONE,
				Constants.EMPTY_STRING);
		return gender;
	}

	public static void saveLocaleInPreferences(String locale, Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.PREFS_LOCALE, locale);
		editor.commit();
	}

	public static String getSavedLocaleInPreferences(Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		String gender = prefs.getString(Constants.PREFS_LOCALE,
				Constants.EMPTY_STRING);
		return gender;
	}

	public static boolean getRefreshOffersFlagInPreferences(Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		boolean refreshOffersFlag = prefs.getBoolean(
				Constants.PREFS_REFRESH_OFFERS_FLAG, false);
		return refreshOffersFlag;
	}

	public static void setRefreshOffersFlagInPreferences(Context context,
			boolean refreshOffersFlag) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(Constants.PREFS_REFRESH_OFFERS_FLAG,
				refreshOffersFlag);
		editor.commit();
	}

	public static void saveSelectedRegionIdInPreferences(Long regionId,
			boolean isChecked, Context context) {
		SharedPreferences prefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		String registeredRegionIdsString = prefs.getString(
				Constants.PREFS_REGISTERED_REGION_IDS, Constants.EMPTY_STRING);
		if (isChecked) {
			registeredRegionIdsString = registeredRegionIdsString.replace(
					Constants.SPLITTER + regionId, Constants.EMPTY_STRING);
			registeredRegionIdsString = registeredRegionIdsString
					+ Constants.SPLITTER + regionId;
		} else {
			registeredRegionIdsString = registeredRegionIdsString.replace(
					Constants.SPLITTER + regionId, Constants.EMPTY_STRING);
		}
		editor.remove(Constants.PREFS_REGISTERED_REGION_IDS);
		editor.putString(Constants.PREFS_REGISTERED_REGION_IDS,
				registeredRegionIdsString);
		editor.commit();
	}

	public static Long[] getSavedRegionIdInPreferences(Context context) {
		Long[] registeredRegionIds;
		List<Long> registeredRegionIdList = new ArrayList<Long>();
		SharedPreferences prefs = getSharedPreferences(context);
		String registeredRegionIdsString = prefs.getString(
				Constants.PREFS_REGISTERED_REGION_IDS, Constants.EMPTY_STRING);
		if (registeredRegionIdsString != null) {
			StringTokenizer tokenizer = new StringTokenizer(
					registeredRegionIdsString, Constants.SPLITTER);
			while (tokenizer.hasMoreElements()) {
				String regionId = (String) tokenizer.nextElement();
				if (!regionId.equals(Constants.SPLITTER)
						&& !regionId.equals(Constants.EMPTY_STRING)) {
					registeredRegionIdList.add(Long.parseLong(regionId));
				}
			}
		}
		registeredRegionIds = new Long[registeredRegionIdList.size()];
		registeredRegionIds = registeredRegionIdList
				.toArray(registeredRegionIds);
		return registeredRegionIds;
	}

	public static boolean checkPlayServices(final Activity activity) {
		final int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						GooglePlayServicesUtil.getErrorDialog(resultCode,
								activity,
								Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
								.show();
					}
				});
			} else {
				activity.finish();
			}
			return false;
		}
		return true;
	}

	public static String getRegistrationId(Context context) {
		final SharedPreferences prefs = Util.getSharedPreferences(context);
		String registrationId = prefs.getString(Constants.PREFS_REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}

	public static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = Util.getSharedPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.PREFS_REG_ID, regId);
		editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static String getAppVersionName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static void sendRegistrationIdToBackend(Context context,
			String regid, Long[] savedRegionIds, String gender, String version)
			throws Exception {
		TimeZone defaultTimezone = TimeZone.getDefault();
		Util.register(regid, defaultTimezone.getID(), savedRegionIds, gender,
				version, getLocale(context), context);
	}

	public static String getLocale(Context context) {
		String locale = context.getResources().getConfiguration().locale
				.getLanguage();
		if (!locale.equalsIgnoreCase(Constants.Language.ARABIC.code)
				&& !locale.equalsIgnoreCase(Constants.Language.ENGLISH.code)) {
			locale = Constants.Language.ENGLISH.code;
		}
		return locale;
	}

	public static void showAlertDialog(Context context, int title, int message,
			int buttonText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		AlertDialog dialog = builder
				.setMessage(message)
				.setTitle(title)
				.setPositiveButton(buttonText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	public static void downloadOfferImg(String imgSrc, ImageView imageView,
			int offerIndex) {
		downloadImg(imgSrc, imageView, offerIndex, DownloadImageType.OFFER);
	}

	public static void downloadCompanyImg(String imgSrc, ImageView imageView,
			int offerIndex) {
		downloadImg(imgSrc, imageView, offerIndex, DownloadImageType.COMPANY);
	}

	public static void downloadCountryImg(String imgSrc, ImageView imageView,
			int index) {
		downloadImg(imgSrc, imageView, index, DownloadImageType.COUNTRY);
	}

	private static void downloadImg(String imgSrc, ImageView imageView,
			int index, DownloadImageType downloadImageType) {
		if (imageView != null) {
			DownloadImgTask downloadImgTask = new DownloadImgTask();
			downloadImgTask.setContext(imageView.getContext());
			downloadImgTask.setImageView(imageView);
			downloadImgTask.setIndex(index);
			downloadImgTask.setDownloadImageType(downloadImageType);
			downloadImgTask.execute(new String[]{imgSrc});
		}
	}

	public static void downloadImg(String imgSrc, ImageView imageView) {
		if (imageView != null) {
			DownloadImgTask downloadImgTask = new DownloadImgTask();
			downloadImgTask.setContext(imageView.getContext());
			downloadImgTask.setImageView(imageView);
			downloadImgTask.execute(new String[]{imgSrc});
		}
	}

	private static class DownloadImgTask
			extends
				AsyncTask<String, Void, Bitmap> {

		private ImageView img;

		private Context context;

		private int index;

		private DownloadImageType downloadImageType;

		public void setDownloadImageType(DownloadImageType downloadImageType) {
			this.downloadImageType = downloadImageType;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap map = null;
			for (String url : urls) {
				map = downloadImage(url);
			}
			return map;
		}

		public void setImageView(ImageView img) {
			this.img = img;
		}

		// Sets the Bitmap returned by doInBackground
		@Override
		protected void onPostExecute(Bitmap result) {
			img.setImageBitmap(result);
			if (downloadImageType != null) {
				switch (downloadImageType) {
					case OFFER :
						if (cachedOffers != null && cachedOffers.size() > 0
								&& cachedOffers.get(index) != null) {
							cachedOffers.get(index).setImgBitmap(result);
						}
						break;

					case COMPANY :
						if (cachedOffers != null && cachedOffers.size() > 0
								&& cachedOffers.get(index) != null
								&& cachedOffers.get(index).getCompany() != null) {
							cachedOffers.get(index).getCompany()
									.setImgBitmap(result);
						}
						break;

					case COUNTRY :
						if (cachedCountries != null
								&& cachedCountries.get(index) != null) {
							cachedCountries.get(index).setImgBitmap(result);
						}
						break;
				}
			}
		}

		// Creates Bitmap from InputStream and returns it
		private Bitmap downloadImage(String url) {
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;

			try {
				stream = Util.getHttpConnection(url, Constants.HTTP_GET, null,
						context);
				bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
				stream.close();
			} catch (Exception exception) {
			}
			return bitmap;
		}
	}

	public static String getDeviceType(Context context) {
		int screenSize = context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		String deviceType = Constants.DeviceType.MOBILE.code;
		switch (screenSize) {
			case Configuration.SCREENLAYOUT_SIZE_XLARGE :
				deviceType = Constants.DeviceType.TABLET.code;
				break;
			case Configuration.SCREENLAYOUT_SIZE_LARGE :
				deviceType = Constants.DeviceType.TABLET.code;
				break;
			case Configuration.SCREENLAYOUT_SIZE_NORMAL :
				deviceType = Constants.DeviceType.MOBILE.code;
				break;
			case Configuration.SCREENLAYOUT_SIZE_SMALL :
				deviceType = Constants.DeviceType.MOBILE.code;
				break;
			default :
				deviceType = Constants.DeviceType.MOBILE.code;
		}
		return deviceType;
	}

	public static void updateGenderInBackend(Context context, String gender)
			throws Exception {
		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setGender(gender);
		Gson request = new Gson();
		String json = request.toJson(subscribeRequest);
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/subscriber/settings/gender/" + getApplianceId(context),
				Constants.HTTP_POST, json, context);
		if (stream != null) {
			stream.close();
		}
	}

	public static void updateTimeZoneInBackend(String timezone, Context context)
			throws Exception {
		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setTimezone(timezone);
		Gson request = new Gson();
		String json = request.toJson(subscribeRequest);
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/subscriber/settings/timezone/" + getApplianceId(context),
				Constants.HTTP_POST, json, context);
		if (stream != null) {
			stream.close();
		}
	}

	public static void updateLocaleInBackend(String locale, Context context)
			throws Exception {
		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setLocale(locale);
		Gson request = new Gson();
		String json = request.toJson(subscribeRequest);
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/subscriber/settings/locale/" + getApplianceId(context),
				Constants.HTTP_POST, json, context);
		if (stream != null) {
			stream.close();
		}
	}

	public static void updateSubscriberVersionName(Context context,
			String version) throws Exception {
		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setAppVersion(version);
		Gson request = new Gson();
		String json = request.toJson(subscribeRequest);
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/subscriber/appversion/" + getApplianceId(context),
				Constants.HTTP_POST, json, context);
		if (stream != null) {
			stream.close();
		}
	}

	public static BannerAd getBannerAdBySubscriber(Context context)
			throws Exception {
		BannerAd bannerAd = null;
		InputStream stream = getHttpConnection(Constants.SERVICE_URL
				+ "/bannerad/" + getApplianceId(context), Constants.HTTP_GET,
				null, context);
		Reader reader = null;
		try {
			if (stream != null) {
				reader = new InputStreamReader(stream);
				Gson gson = new Gson();
				bannerAd = gson.fromJson(reader, BannerAd.class);
				stream.close();
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new Exception();
				}
			}
		}
		return bannerAd;
	}

	public static List<Category> getCategoryList(Context context) {
		Resources res = context.getResources();
		categoryList = new ArrayList<Category>();

		Category automativeCategory = new Category(
				R.drawable.category_automotive,
				Constants.Category.AUTOMATIVE.code,
				res.getString(R.string.category_automotive));

		Category computerCategory = new Category(R.drawable.category_computers,
				Constants.Category.COMPUTER.code,
				res.getString(R.string.category_computer));

		Category constructionCategory = new Category(
				R.drawable.category_construction,
				Constants.Category.CONSTRUCTION.code,
				res.getString(R.string.category_construction));

		Category educationCategory = new Category(
				R.drawable.category_education,
				Constants.Category.EDUCATION.code,
				res.getString(R.string.category_education));

		Category electronicsCategory = new Category(
				R.drawable.category_electronics,
				Constants.Category.ELCTRONICS.code,
				res.getString(R.string.category_electronics));

		Category entertainmentCategory = new Category(
				R.drawable.category_entertainment,
				Constants.Category.ENTERTAINMENT.code,
				res.getString(R.string.category_entertainment));

		Category fashionCategory = new Category(R.drawable.category_fashion,
				Constants.Category.FASHION.code,
				res.getString(R.string.category_fashion));

		Category financialServicesCategory = new Category(
				R.drawable.category_financial,
				Constants.Category.FINANCIAL_SERVICES.code,
				res.getString(R.string.category_financialservices));

		Category foodCategory = new Category(R.drawable.category_food,
				Constants.Category.FOOD.code,
				res.getString(R.string.category_food));

		Category healthCategory = new Category(R.drawable.category_health,
				Constants.Category.HEALTH.code,
				res.getString(R.string.category_health));

		Category homeCategory = new Category(R.drawable.category_home,
				Constants.Category.HOME.code,
				res.getString(R.string.category_home));

		Category legalServicesCategory = new Category(
				R.drawable.category_legal,
				Constants.Category.LEGAL_SERVICES.code,
				res.getString(R.string.category_legalservies));

		Category realestateCategory = new Category(
				R.drawable.category_realestate,
				Constants.Category.REALESTATE.code,
				res.getString(R.string.category_realestate));

		Category supermarketsCategory = new Category(
				R.drawable.category_supermarkets,
				Constants.Category.SUPERMARKETS.code,
				res.getString(R.string.category_supermarkets));

		Category telecomCategory = new Category(R.drawable.category_telecom,
				Constants.Category.TELECOM.code,
				res.getString(R.string.category_telecom));

		Category travelCategory = new Category(
				R.drawable.category_transportation,
				Constants.Category.TRAVEL.code,
				res.getString(R.string.category_travel));

		Category hotelsCategory = new Category(R.drawable.category_hotels,
				Constants.Category.HOTELS.code,
				res.getString(R.string.category_hotels));

		categoryList.add(foodCategory);
		categoryList.add(supermarketsCategory);
		categoryList.add(entertainmentCategory);
		categoryList.add(fashionCategory);
		categoryList.add(healthCategory);
		categoryList.add(automativeCategory);
		categoryList.add(computerCategory);
		categoryList.add(electronicsCategory);
		categoryList.add(telecomCategory);
		categoryList.add(hotelsCategory);
		categoryList.add(travelCategory);
		categoryList.add(realestateCategory);
		categoryList.add(homeCategory);
		categoryList.add(constructionCategory);
		categoryList.add(financialServicesCategory);
		categoryList.add(educationCategory);
		categoryList.add(legalServicesCategory);
		return categoryList;
	}

	public static void stripUnderlines(TextView textView) {
		if (textView.getText() instanceof Spannable) {
			Spannable s = (Spannable) textView.getText();
			URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
			for (URLSpan span : spans) {
				int start = s.getSpanStart(span);
				int end = s.getSpanEnd(span);
				s.removeSpan(span);
				span = new URLSpanNoUnderline(span.getURL());
				s.setSpan(span, start, end, 0);
			}
		}
	}

	public static void killApplication() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
