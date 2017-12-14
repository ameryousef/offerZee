package com.offerzee;

import java.util.List;
import java.util.Locale;

import com.offerzee.fragment.CategoriesFragment;
import com.offerzee.fragment.OffersFragment;
import com.offerzee.fragment.SettingsFragment;
import com.offerzee.model.Offer;
import com.offerzee.util.Constants;
import com.offerzee.util.Util;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StartActivity extends FragmentActivity
		implements
			ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	Context context;

	private MenuItem menuItem;

	private Menu menu;

	private ActionBar actionBar;

	private RefreshOffersTask task;

	private OffersFragment offersFragment;

	@Override
	public void onBackPressed() {
		// To kill application and download process in order to not wait for all
		// offers and images to download in cases entered the application after
		// the back button was pressed and download was not finished.
		Util.killApplication();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.context = getBaseContext();
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			View customActionBar = getLayoutInflater().inflate(
					R.layout.custom_action_bar_tab, new LinearLayout(this),
					false);
			TextView tabTitle = (TextView) customActionBar
					.findViewById(R.id.tab_title);
			ImageView tabIcon = (ImageView) customActionBar
					.findViewById(R.id.tab_icon);
			if (i == 0) {
				tabIcon.setBackgroundResource(R.drawable.offers);
			} else if (i == 1) {
				tabIcon.setBackgroundResource(R.drawable.categories);
			} else if (i == 2) {
				tabIcon.setBackgroundResource(R.drawable.settings);
			}
			tabTitle.setText(mSectionsPagerAdapter.getPageTitle(i));
			Tab homeTab = actionBar.newTab();
			homeTab.setTabListener(this);
			actionBar.addTab(homeTab);
			homeTab.setCustomView(customActionBar);
			// actionBar.addTab(actionBar.newTab()
			// .setText(mSectionsPagerAdapter.getPageTitle(i)).setIcon(R.drawable.categories)
			// .setTabListener(this));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		long notificationOfferId = intent.getLongExtra(
				Constants.NOTIFICATION_OFFER_ID_EXTRA_MSG,
				Constants.DEFAULT_NOTIFICATION_OFFER_ID);
		actionBar.setSelectedNavigationItem(0);
		task = new RefreshOffersTask();
		task.setNotificationOfferId(notificationOfferId);
		task.execute("Refresh offers");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Util.checkPlayServices(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		menuItem = item;
		switch (item.getItemId()) {
			case R.id.action_refresh :
				item.setActionView(R.layout.progressbar);
				item.expandActionView();
				task = new RefreshOffersTask();
				task.execute("Refresh offers");
				break;
			default :
				break;
		}
		return true;
	}

	private class RefreshOffersTask extends AsyncTask<String, Void, String> {

		long notificationOfferId = Constants.DEFAULT_NOTIFICATION_OFFER_ID;

		@Override
		protected String doInBackground(String... params) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Object currentView = mSectionsPagerAdapter.instantiateItem(
							mViewPager, mViewPager.getCurrentItem());
					if (currentView instanceof OffersFragment) {
						offersFragment = (OffersFragment) currentView;
						offersFragment.refreshOfferList(offersFragment
								.getView());
					}
				}
			});
			return null;
		}

		public void setNotificationOfferId(long notificationOfferId) {
			this.notificationOfferId = notificationOfferId;
		}

		@Override
		protected void onPostExecute(String result) {
			Util.setRefreshOffersFlagInPreferences(context, false);
			if (menuItem != null) {
				menuItem.collapseActionView();
				menuItem.setActionView(null);
			}
			prepareNotificationOffer(notificationOfferId);
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.main, menu);
		// Set to true only when the user already selected a region
		menu.getItem(0).setVisible(true);
		return true;
	}

	public void prepareNotificationOffer(long notificationOfferId) {
		List<Offer> cachedOffers = Util.getCachedOffers();
		Offer notificationOffer = null;
		if (cachedOffers != null && cachedOffers.size() > 0) {
			for (Offer offer : cachedOffers) {
				if (offer.getId() == notificationOfferId) {
					notificationOffer = offer;
					break;
				}
			}
			if (notificationOffer != null) {
				// while (true) {
				// if(notificationOffer.getImgBitmap() != null) {
				// break;
				// }
				// }
				// openOfferNotificationDetails(notificationOffer.getId());
			}
		}
	}

	public void openOfferNotificationDetails(Long notificationOfferId) {
		Intent intent = new Intent(getApplication(), OfferDetailsActivity.class);
		intent.putExtra(Constants.SELECTED_OFFER_ID_EXTRA_MSG,
				notificationOfferId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(intent);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
		switch (tab.getPosition()) {
			case 0 :
				if (menu != null) {
					menu.getItem(0).setVisible(true);
				}
				break;
			case 1 :
				if (menu != null) {
					menu.getItem(0).setVisible(false);
				}
				break;
			case 2 :
				if (menu != null) {
					menu.getItem(0).setVisible(false);
				}
				break;

			default :
				if (menu != null) {
					menu.getItem(0).setVisible(false);
				}
				break;
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		if (tab.getPosition() == 0
				&& Util.getRefreshOffersFlagInPreferences(context)) {
			task = new RefreshOffersTask();
			task.execute("Refresh offers");
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0 :
					return new OffersFragment();
				case 1 :
					return new CategoriesFragment();
				case 2 :
					return new SettingsFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0 :
					return getString(R.string.offers_tab_name).toUpperCase(l);
				case 1 :
					return getString(R.string.categories_tab_name).toUpperCase(
							l);
				case 2 :
					return getString(R.string.settings_tab_name).toUpperCase(l);
			}
			return null;
		}
	}

}
