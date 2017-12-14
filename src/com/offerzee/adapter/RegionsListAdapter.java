package com.offerzee.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.offerzee.R;
import com.offerzee.model.City;
import com.offerzee.model.Country;
import com.offerzee.util.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class RegionsListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader;
	private HashMap<String, List<String>> _listDataChild;
	private List<Country> countries;
	private Map<Long, CheckBox> checkBoxesMap;

	public RegionsListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData, List<Country> countries) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this.countries = countries;
		checkBoxesMap = new HashMap<Long, CheckBox>();
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("CutPasteId")
	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String childText = (String) getChild(groupPosition, childPosition);
		LayoutInflater infalInflater = (LayoutInflater) this._context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.region_explist_item, null);
		int regionPosition = childPosition;
		if(countries != null && countries.get(groupPosition).getCities().size() > 1) {
			regionPosition = regionPosition - 1;
		}
		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);
		txtListChild.setText(childText);
		if(regionPosition != -1 && countries != null) {
			final Long regionId = countries.get(groupPosition).getCities()
					.get(regionPosition).getId();
			final CheckBox cityCheckBox = (CheckBox) convertView
					.findViewById(R.id.citiesCheck);
			cityCheckBox.setChecked(false);
			checkBoxesMap.put(regionId, cityCheckBox);
			Long[] savedRegionIds = Util.getSavedRegionIdInPreferences(_context);
			for (Long savedRegionId : savedRegionIds) {
				if (regionId == savedRegionId) {
					cityCheckBox.setChecked(true);
					break;
				}
			}
			cityCheckBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					notifyDataSetChanged();
					Util.saveSelectedRegionIdInPreferences(regionId,
							cityCheckBox.isChecked(), _context);
					determineCheckAll(groupPosition);
				}
			});
		}
		else if(countries != null) {
			final CheckBox checkAllBox = (CheckBox) convertView
					.findViewById(R.id.citiesCheck);
			checkBoxesMap.put(countries.get(groupPosition).getId(), checkAllBox);
			checkAllBox.setChecked(false);
			checkAllBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					notifyDataSetChanged();
					for (Long regionId : checkBoxesMap.keySet()) {
						for (City city : countries.get(groupPosition).getCities()) {
							if(city.getId() == regionId) {
								CheckBox cityCheckBox = checkBoxesMap.get(regionId);
								if(cityCheckBox != null) {
									if(checkAllBox.isChecked()) {
										cityCheckBox.setChecked(true);
									}
									else {
										cityCheckBox.setChecked(false);
									}
								}
								Util.saveSelectedRegionIdInPreferences(regionId,
										cityCheckBox.isChecked(), _context);
							}
						}
					}
				}
			});
		}
		determineCheckAll(groupPosition);
		notifyDataSetChanged();
		return convertView;
	}
	
	public void determineCheckAll(int groupPosition) {
		if(countries != null) {
			boolean isCheckAll = false;
			int matchSize = 0;
			Long[] savedRegionIds = Util.getSavedRegionIdInPreferences(_context);
			for (Long savedRegionId : savedRegionIds) {
				for (City city : countries.get(groupPosition).getCities()) {
					if(city.getId() == savedRegionId) {
						matchSize++;
						break;
					}
				}
			}
			if(countries.get(groupPosition).getCities().size() == matchSize) {
				isCheckAll = true;
			}
			if(checkBoxesMap.get(countries.get(groupPosition).getId()) != null) {
				checkBoxesMap.get(countries.get(groupPosition).getId()).setChecked(isCheckAll);
			}
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		LayoutInflater infalInflater = (LayoutInflater) this._context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater
				.inflate(R.layout.region_explist_group, null);
		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);
		ImageView flag = (ImageView) convertView.findViewById(R.id.flag);
		Country country = null;
		if (countries != null) {
			country = countries.get(groupPosition);
		}
		if (country != null && country.getFlagImgSrc() != null) {
			if (Util.getCachedCountries() != null
					&& Util.getCachedCountries().get(groupPosition) != null
					&& Util.getCachedCountries().get(groupPosition)
							.getImgBitmap() == null
					&& Util.getCachedCountries().get(groupPosition).isHasIcon()) {
				Util.downloadCountryImg(country.getFlagImgSrc(), flag,
						groupPosition);
			} else if (Util.getCachedCountries() != null
					&& Util.getCachedCountries().get(groupPosition) != null
					&& Util.getCachedCountries().get(groupPosition)
							.getImgBitmap() != null) {
				flag.setImageBitmap(Util.getCachedCountries()
						.get(groupPosition).getImgBitmap());
			}
		}
		notifyDataSetChanged();
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}