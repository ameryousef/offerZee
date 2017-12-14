package com.offerzee.fragment;

import java.util.List;

import com.offerzee.BannerCompanyOfferListActivity;
import com.offerzee.PagerContainer;
import com.offerzee.R;
import com.offerzee.adapter.OffersPagingAdapter;
import com.offerzee.model.BannerAd;
import com.offerzee.model.Offer;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class OffersFragment extends Fragment {

	private RefreshOffersTask refreshOffersTask;

	private ViewGroup offersView;

	private View errorPage;

	private PagerContainer pagerContainer;

	private OffersPagingAdapter adapter;

	public OffersFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		offersView = (ViewGroup) inflater.inflate(R.layout.offers_page,
				container, false);
		errorPage = inflater.inflate(R.layout.regions_error_page, container,
				false);
		errorPage.setVisibility(View.INVISIBLE);
		offersView.addView(errorPage);
		refreshOfferList(offersView);
		return offersView;
	}

	public void refreshOfferList(View offersView) {
		refreshBannerAd(offersView.getContext());
		pagerContainer = (PagerContainer) offersView
				.findViewById(R.id.pager_container);
		ViewPager pager = pagerContainer.getViewPager();
		Resources res = getResources();
		ProgressDialog progressDialog = ProgressDialog.show(
				offersView.getContext(),
				res.getString(R.string.refresh_progress_dialog_title),
				res.getString(R.string.refresh_progress_dialog_msg), true);
		refreshOffersTask = new RefreshOffersTask();
		refreshOffersTask.setPager(pager);
		refreshOffersTask.setProgressDialog(progressDialog);
		refreshOffersTask.setOffersView(offersView);
		refreshOffersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Refresh offers list");
		pagerContainer.setVisibility(View.VISIBLE);
		errorPage.setVisibility(View.INVISIBLE);
	}

	private class RefreshOffersTask
			extends
				AsyncTask<String, Void, List<Offer>> {

		private ProgressDialog progressDialog;

		public void setProgressDialog(ProgressDialog progressDialog) {
			this.progressDialog = progressDialog;
		}

		public void setPager(ViewPager pager) {
			this.pager = pager;
		}

		private View offersView;

		private ViewPager pager;

		public void setOffersView(View offersView) {
			this.offersView = offersView;
		}

		@Override
		protected List<Offer> doInBackground(String... params) {
			adapter = new OffersPagingAdapter();
			final List<Offer> offers;
			try {
				offers = Util.getOffers(offersView.getContext());
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.setOfferList(offers);
						pager.setAdapter(adapter);
						pager.setOffscreenPageLimit(adapter.getCount());
						pager.setPageMargin(20);
						pager.setClipChildren(false);
					}
				});
			} catch (Exception e) {
				getActivity().runOnUiThread(errorHandler);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Offer> result) {
			if (offersView != null) {
				Util.setRefreshOffersFlagInPreferences(offersView.getContext(),
						false);
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	private class GetBannerAdBySubscriberTask
			extends
				AsyncTask<String, Void, BannerAd> {

		private Context context;

		@Override
		protected BannerAd doInBackground(String... params) {
			BannerAd bannerAd = null;
			try {
				bannerAd = Util.getBannerAdBySubscriber(context);
			} catch (Exception e) {
			}
			return bannerAd;
		}

		@Override
		protected void onPostExecute(final BannerAd bannerAd) {
			ImageView bannerAdImg = (ImageView) offersView
					.findViewById(R.id.ads_banner);
			if (bannerAd != null) {
				Util.downloadImg(Constants.SERVICE_URL + "/bannerad/image/"
						+ Util.getDeviceType(context) + "/" + bannerAd.getId(),
						bannerAdImg);
				bannerAdImg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (bannerAd != null && bannerAd.getCompany() != null) {
							Intent intent = new Intent(getActivity(),
									BannerCompanyOfferListActivity.class);
							intent.putExtra(
									Constants.SELECTED_BANNER_COMPANY_INDEX_EXTRA_MSG,
									bannerAd.getCompany().getId());
							startActivity(intent);
						}
					}
				});
			}
		}

		public void setContext(Context context) {
			this.context = context;
		}
	}

	private Runnable errorHandler = new Runnable() {

		@Override
		public void run() {
			OffersFragment.this.errorPage = errorPage;
			pagerContainer.setVisibility(View.INVISIBLE);
			errorPage.setVisibility(View.VISIBLE);
			Button tryAgainButton = (Button) errorPage
					.findViewById(R.id.try_again_button);
			tryAgainButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					OffersFragment.this.refreshOfferList(offersView);
				}
			});
		}
	};

	private void refreshBannerAd(Context context) {
		GetBannerAdBySubscriberTask getBannerAdBySubscriberTask = new GetBannerAdBySubscriberTask();
		getBannerAdBySubscriberTask.setContext(context);
		getBannerAdBySubscriberTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Get banner ad info");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
