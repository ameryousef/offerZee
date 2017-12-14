package com.offerzee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.offerzee.model.City;
import com.offerzee.model.Country;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

public class RegionsActivity extends Activity {

	private RefreshRegionsTask refreshRegionsTask;

	private GoogleCloudMessaging gcm;

	private String regid;

	private LinearLayout regionsView;

	private ViewGroup container;

	private LayoutInflater inflater;

	private View errorPage;

	private ProgressDialog registerProgressDialog;

	private Button saveRegionsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.inflater = getLayoutInflater();
		LinearLayout regionsView = (LinearLayout) inflater.inflate(
				R.layout.regions_page, container, false);
		setContentView(regionsView);
		this.container = (ViewGroup) regionsView.getRootView();
		this.regionsView = regionsView;
		this.errorPage = inflater.inflate(R.layout.regions_error_page,
				container, false);
		refreshRegions();
	}

	private class RefreshRegionsTask
			extends
				AsyncTask<String, Void, List<Country>> {

		private ProgressDialog progressDialog;

		private LayoutInflater inflater;

		public void setProgressDialog(ProgressDialog progressDialog) {
			this.progressDialog = progressDialog;
		}

		public void setInflater(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		private Context context;

		private LinearLayout regionsView;

		public void setContext(Context context) {
			this.context = context;
		}

		public void setRegionsView(LinearLayout regionsView) {
			this.regionsView = regionsView;
		}

		@Override
		protected List<Country> doInBackground(String... params) {
			List<Country> countries = null;
			try {
				countries = Util.getCountries(context);
			} catch (Exception e) {
				RegionsActivity.this.runOnUiThread(errorHandler);
			}
			return countries;
		}

		private Runnable errorHandler = new Runnable() {

			@Override
			public void run() {
				regionsView.addView(errorPage);
				Button tryAgainButton = (Button) errorPage
						.findViewById(R.id.try_again_button);
				tryAgainButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						RegionsActivity.this.refreshRegions();
					}
				});
			}
		};

		@Override
		protected void onPostExecute(final List<Country> countries) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			RegionsActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (countries != null) {
						final ExpandableListView expListView = (ExpandableListView) regionsView
								.findViewById(R.id.lvExp);
						List<String> listDataHeader;
						HashMap<String, List<String>> listDataChild;
						listDataHeader = new ArrayList<String>();
						listDataChild = new HashMap<String, List<String>>();
						int i = 0;
						for (Country country : countries) {
							listDataHeader.add(country.getName());
							List<String> cityNames = new ArrayList<String>();
							if(country.getCities() != null && country.getCities().size() > 1) {
								Resources res = context.getResources();
								cityNames.add(res.getString(R.string.select_all));
							}
							for (final City city : country.getCities()) {
								cityNames.add(city.getName());
							}
							listDataChild.put(listDataHeader.get(i), cityNames);
							i++;
						}
						final ExpandableListAdapter listAdapter = new com.offerzee.adapter.RegionsListAdapter(
								context, listDataHeader, listDataChild,
								countries);
						View regionsFooter = inflater.inflate(
								R.layout.regions_explist_footer, expListView,
								false);
						View footerLayout = regionsFooter
								.findViewById(R.id.save_region_layout);
						saveRegionsButton = (Button) regionsFooter
								.findViewById(R.id.save_region);
						saveRegionsButton
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										final Resources res = getResources();
										saveRegionsButton.setEnabled(false);
										RegionsActivity.this
												.runOnUiThread(new Runnable() {

													@Override
													public void run() {
														registerProgressDialog = ProgressDialog
																.show(RegionsActivity.this,
																		res.getString(R.string.registering_progress_title),
																		res.getString(R.string.registering_progress_msg),
																		true);
													}
												});
										Long[] savedRegionIds = Util
												.getSavedRegionIdInPreferences(context);
										if (savedRegionIds.length == 0) {
											saveRegionsButton.setEnabled(true);
											if (registerProgressDialog != null) {
												registerProgressDialog
														.dismiss();
											}
											RegionsActivity.this
													.runOnUiThread(new Runnable() {

														@Override
														public void run() {
															Util.showAlertDialog(
																	RegionsActivity.this,
																	R.string.register_dialog_no_region_selected_title,
																	R.string.register_dialog_no_region_selected_msg,
																	R.string.alert_dialog_ok_button);
														}
													});
										} else if (Util
												.checkPlayServices(RegionsActivity.this)) {
											gcm = GoogleCloudMessaging
													.getInstance(context);
											regid = Util
													.getRegistrationId(context);
											registerInBackground(
													savedRegionIds, context);
										} else {
											saveRegionsButton.setEnabled(true);
											if (registerProgressDialog != null) {
												registerProgressDialog
														.dismiss();
											}
										}
									}
								});

						expListView.addFooterView(footerLayout);
						// setting list adapter
						expListView.setAdapter(listAdapter);
						// To auto expand first group item
						expListView.expandGroup(0);
					}
				}
			});
		}
	}

	private void refreshRegions() {
		if (errorPage != null) {
			regionsView.removeView(errorPage);
		}
		Resources res = getResources();
		ProgressDialog progressDialog = ProgressDialog.show(
				container.getContext(),
				res.getString(R.string.refresh_progress_dialog_title),
				res.getString(R.string.refresh_progress_dialog_msg), true);
		refreshRegionsTask = new RefreshRegionsTask();
		refreshRegionsTask.setContext(container.getContext());
		refreshRegionsTask.setRegionsView(regionsView);
		refreshRegionsTask.setInflater(inflater);
		refreshRegionsTask.setProgressDialog(progressDialog);
		// Execute the task
		refreshRegionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Refresh countries list");
	}

	private void registerInBackground(final Long[] savedRegionIds,
			final Context context) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						System.out.println("1111111111111111111111111111111");
						gcm = GoogleCloudMessaging
								.getInstance(RegionsActivity.this);
						System.out.println("22222222222222222222222222222222");
					} else {
						System.out.println("333333333333333333333333333");
					}
					if (regid.isEmpty()) {
						regid = gcm.register(Constants.SENDER_ID);
						System.out.println("tttttttttttttttttttttttttttttttttttt : " + regid);
					}
					else {
						System.out.println("4444444444444444444444 " + regid);
					}
					String timezone = TimeZone.getDefault().getID();
					String locale = Util.getLocale(context);
					String gender = Util
							.getSavedGenderInPreferences(RegionsActivity.this);
					msg = "Device registered, registration ID=" + regid;
					Util.sendRegistrationIdToBackend(RegionsActivity.this,
							regid, savedRegionIds, gender,
							Util.getAppVersionName(context));
					Util.saveLatestUpdatedVersionInPreferences(
							Util.getAppVersionName(context),
							getApplicationContext());
					Util.storeRegistrationId(RegionsActivity.this, regid);
					Util.saveIsDeviceRegisteredInPreferences(true, context);
					Util.saveTimezoneInPreferences(timezone, context);
					Util.saveLocaleInPreferences(locale, context);
					RegionsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							boolean isRedirectedFromGender = getIntent()
									.getBooleanExtra(
											Constants.REDIRECTED_FROM_GENDER_EXTRA_MSG,
											false);
							if (isRedirectedFromGender) {
								Intent intent = new Intent(
										RegionsActivity.this,
										StartActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							} else {
								Util.showAlertDialog(RegionsActivity.this,
										R.string.register_dialog_success_title,
										R.string.register_dialog_success_msg,
										R.string.alert_dialog_ok_button);
							}
						}
					});
				} catch (Exception e) {
					RegionsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Util.showAlertDialog(RegionsActivity.this,
									R.string.register_dialog_error_title,
									R.string.register_dialog_error_msg,
									R.string.alert_dialog_ok_button);
						}
					});
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				saveRegionsButton.setEnabled(true);
				if (registerProgressDialog != null) {
					registerProgressDialog.dismiss();
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
	}

}
