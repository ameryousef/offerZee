package com.offerzee;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.offerzee.model.AddressInfo;
import com.offerzee.model.Company;
import com.offerzee.model.ContactInfo;
import com.offerzee.model.Offer;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class OfferCompanyContactAndAddressInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offer_company_contact_address_info_page);
		Resources res = getResources();
		View addressSection = findViewById(R.id.company_address_section);
		View contactSection = findViewById(R.id.company_contact_section);
		View phoneLayout = findViewById(R.id.phone_layout);
		View websiteLayout = findViewById(R.id.website_layout);
		View fbpageLayout = findViewById(R.id.fbpage_layout);
		View emailLayout = findViewById(R.id.email_layout);
		View mapView = findViewById(R.id.map);
		final ImageView offerImg = (ImageView) findViewById(R.id.imageView);
		ImageView companyLogo = (ImageView) findViewById(R.id.company_logo);
		TextView offerDescTextView = (TextView) findViewById(R.id.offer_desc);
		TextView companyNameTextView = (TextView) findViewById(R.id.company_name);
		TextView expDate = (TextView) findViewById(R.id.offer_exp_date);
		TextView companyAddress = (TextView) findViewById(R.id.company_address);
		TextView phoneTextView = (TextView) findViewById(R.id.phone);
		TextView websiteTextView = (TextView) findViewById(R.id.website);
		TextView fbpageTextView = (TextView) findViewById(R.id.fbpage);
		TextView emailTextView = (TextView) findViewById(R.id.email);
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setAllGesturesEnabled(false);
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.getUiSettings().setCompassEnabled(false);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.getUiSettings().setScrollGesturesEnabled(false);
		map.getUiSettings().setZoomGesturesEnabled(false);
		final long selectedOfferId = getIntent().getExtras().getLong(
				Constants.SELECTED_OFFER_ID_EXTRA_MSG);
		Offer selectedOffer = null;
		for (Offer offer : Util.getCachedOffers()) {
			if (offer.getId() == selectedOfferId) {
				selectedOffer = offer;
				break;
			}
		}
		if (selectedOffer != null) {
			offerImg.setImageBitmap(selectedOffer.getImgBitmap());
			Company selectedOfferCompany = selectedOffer.getCompany();
			AddressInfo addressInfo = selectedOfferCompany.getAddressInfo();
			ContactInfo contactInfo = selectedOfferCompany.getContactInfo();

			// ///////////////////
			// AddressInfo addressInfo = new AddressInfo();
			// addressInfo.setAddress("Ramallah, Palestine");
			// addressInfo.setAddressAr("Ramallah, Palestine");
			// addressInfo.setLatitude("31.9222263");
			// addressInfo.setLongitude("35.2080812");
			// contactInfo = new ContactInfo();
			// ///////////////////

			offerDescTextView.setText(Util.determineLocalizedString(
					selectedOffer.getHeadline(), selectedOffer.getHeadlineAr(),
					this));
			expDate.setText(res.getString(R.string.offer_exp_date_label) + " "
					+ selectedOffer.getEndDate());
			if (selectedOfferCompany != null) {
				companyLogo.setImageBitmap(selectedOfferCompany.getImgBitmap());
				companyNameTextView.setText(Util.determineLocalizedString(
						selectedOfferCompany.getName(),
						selectedOfferCompany.getNameAr(), this));
				if (addressInfo != null
						&& ((addressInfo.getAddress() != null && !addressInfo
								.getAddress().isEmpty()) || (addressInfo
								.getLatitude() != null
								&& !addressInfo.getLatitude().isEmpty()
								&& addressInfo.getLongitude() != null && !addressInfo
								.getLongitude().isEmpty()))) {
					addressSection.setVisibility(View.VISIBLE);
					companyAddress.setText(Util.determineLocalizedString(
							addressInfo.getAddress(),
							addressInfo.getAddressAr(), this));
					if (addressInfo.getLatitude() != null
							&& !addressInfo.getLatitude().isEmpty()
							&& addressInfo.getLongitude() != null
							&& !addressInfo.getLongitude().isEmpty()) {
						LatLng latLng = new LatLng(Double.valueOf(addressInfo
								.getLatitude()), Double.valueOf(addressInfo
								.getLongitude()));
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(
								latLng, 13));
						map.addMarker(new MarkerOptions().position(latLng));
					} else {
						mapView.setVisibility(View.GONE);
					}
				}
				if (contactInfo != null
						&& (contactInfo.getEmail() != null && !contactInfo
								.getEmail().isEmpty())
						|| (contactInfo.getFbPage() != null && !contactInfo
								.getFbPage().isEmpty())
						|| (contactInfo.getPhone() != null && !contactInfo
								.getPhone().isEmpty())
						|| (contactInfo.getWebsite() != null && !contactInfo
								.getWebsite().isEmpty())) {
					contactSection.setVisibility(View.VISIBLE);
					phoneTextView.setText(contactInfo.getPhone());
					websiteTextView.setText(contactInfo.getWebsite());
					fbpageTextView.setText(contactInfo.getFbPage());
					emailTextView.setText(contactInfo.getEmail());
					if (contactInfo.getEmail() == null
							|| contactInfo.getEmail().isEmpty()) {
						emailLayout.setVisibility(View.GONE);
					}
					if (contactInfo.getFbPage() == null
							|| contactInfo.getFbPage().isEmpty()) {
						fbpageLayout.setVisibility(View.GONE);
					}
					if (contactInfo.getPhone() == null
							|| contactInfo.getPhone().isEmpty()) {
						phoneLayout.setVisibility(View.GONE);
					}
					if (contactInfo.getWebsite() == null
							|| contactInfo.getWebsite().isEmpty()) {
						websiteLayout.setVisibility(View.GONE);
					}
				}
				Util.stripUnderlines(phoneTextView);
				Util.stripUnderlines(websiteTextView);
				Util.stripUnderlines(fbpageTextView);
				Util.stripUnderlines(emailTextView);
			}
			offerImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (offerImg != null && offerImg.getDrawable() != null) {
						Intent intent = new Intent(
								OfferCompanyContactAndAddressInfoActivity.this,
								OfferDetailsActivity.class);
						intent.putExtra(Constants.SELECTED_OFFER_ID_EXTRA_MSG,
								selectedOfferId);
						startActivity(intent);
					}
				}
			});
			map.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng arg0) {
					Intent intent = new Intent(
							OfferCompanyContactAndAddressInfoActivity.this,
							MapViewActivity.class);
					intent.putExtra(Constants.SELECTED_OFFER_ID_EXTRA_MSG,
							selectedOfferId);
					startActivity(intent);
				}
			});
		}
	}

}
