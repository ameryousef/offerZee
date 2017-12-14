package com.offerzee.fragment;

import com.offerzee.R;
import com.offerzee.RegionsActivity;
import com.offerzee.GenderActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableLayout;

public class SettingsFragment extends Fragment {
	
	public SettingsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {
		TableLayout settingsView = (TableLayout) inflater.inflate(
				R.layout.settings_page, container, false);
		ImageView regionImageView = (ImageView) settingsView.findViewById(R.id.region);
		regionImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), RegionsActivity.class);
				startActivity(intent);
			}
		});
		ImageView genderImageView = (ImageView) settingsView.findViewById(R.id.gender);
		genderImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), GenderActivity.class);
				startActivity(intent);
			}
		});
		return settingsView;
	}
}
