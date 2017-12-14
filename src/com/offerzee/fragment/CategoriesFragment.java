package com.offerzee.fragment;

import com.offerzee.CategoryOfferListActivity;
import com.offerzee.R;
import com.offerzee.adapter.CategoriesAdapter;
import com.offerzee.util.Constants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class CategoriesFragment extends Fragment {
	
	public CategoriesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GridView categorisView = (GridView) inflater.inflate(
				R.layout.categories_page, container, false);
		categorisView.setAdapter(new CategoriesAdapter(getActivity()));
		categorisView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						CategoryOfferListActivity.class);
				intent.putExtra(Constants.SELECTED_CATEGORY_INDEX_EXTRA_MSG,
						position);
				startActivity(intent);
			}
		});
		return categorisView;
	}

}
