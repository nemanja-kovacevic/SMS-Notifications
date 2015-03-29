package net.nemanjakovacevic.smsnotifications.activities;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.dao.NotificationsDAO;
import net.nemanjakovacevic.smsnotifications.dao.SpamDAO;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, Keys {

	private ListPreference notificationsStoreLimitPreference;
	private ListPreference spamStoreLimitPreference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(KEY_NOTIFICATIONS_STORE_LIMIT)) {
			String newValue = sharedPreferences.getString(KEY_NOTIFICATIONS_STORE_LIMIT, "0");
			String message = this.getResources().getString(
					R.string.operators_notifications_preference_notifications_store_limit_summary);
			message = String.format(message, newValue);
			getNotificationsStoreLimitPreference().setSummary(message);
			
			NotificationsDAO.trimTable(Integer.parseInt(newValue), this);
		} else if(key.equals(KEY_SPAM_STORE_LIMIT)){
			String newValue = sharedPreferences.getString(KEY_SPAM_STORE_LIMIT, "0");
			String message;
			if(newValue.equals("0")){
				message = getResources().getString(R.string.do_not_store_spam);
			}else{
				message = this.getResources().getString(
						R.string.spam_preference_store_limit_summary);
				message = String.format(message,newValue);
			}
			getSpamStoreLimitPreference().setSummary(message);
			
			SpamDAO.trimTable(Integer.parseInt(newValue), this);
		}
	}

	private ListPreference getNotificationsStoreLimitPreference() {
		if (notificationsStoreLimitPreference == null) {
			notificationsStoreLimitPreference = (ListPreference) getPreferenceScreen().findPreference(
					KEY_NOTIFICATIONS_STORE_LIMIT);
		}
		return notificationsStoreLimitPreference;
	}
	
	private ListPreference getSpamStoreLimitPreference() {
		if (spamStoreLimitPreference == null) {
			spamStoreLimitPreference = (ListPreference) getPreferenceScreen().findPreference(
					KEY_SPAM_STORE_LIMIT);
		}
		return spamStoreLimitPreference;
	}

	@Override
	protected void onResume() {
		String value = getPreferenceScreen().getSharedPreferences().getString(KEY_NOTIFICATIONS_STORE_LIMIT, "0");
		String message = this.getResources().getString(
				R.string.operators_notifications_preference_notifications_store_limit_summary);
		message = String.format(message,value);
		getNotificationsStoreLimitPreference().setSummary(message);
		
		String value2 = getPreferenceScreen().getSharedPreferences().getString(KEY_SPAM_STORE_LIMIT, "0");
		String message2;
		if(value2.equals("0")){
			message2 = getResources().getString(R.string.do_not_store_spam);
		}else{
			message2 = this.getResources().getString(
					R.string.spam_preference_store_limit_summary);
			message2 = String.format(message2,value2);
		}
		getSpamStoreLimitPreference().setSummary(message2);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

}
