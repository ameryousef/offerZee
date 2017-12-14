package com.offerzee.adapter;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.offerzee.OfferCompanyContactAndAddressInfoActivity;
import com.offerzee.R;
import com.offerzee.model.Offer;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

public class OffersPagingAdapter extends PagerAdapter {

	private List<Offer> offerList;

	private String deviceType;

	private Context mContext;

	public List<Offer> getOfferList() {
		return offerList;
	}

	public void setOfferList(List<Offer> offerList) {
		this.offerList = offerList;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Offer offer = null;
		if (offerList != null) {
			offer = offerList.get(position);
		}
		deviceType = Util.getDeviceType(container.getContext());
		mContext = container.getContext();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View pagingItemView = inflater.inflate(R.layout.offer_paging_item,
				container, false);
		ImageView offerImg = (ImageView) pagingItemView
				.findViewById(R.id.offer_img);
		TextView companyNameTextView = (TextView) pagingItemView
				.findViewById(R.id.company_name);
		TextView expDate = (TextView) pagingItemView
				.findViewById(R.id.offer_exp_date);
		TextView offerDescriptionTextView = (TextView) pagingItemView
				.findViewById(R.id.offer_description);
		ImageView companyLogoImageView = (ImageView) pagingItemView
				.findViewById(R.id.company_logo);
		if (offer != null && offer.getCompany() != null) {
			Util.downloadOfferImg(offer.getImgSrc(deviceType), offerImg,
					position);
			Util.downloadCompanyImg(offer.getCompany().getImgSrc(),
					companyLogoImageView, position);
			companyNameTextView.setText(Util.determineLocalizedString(offer
					.getCompany().getName(), offer.getCompany().getNameAr(),
					mContext));
			offerDescriptionTextView.setText(Util.determineLocalizedString(
					offer.getHeadline(), offer.getHeadlineAr(), mContext));
			Resources res = container.getResources();
			expDate.setText(res.getString(R.string.offer_exp_date_label) + " "
					+ offer.getEndDate());
			addClickListenerToOfferImage(offerImg, offer, inflater, container);
		}
		container.addView(pagingItemView);
		return pagingItemView;
	}

	private void addClickListenerToOfferImage(final ImageView offerImg,
			final Offer offer, final LayoutInflater inflater,
			final ViewGroup container) {
		offerImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (offerImg != null && offerImg.getDrawable() != null) {
					Intent intent = new Intent(mContext,
							OfferCompanyContactAndAddressInfoActivity.class);
					intent.putExtra(Constants.SELECTED_OFFER_ID_EXTRA_MSG,
							offer.getId());
					mContext.startActivity(intent);
				}
			}
		});
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		if (offerList != null) {
			return offerList.size();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == object);
	}

}