package net.nemanjakovacevic.smsnotifications;

import net.nemanjakovacevic.smsnotifications.activities.InitializeAppActivity;
import net.nemanjakovacevic.smsnotifications.dao.PreferenceDAO;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class App extends Application implements Keys {

	private NewNotificationsListener newNotificationsListener;

	public void setNewNotificationsListener(
			NewNotificationsListener newNotificationsListener) {
		this.newNotificationsListener = newNotificationsListener;
	}

	public void newNotifications() {
		if (newNotificationsListener != null) {
			newNotificationsListener.newNotifications();
		}
	}

	public static interface NewNotificationsListener {

		public void newNotifications();

	}

	public static boolean isInitialized(Context context) {
		return PreferenceDAO.isInitialized(context);
	}

	public static void initialize(Context context) {
		PreferenceDAO.initialize(context);
		Intent intent = new Intent(context, InitializeAppActivity.class);
		context.startActivity(intent);
	}

}
