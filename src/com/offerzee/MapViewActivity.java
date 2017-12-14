package com.offerzee;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.offerzee.model.Offer;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.Activity;
import android.os.Bundle;

public class MapViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		final long selectedOfferId = getIntent().getExtras().getLong(
				Constants.SELECTED_OFFER_ID_EXTRA_MSG);
		Offer selectedOffer = null;
		for (Offer offer : Util.getCachedOffers()) {
			if (offer.getId() == selectedOfferId) {
				selectedOffer = offer;
				break;
			}
		}
		if (selectedOffer != null
				&& selectedOffer.getCompany() != null
				&& selectedOffer.getCompany().getAddressInfo().getLatitude() != null
				&& !selectedOffer.getCompany().getAddressInfo().getLatitude()
						.isEmpty()
				&& selectedOffer.getCompany().getAddressInfo().getLongitude() != null
				&& !selectedOffer.getCompany().getAddressInfo().getLongitude()
						.isEmpty()) {

			LatLng latLng = new LatLng(Double.valueOf(selectedOffer
					.getCompany().getAddressInfo().getLatitude()),
					Double.valueOf(selectedOffer.getCompany().getAddressInfo()
							.getLongitude()));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
			map.addMarker(new MarkerOptions().position(latLng));
		}
	}

}
