package net.nemanjakovacevic.smsnotifications.dao;

import java.util.List;

import net.nemanjakovacevic.smsnotifications.db.DatabaseOpenHelper;
import net.nemanjakovacevic.smsnotifications.domain.NotificationMessage;
import net.nemanjakovacevic.smsnotifications.domain.SpamMessage;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SpamDAO implements Keys {

	/**
	 * Method for persisting sms spam messages. Method checks user preferences. If user preference is
	 * not to store spam no persisting takes place. Otherwise methods checks count of persisted messages
	 * and deletes one if necessary.
	 * 
	 * @param smsMessage
	 * @param context
	 */
	public static void storeNewSmsSpam(NotificationMessage notificationMessage, Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int storeLimitOnSpam = Integer.parseInt(preferences.getString(KEY_SPAM_STORE_LIMIT, "0"));
		if(storeLimitOnSpam > 0){
			DatabaseOpenHelper db = new DatabaseOpenHelper(context);
			db.storeSpam(notificationMessage);
			if(db.getSpamCount() > storeLimitOnSpam){
				db.deleteOldestSpamMessage();
			}
			db.close();
		}
	}
	
	public static List<SpamMessage> getAllSpamMessages(Context context){
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		List<SpamMessage> allSpamMessages = db.loadAllSpamMessages();
		db.close();
		return allSpamMessages;
	}

	public static void deleteSpamMessage(int spamMessageId, Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.deleteSpamMessage(spamMessageId);
		db.close();
	}

	public static void trimTable(int max, Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		int count = db.getNotificationCount();
		if(count > max){
			db.trimSpamMessages(max);
		}
		db.close();
	}

}
