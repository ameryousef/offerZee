package com.offerzee.adapter;

import java.util.List;

import com.offerzee.R;
import com.offerzee.model.Category;
import com.offerzee.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CategoriesAdapter extends BaseAdapter {

	private Context mContext;

	private List<Category> categoryList;

	public CategoriesAdapter(Context c) {
		mContext = c;
		categoryList = Util.getCategoryList(mContext);
	}

	public int getCount() {
		return categoryList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout categoryItemView = null;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		categoryItemView = (LinearLayout) inflater.inflate(
				R.layout.category_item, parent, false);
		TextView categoryItemDesc = (TextView) categoryItemView
				.findViewById(R.id.category_item_desc);
		ImageView categoryItemImage = (ImageView) categoryItemView
				.findViewById(R.id.category_item_img);
		categoryItemDesc.setText(categoryList.get(position).getDescription());
		categoryItemImage.setBackgroundResource(categoryList.get(position)
				.getDrawableResource());
		return categoryItemView;
	}

}