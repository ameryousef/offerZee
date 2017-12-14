package com.offerzee.adapter;

import java.util.List;
import com.offerzee.R;
import com.offerzee.model.Offer;
import com.offerzee.util.Util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryOfferListAdapter extends ArrayAdapter<Offer> {

	private List<Offer> offers;

	private Handler mHandler = new Handler();
	//
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			notifyDataSetChanged();
			if (!stopListUpdate()) {
				mHandler.postDelayed(mUpdateTimeTask, (1000));
			} else {
				mHandler.removeCallbacks(mUpdateTimeTask);
			}
		}
	};

	public CategoryOfferListAdapter(Context context, List<Offer> offers) {
		super(context, R.layout.category_offer_list_row, offers);
		this.offers = offers;
		// timer
		mHandler.removeCallbacks(mUpdateTimeTask);
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	private boolean stopListUpdate() {
		boolean stopListUpdate = true;
		for (Offer offer : offers) {
			if (offer != null
					&& offer.getCompany() != null
					&& (offer.getImgBitmap() == null || offer.getCompany()
							.getImgBitmap() == null)) {
				stopListUpdate = false;
				break;
			}
		}
		return stopListUpdate;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.category_offer_list_row,
				parent, false);
		TextView companyNameTextView = (TextView) rowView
				.findViewById(R.id.company_name);
		TextView offerDescriptionTextView = (TextView) rowView
				.findViewById(R.id.offer_description);
		ImageView companyLogoImageView = (ImageView) rowView
				.findViewById(R.id.company_logo);
		ImageView offerImageView = (ImageView) rowView
				.findViewById(R.id.offer_img);
		TextView expDate = (TextView) rowView.findViewById(R.id.offer_exp_date);
		Offer offer = offers.get(position);
		if (offer != null && offer.getCompany() != null) {
			companyNameTextView.setText(Util.determineLocalizedString(offer
					.getCompany().getName(), offer.getCompany().getNameAr(),
					getContext()));
			offerDescriptionTextView.setText(Util.determineLocalizedString(
					offer.getHeadline(), offer.getHeadlineAr(), getContext()));
			Resources res = parent.getResources();
			expDate.setText(res.getString(R.string.offer_exp_date_label) + " "
					+ offer.getEndDate());
			offerImageView.setImageBitmap(offer.getImgBitmap());
			companyLogoImageView.setImageBitmap(offer.getCompany()
					.getImgBitmap());
		}
		return rowView;
	}
}