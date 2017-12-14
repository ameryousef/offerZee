package com.offerzee;

import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class GenderActivity extends Activity {

	private ProgressDialog progressDialog;

	private ImageView maleImageView;

	private ImageView femaleImageView;

	private ImageView maleCheckImageView;

	private ImageView femaleCheckImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gender_page);
		maleImageView = (ImageView) findViewById(R.id.male);
		femaleImageView = (ImageView) findViewById(R.id.female);
		maleCheckImageView = (ImageView) findViewById(R.id.male_check);
		femaleCheckImageView = (ImageView) findViewById(R.id.female_check);
		checkGender(Util.getSavedGenderInPreferences(GenderActivity.this));
		maleImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				maleImageView.setEnabled(false);
				exceuteGenderSelection(Constants.Gender.MALE.code);
			}
		});
		femaleImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				femaleImageView.setEnabled(false);
				exceuteGenderSelection(Constants.Gender.FEMALE.code);
			}
		});
	}

	private void checkGender(String gender) {
		maleCheckImageView.setVisibility(View.INVISIBLE);
		femaleCheckImageView.setVisibility(View.INVISIBLE);
		if (gender.equalsIgnoreCase(Constants.Gender.MALE.code)) {
			maleCheckImageView.setVisibility(View.VISIBLE);
		} else if (gender.equalsIgnoreCase(Constants.Gender.FEMALE.code)) {
			femaleCheckImageView.setVisibility(View.VISIBLE);
		}
	}

	private void exceuteGenderSelection(String selectedGender) {
		final Resources res = getResources();
		GenderActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				progressDialog = ProgressDialog.show(GenderActivity.this,
						res.getString(R.string.registering_progress_title),
						res.getString(R.string.registering_progress_msg), true);
			}
		});
		if (!Util.getIsDeviceRegisteredInPreferences(getApplicationContext())) {
			maleImageView.setEnabled(true);
			femaleImageView.setEnabled(true);
			Intent intent = new Intent(GenderActivity.this,
					RegionsActivity.class);
			intent.putExtra(Constants.REDIRECTED_FROM_GENDER_EXTRA_MSG, true);
			Util.saveGenderInPreferences(selectedGender, GenderActivity.this);
			startActivity(intent);
			checkGender(selectedGender);
			progressDialog.dismiss();
		} else {
			updateGender(selectedGender);
		}
	}

	private void updateGender(final String gender) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				try {
					Util.updateGenderInBackend(getApplicationContext(), gender);
					GenderActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Util.saveGenderInPreferences(gender,
									GenderActivity.this);
							Util.showAlertDialog(GenderActivity.this,
									R.string.register_dialog_success_title,
									R.string.register_dialog_success_msg,
									R.string.alert_dialog_ok_button);
						}
					});
				} catch (Exception e) {
					GenderActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Util.showAlertDialog(GenderActivity.this,
									R.string.register_dialog_error_title,
									R.string.register_dialog_error_msg,
									R.string.alert_dialog_ok_button);
						}
					});
				}
				return null;
			}

			@Override
			protected void onPostExecute(String msg) {
				checkGender(gender);
				maleImageView.setEnabled(true);
				femaleImageView.setEnabled(true);
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
	}

}
