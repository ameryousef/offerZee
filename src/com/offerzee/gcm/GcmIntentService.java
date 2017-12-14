package com.offerzee.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.offerzee.R;
import com.offerzee.StartActivity;
import com.offerzee.util.Constants;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				sendNotification(extras);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(Bundle extras) {
		Long offerId = Long.parseLong(extras.getString("id"));
		String offerMsg = extras.getString("message");
		Resources rs = getResources();
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

//		Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/"
//				+ R.raw.notisound);
		
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Intent StartIntent = new Intent(this, StartActivity.class);
		StartIntent.putExtra(Constants.NOTIFICATION_OFFER_ID_EXTRA_MSG, offerId);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				StartIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
				.setSound(soundUri)
				.setContentTitle(rs.getString(R.string.app_name))
				.setContentText(offerMsg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
