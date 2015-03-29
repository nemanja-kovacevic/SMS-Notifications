package net.nemanjakovacevic.smsnotifications.activities;

import net.nemanjakovacevic.smsnotifications.App;
import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class HostActivity extends TabActivity implements Keys {

	public static final String TAG_NOTIFICATIONS = "notifications";
	public static final String TAG_SPAM = "spam";
	public static final String TAG_SETTINGS = "settings";
	public static final String TAG_ABOUT = "about";

	private App app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		app = (App) getApplication();

		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, ListOperatorsNotificationsActivity.class);
		int notificationMode = getIntent().getIntExtra(MODE, 2);
		intent.putExtra(MODE, notificationMode);
		spec = tabHost
				.newTabSpec(TAG_NOTIFICATIONS)
				.setIndicator(getResources().getString(R.string.tab_title_notifications),
						res.getDrawable(R.drawable.tab_notifications)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ListSpamMessagesActivity.class);
		spec = tabHost.newTabSpec(TAG_SPAM).setIndicator(getResources().getString(R.string.tab_title_spam),
							res.getDrawable(R.drawable.tab_spam))
						.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, PreferencesActivity.class);
		spec = tabHost.newTabSpec(TAG_SETTINGS).setIndicator(getResources().getString(R.string.tab_title_settings),
							res.getDrawable(R.drawable.tab_settings))
						.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, AboutActivity.class);
		spec = tabHost.newTabSpec(TAG_ABOUT).setIndicator(getResources().getString(R.string.tab_title_about),
							res.getDrawable(R.drawable.tab_about))
						.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		getTabHost().setCurrentTab(0);
		app.newNotifications();
	}

}
