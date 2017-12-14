package com.offerzee;

import com.offerzee.listener.PanAndZoomListener;
import com.offerzee.listener.PanAndZoomListener.Anchor;
import com.offerzee.model.Offer;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OfferDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offer_details_page);
		ImageView offerImg = (ImageView) findViewById(R.id.imageView);
		long selectedOfferId = getIntent().getExtras().getLong(
				Constants.SELECTED_OFFER_ID_EXTRA_MSG);
		Bitmap offerImgBitmap = null;
		String offerHeadline = null;
		for (Offer offer : Util.getCachedOffers()) {
			if (offer.getId() == selectedOfferId) {
				offerImgBitmap = offer.getImgBitmap();
				offerHeadline = Util.determineLocalizedString(
						offer.getHeadline(), offer.getHeadlineAr(),
						getApplicationContext());
				break;
			}
		}
		offerImg.setImageBitmap(offerImgBitmap);
		final LinearLayout offerHeadlineLayout = (LinearLayout) findViewById(R.id.offer_headline);
		offerImg.setOnTouchListener(new PanAndZoomListener(getWindow()
				.getDecorView(), offerImg, Anchor.CENTER) {

			@Override
			public void showExtraWidgets(boolean showExtraWidgets) {
				if (showExtraWidgets) {
					offerHeadlineLayout.setVisibility(View.VISIBLE);
				} else {
					offerHeadlineLayout.setVisibility(View.INVISIBLE);
				}
			}

		});
		Button closeButton = (Button) findViewById(R.id.offer_close_button);
		TextView offerDescTextView = (TextView) findViewById(R.id.offer_description);
		offerDescTextView.setText(offerHeadline);
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
