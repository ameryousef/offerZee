package com.offerzee;

import java.util.ArrayList;

import com.offerzee.adapter.CategoryOfferListAdapter;
import com.offerzee.model.Offer;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BannerCompanyOfferListActivity extends ListActivity {
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		long selectedCompanyIndex = getIntent().getExtras().getLong(
				Constants.SELECTED_BANNER_COMPANY_INDEX_EXTRA_MSG);
		final ArrayList<Offer> offersList = new ArrayList<Offer>();
		if (Util.getCachedOffers() != null) {
			for (Offer offer : Util.getCachedOffers()) {
				if (offer != null && offer.getCompany() != null
						&& offer.getCompany().getId() == selectedCompanyIndex) {
					offersList.add(offer);
				}
			}
		}
		if (offersList.size() == 0) {
			setContentView(R.layout.no_offers_page);
		} else {
			ListView listView = getListView();
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int index, long arg3) {
					if (offersList != null && offersList.get(index) != null) {
						Intent intent = new Intent(getBaseContext(),
								OfferCompanyContactAndAddressInfoActivity.class);
						intent.putExtra(Constants.SELECTED_OFFER_ID_EXTRA_MSG,
								offersList.get(index).getId());
						startActivity(intent);
					}
				}
			});
			CategoryOfferListAdapter adapter = new CategoryOfferListAdapter(
					this, offersList);
			setListAdapter(adapter);
		}
	}
}