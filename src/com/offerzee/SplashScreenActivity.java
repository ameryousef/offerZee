package com.offerzee;

import java.io.IOException;
import java.util.TimeZone;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.offerzee.model.AvailableUpdates;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

	private String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				CheckAvailableUpdatesTask checkAvailableUpdatesTask = new CheckAvailableUpdatesTask();
				checkAvailableUpdatesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Check available updates");
			}
		}, Constants.SPLASH_TIME_OUT);
	}

	private class CheckAvailableUpdatesTask
			extends
				AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(final String result) {

		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				final AvailableUpdates availableUpdates = Util
						.getAvailableUpdates(getApplicationContext());
				//availableUpdates.setStatus(Constants.AvailableUpdatesStatus.UPDATE_OPTIONAL.code);
				if (availableUpdates != null
						&& availableUpdates.getStatus() != null
						&& availableUpdates.getVersion() != null) {
					if (availableUpdates.getStatus().equalsIgnoreCase(
							Constants.AvailableUpdatesStatus.UPDATE_FORCE.code)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showForceUpdateAlertDialog();
							}
						});
					} else if (availableUpdates
							.getStatus()
							.equalsIgnoreCase(
									Constants.AvailableUpdatesStatus.UPDATE_OPTIONAL.code)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showOptionalUpdateAlertDialog(availableUpdates
										.getVersion());
							}
						});
						// if
						// (Util.getShowOptionalUpdateDialogInPreferences(getApplicationContext())
						// || !Util.getRefusedUpdateVersionInPreferences(
						// getApplicationContext()).equals(
						// availableUpdates.getVersion())) {
						// runOnUiThread(new Runnable() {
						// @Override
						// public void run() {
						// showOptionalUpdateAlertDialog(availableUpdates
						// .getVersion());
						// }
						// });
						// } else {
						// openApplication();
						// }
					} else {
						if (!availableUpdates
								.getVersion()
								.equals(Util
										.getLatestUpdatedVersionInPreferences(getApplicationContext()))
								&& Util.getIsDeviceRegisteredInPreferences(getApplicationContext())) {
							try {
								Util.updateSubscriberVersionName(
										getApplicationContext(),
										availableUpdates.getVersion());
								Util.saveLatestUpdatedVersionInPreferences(
										availableUpdates.getVersion(),
										getApplicationContext());
							} catch (Exception e) {
								openApplication();
							}
						}
						openApplication();
					}
				} else {
					openApplication();
				}
			} catch (Exception e) {
				openApplication();
			}
			return null;
		}
	}

	private class UpdateRegistrationTask
			extends
				AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(final String result) {

		}

		@Override
		protected String doInBackground(String... arg0) {
			Long[] savedRegionIds = Util
					.getSavedRegionIdInPreferences(getApplicationContext());
			String gender = Util
					.getSavedGenderInPreferences(SplashScreenActivity.this);
			try {
				Util.sendRegistrationIdToBackend(SplashScreenActivity.this,
						regid, savedRegionIds, gender,
						Util.getAppVersionName(getApplicationContext()));
				Util.storeRegistrationId(SplashScreenActivity.this, regid);
			} catch (Exception e) {
			}
			return null;
		}
	}

	private class UpdateLocaleTask extends AsyncTask<String, Void, String> {

		private String locale;

		@Override
		protected void onPostExecute(final String result) {

		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				Util.updateLocaleInBackend(locale, getApplicationContext());
				Util.saveLocaleInPreferences(locale, SplashScreenActivity.this);
			} catch (Exception e) {
			}
			return null;
		}

		public void setLocale(String locale) {
			this.locale = locale;
		}
	}

	private class UpdateTimezoneTask extends AsyncTask<String, Void, String> {

		private String timezone;

		@Override
		protected void onPostExecute(final String result) {

		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				Util.updateTimeZoneInBackend(timezone, getApplicationContext());
				Util.saveTimezoneInPreferences(timezone,
						SplashScreenActivity.this);
			} catch (Exception e) {
			}
			return null;
		}

		public void setTimeZone(String timezone) {
			this.timezone = timezone;
		}
	}

	public void openApplication() {
		String timezone = TimeZone.getDefault().getID();
		String locale = Util.getLocale(getApplicationContext());
		if (Util.checkPlayServices(SplashScreenActivity.this)) {
			GoogleCloudMessaging gcm = GoogleCloudMessaging
					.getInstance(getApplicationContext());
			regid = Util.getRegistrationId(getApplicationContext());
			if (gcm == null) {
				gcm = GoogleCloudMessaging
						.getInstance(SplashScreenActivity.this);
			}
			if (regid.isEmpty()) {
				try {
					regid = gcm.register(Constants.SENDER_ID);
					if (Util.getIsDeviceRegisteredInPreferences(getApplicationContext())) {
						updateRegistrationInBackend();
					} else {
						Util.storeRegistrationId(SplashScreenActivity.this,
								regid);
					}
				} catch (IOException e) {
				}
			}
			if (Util.getIsDeviceRegisteredInPreferences(getApplicationContext())
					&& !Util.getSavedLocaleInPreferences(
							getApplicationContext()).equalsIgnoreCase(locale)) {
				updateLocaleInBackend(locale);
			}
			if (Util.getIsDeviceRegisteredInPreferences(getApplicationContext())
					&& !Util.getSavedTimezoneInPreferences(
							getApplicationContext()).equalsIgnoreCase(timezone)) {
				updateTimezoneInBackend(timezone);
			}
		}
		Intent intent;
		if (Util.getIsDeviceRegisteredInPreferences(getApplicationContext())) {
			intent = new Intent(SplashScreenActivity.this, StartActivity.class);
		} else {
			intent = new Intent(SplashScreenActivity.this, GenderActivity.class);
		}
		startActivity(intent);
		finish();
	}

	private void updateRegistrationInBackend() {
		UpdateRegistrationTask updateRegistrationTask = new UpdateRegistrationTask();
		updateRegistrationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Update registration task");
	}

	private void updateLocaleInBackend(String locale) {
		UpdateLocaleTask updateLocaleTask = new UpdateLocaleTask();
		updateLocaleTask.setLocale(locale);
		updateLocaleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Update locale task");
	}

	private void updateTimezoneInBackend(String timezone) {
		UpdateTimezoneTask updateTimezoneTask = new UpdateTimezoneTask();
		updateTimezoneTask.setTimeZone(timezone);
		updateTimezoneTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Update timezone task");
	}

	public void openGooglePlay() {
		final String appPackageName = getPackageName();
		Util.saveShowOptionalUpdateDialogInPreferences(true,
				getApplicationContext());
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appPackageName)));
		}
		finish();
	}

	public void showForceUpdateAlertDialog() {
		Resources res = getResources();
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				SplashScreenActivity.this);
		alertDialogBuilder.setTitle(res
				.getString(R.string.force_update_dialog_title));
		alertDialogBuilder
				.setMessage(res.getString(R.string.force_update_dialog_msg))
				.setCancelable(false)
				.setPositiveButton(
						res.getString(R.string.force_update_dialog_update_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								openGooglePlay();
							}
						})
				.setNegativeButton(
						res.getString(R.string.force_update_dialog_exit_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Util.saveShowOptionalUpdateDialogInPreferences(
										false, getApplicationContext());
								dialog.cancel();
								finish();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void showOptionalUpdateAlertDialog(
			final String availableUpdateVersion) {
		Resources res = getResources();
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				SplashScreenActivity.this);
		alertDialogBuilder.setTitle(res
				.getString(R.string.optional_update_dialog_title));
		alertDialogBuilder
				.setMessage(res.getString(R.string.optional_update_dialog_msg))
				.setCancelable(false)
				.setPositiveButton(
						res.getString(R.string.alert_dialog_yes_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								openGooglePlay();
							}
						})
				.setNegativeButton(
						res.getString(R.string.alert_dialog_no_button),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Util.saveShowOptionalUpdateDialogInPreferences(
										false, getApplicationContext());
								Util.saveRefusedUpdateVersionInPreferences(
										availableUpdateVersion,
										getApplicationContext());
								dialog.cancel();
								openApplication();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
