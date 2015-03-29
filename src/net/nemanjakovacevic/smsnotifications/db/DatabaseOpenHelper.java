package net.nemanjakovacevic.smsnotifications.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.nemanjakovacevic.smsnotifications.db.tables.TNotification;
import net.nemanjakovacevic.smsnotifications.db.tables.TNotificationProductionException;
import net.nemanjakovacevic.smsnotifications.db.tables.TPreferences;
import net.nemanjakovacevic.smsnotifications.db.tables.TRuleSet;
import net.nemanjakovacevic.smsnotifications.db.tables.TSpamMessage;
import net.nemanjakovacevic.smsnotifications.domain.Contact;
import net.nemanjakovacevic.smsnotifications.domain.NotificationMessage;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.domain.RuleSet;
import net.nemanjakovacevic.smsnotifications.domain.SpamMessage;
import net.nemanjakovacevic.smsnotifications.exceptions.NotificationProductionException;
import net.nemanjakovacevic.smsnotifications.exceptions.NotificationProductionException.Status;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import net.nemanjakovacevic.smsnotifications.util.Util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flurry.android.FlurryAgent;

public class DatabaseOpenHelper extends SQLiteOpenHelper implements Keys {

	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 3;
	
	private Context context;

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TNotification.createStatement());
		db.execSQL(TSpamMessage.createStatement());
		db.execSQL(TRuleSet.createStatement());
		db.execSQL(TNotificationProductionException.createStatement());
		setUpPreferences(db);
	}
	
	public void setUpPreferences(SQLiteDatabase db){
		db.execSQL(TPreferences.createStatement());
		db.execSQL("INSERT INTO " + TPreferences.TABLE_NAME + " VALUES ('" + KEY_APP_INITIALISED + "','false')");
		db.execSQL("INSERT INTO " + TPreferences.TABLE_NAME + " VALUES ('" + KEY_RULES_INITIALY_INSTALLED + "','false')");
		db.execSQL("INSERT INTO " + TPreferences.TABLE_NAME + " VALUES ('" + KEY_USER_ACKNOWLEDGED_LACK_OF_RULES + "','false')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion == 1){
			try{
				db.execSQL(TPreferences.createStatement());
				db.execSQL("INSERT INTO " + TPreferences.TABLE_NAME + " VALUES ('" + KEY_APP_INITIALISED + "','false')");
				db.execSQL("INSERT INTO " + TPreferences.TABLE_NAME + " VALUES ('" + KEY_RULES_INITIALY_INSTALLED + "','false')");
				db.execSQL("INSERT INTO " + TPreferences.TABLE_NAME + " VALUES ('" + KEY_USER_ACKNOWLEDGED_LACK_OF_RULES + "','false')");
			}catch(Exception ignore){
			}
		} else if (oldVersion == 2){
			// update to version with analytics, survey data event should be posted
			reportSpamSendersCount();
		}
	}

	private void reportSpamSendersCount() {
		try {
			int spamSendersCount = Util.getSpamSendersCount(context);
			Map<String, String> eventParams = new HashMap<String, String>();
			eventParams.put(ANALYTICS_SPAM_SENDER_COUNT_PARAM, String.valueOf(spamSendersCount));
			FlurryAgent.onEvent(ANALYTICS_INTRO_EVENT, eventParams);
		} catch (Exception ignore) {
			// should be no problems, but any issue should not annoy user
		}
	}

	public void storeNotification(OperatorNotification notification) {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TNotification.Column.type.name(), notification.getType().code());
		values.put(TNotification.Column.number.name(), notification.getNumber());
		if (notification.getContact() != null) {
			values.put(TNotification.Column.contact_id.name(), notification.getContact().getId());
		} else {
			values.put(TNotification.Column.contact_id.name(), 0);
		}
		values.put(TNotification.Column.number_of_calls.name(), notification.getNumberOfCalls());
		values.put(TNotification.Column.timestamp.name(), notification.getTimestamp().getTime());
		values.put(TNotification.Column.seen.name(), 0);
		this.getWritableDatabase().insert(TNotification.TABLE_NAME, null, values);
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public void storeSpam(NotificationMessage notificationMessage) {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TSpamMessage.Column.sender.name(), notificationMessage.getSender());
		values.put(TSpamMessage.Column.body.name(), notificationMessage.getBody());
		values.put(TSpamMessage.Column.timestamp.name(), System.currentTimeMillis());
		this.getWritableDatabase().insert(TSpamMessage.TABLE_NAME, null, values);
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public int getSpamCount() {
		Cursor cursor = this.getReadableDatabase().query(TSpamMessage.TABLE_NAME,
				new String[] { "count(" + TSpamMessage.Column.id + ")" }, null, null, null, null, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public int getNotificationCount() {
		Cursor cursor = this.getReadableDatabase().query(TNotification.TABLE_NAME,
				new String[] { "count(" + TNotification.Column.id + ")" }, null, null, null, null, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public void deleteOldestSpamMessage() {
		Cursor cursor = this.getReadableDatabase().query(TSpamMessage.TABLE_NAME,
				new String[] { "min(" + TSpamMessage.Column.timestamp + ")" }, null, null, null, null, null);
		cursor.moveToNext();
		int timestamp = cursor.getInt(0);
		cursor.close();
		this.getWritableDatabase().beginTransaction();
		this.getWritableDatabase().delete(TSpamMessage.TABLE_NAME, TSpamMessage.Column.timestamp + " = ?",
				new String[] { String.valueOf(timestamp) });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public void deleteSpamMessage(int id) {
		this.getWritableDatabase().beginTransaction();
		this.getWritableDatabase().delete(TSpamMessage.TABLE_NAME, TSpamMessage.Column.id + " = ?",
				new String[] { String.valueOf(id) });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public void deleteNotification(int id) {
		this.getWritableDatabase().beginTransaction();
		this.getWritableDatabase().delete(TNotification.TABLE_NAME, TNotification.Column.id + " = ?",
				new String[] { String.valueOf(id) });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}
	
	public void deleteAllNotifications() {
		this.getWritableDatabase().beginTransaction();
		this.getWritableDatabase().delete(TNotification.TABLE_NAME, null, null);
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public List<OperatorNotification> loadUnseenNotifications() {
		Cursor cursor = this.getReadableDatabase().query(TNotification.TABLE_NAME, TNotification.columns(),
				TNotification.Column.seen.name() + " = 0", null, null, null,
				TNotification.Column.timestamp.name() + " ASC");
		List<OperatorNotification> notifications = new ArrayList<OperatorNotification>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(TNotification.Column.id.name()));
			OperatorNotification.Type type = OperatorNotification.Type.getTypeFromCode(cursor.getInt(cursor
					.getColumnIndex(TNotification.Column.type.name())));
			String number = cursor.getString(cursor.getColumnIndex(TNotification.Column.number.name()));
			int contactId = cursor.getInt(cursor.getColumnIndex(TNotification.Column.contact_id.name()));
			int numberOfCalls = cursor.getInt(cursor.getColumnIndex(TNotification.Column.number_of_calls.name()));
			long timestamp = cursor.getLong(cursor.getColumnIndex(TNotification.Column.timestamp.name()));
			Contact contact = null;
			if (contactId != 0) {
				contact = new Contact();
				contact.setId(contactId);
			}
			notifications.add(new OperatorNotification(id, type, number, contact, numberOfCalls, new Date(timestamp),
					false));
		}
		cursor.close();
		return notifications;
	}

	public void markAllNotificationsSeen() {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TNotification.Column.seen.name(), 1);
		this.getWritableDatabase().update(TNotification.TABLE_NAME, values, null, null);
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public List<OperatorNotification> loadAllNotifications() {
		Cursor cursor = this.getReadableDatabase().query(TNotification.TABLE_NAME, TNotification.columns(), null, null,
				null, null, TNotification.Column.timestamp.name() + " DESC");
		List<OperatorNotification> notifications = new ArrayList<OperatorNotification>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(TNotification.Column.id.name()));
			OperatorNotification.Type type = OperatorNotification.Type.getTypeFromCode(cursor.getInt(cursor
					.getColumnIndex(TNotification.Column.type.name())));
			String number = cursor.getString(cursor.getColumnIndex(TNotification.Column.number.name()));
			int contactId = cursor.getInt(cursor.getColumnIndex(TNotification.Column.contact_id.name()));
			int numberOfCalls = cursor.getInt(cursor.getColumnIndex(TNotification.Column.number_of_calls.name()));
			long timestamp = cursor.getLong(cursor.getColumnIndex(TNotification.Column.timestamp.name()));
			Contact contact = null;
			if (contactId != 0) {
				contact = new Contact();
				contact.setId(contactId);
			}
			notifications.add(new OperatorNotification(id, type, number, contact, numberOfCalls, new Date(timestamp),
					false));
		}
		cursor.close();
		return notifications;
	}

	public List<SpamMessage> loadAllSpamMessages() {
		Cursor cursor = this.getReadableDatabase().query(TSpamMessage.TABLE_NAME, TSpamMessage.columns(), null, null,
				null, null, TSpamMessage.Column.timestamp.name() + " DESC");
		List<SpamMessage> spamMessages = new ArrayList<SpamMessage>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(TSpamMessage.Column.id.name()));
			String sender = cursor.getString(cursor.getColumnIndex(TSpamMessage.Column.sender.name()));
			String body = cursor.getString(cursor.getColumnIndex(TSpamMessage.Column.body.name()));
			long timestamp = cursor.getLong(cursor.getColumnIndex(TSpamMessage.Column.timestamp.name()));
			spamMessages.add(new SpamMessage(id, sender, body, new Date(timestamp)));
		}
		cursor.close();
		return spamMessages;
	}

	public void trimNotifications(int max) {
		this.getWritableDatabase().beginTransaction();
		String innerSelect = "select " + TNotification.Column.id +
								" from " + TNotification.TABLE_NAME +
								" order by " + TNotification.Column.timestamp + " desc limit " + max;
		this.getWritableDatabase().delete(
				TNotification.TABLE_NAME,
				TNotification.Column.id + " not in (" + innerSelect + ")",
				null);
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public void trimSpamMessages(int max) {
		this.getWritableDatabase().beginTransaction();
		String innerSelect = "select " + TSpamMessage.Column.id +
								" from " + TSpamMessage.TABLE_NAME +
								" order by " + TSpamMessage.Column.timestamp + " desc limit " + max;
		this.getWritableDatabase().delete(
				TSpamMessage.TABLE_NAME,
				TSpamMessage.Column.id + " not in (" + innerSelect + ")",
				null);
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public void insertRuleSet(RuleSet ruleSet) {
		getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TRuleSet.Column.country_code.name(), ruleSet.getCountryCode());
		values.put(TRuleSet.Column.country_name.name(), ruleSet.getCountryName());
		values.put(TRuleSet.Column.version.name(), ruleSet.getLocalVersion());
		values.put(TRuleSet.Column.file_name.name(), ruleSet.getFileName());
		getWritableDatabase().insert(TRuleSet.TABLE_NAME, null, values);
		getWritableDatabase().setTransactionSuccessful();
		getWritableDatabase().endTransaction();
	}

	public List<RuleSet> loadLocalRuleSets() {
		Cursor cursor = this.getReadableDatabase().query(TRuleSet.TABLE_NAME, TRuleSet.columns(), null, null,
				null, null, null);
		List<RuleSet> localeRuleSets = new ArrayList<RuleSet>();
		while (cursor.moveToNext()) {
			String countryCode = cursor.getString(cursor.getColumnIndex(TRuleSet.Column.country_code.name()));
			String countryName = cursor.getString(cursor.getColumnIndex(TRuleSet.Column.country_name.name()));
			int version = cursor.getInt(cursor.getColumnIndex(TRuleSet.Column.version.name()));
			String fileName = cursor.getString(cursor.getColumnIndex(TRuleSet.Column.file_name.name()));
			localeRuleSets.add(new RuleSet(countryCode, countryName, version, 0, fileName));
		}
		cursor.close();
		return localeRuleSets;
	}

	public void deleteRuleSet(RuleSet ruleSet) {
		this.getWritableDatabase().beginTransaction();
		this.getWritableDatabase().delete(TRuleSet.TABLE_NAME, TRuleSet.Column.country_code + " = ?",
				new String[] { ruleSet.getCountryCode() });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public int getLocalVersionOfRulesForCountryCode(String countryCode) {
		Cursor cursor = this.getReadableDatabase().query(TRuleSet.TABLE_NAME,
				new String[] { TRuleSet.Column.version.name() }, TRuleSet.Column.country_code + " = ?",
				new String[] { countryCode }, null, null, null);
		int localVersion = 0;
		if (cursor.moveToNext()) {
			localVersion = cursor.getInt(0);
		}
		cursor.close();
		return localVersion;
	}

	public void storeNotificationProductionException(NotificationProductionException npe) {
		getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TNotificationProductionException.Column.country_code.name(), npe.getRulesCountryCode());
		values.put(TNotificationProductionException.Column.rules_version.name(), npe.getRulesVersion());
		values.put(TNotificationProductionException.Column.message.name(), npe.getMessage());
		values.put(TNotificationProductionException.Column.sender.name(), npe.getNotificationMessage().getSender());
		values.put(TNotificationProductionException.Column.body.name(), npe.getNotificationMessage().getBody());
		values.put(TNotificationProductionException.Column.status.name(), npe.getStatus().getCode());
		getWritableDatabase().insert(TNotificationProductionException.TABLE_NAME, null, values);
		getWritableDatabase().setTransactionSuccessful();
		getWritableDatabase().endTransaction();
	}

	public List<NotificationProductionException> loadActiveExceptions() {
		Cursor cursor = this.getReadableDatabase().query(TNotificationProductionException.TABLE_NAME,
				TNotificationProductionException.columns(),
				TNotificationProductionException.Column.status.name() + " = ?",
				new String[] { String.valueOf(Status.OPEN.getCode()) }, null, null, null);
		List<NotificationProductionException> npes = new ArrayList<NotificationProductionException>();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(TNotificationProductionException.Column.id.name()));
			String countryCode = cursor.getString(cursor
					.getColumnIndex(TNotificationProductionException.Column.country_code.name()));
			int rulesVersion = cursor.getInt(cursor
					.getColumnIndex(TNotificationProductionException.Column.rules_version.name()));
			String message = cursor.getString(cursor.getColumnIndex(TNotificationProductionException.Column.message
					.name()));
			String body = cursor.getString(cursor.getColumnIndex(TNotificationProductionException.Column.body.name()));
			String sender = cursor.getString(cursor.getColumnIndex(TNotificationProductionException.Column.sender
					.name()));
			npes.add(new NotificationProductionException(id, message, countryCode,
					new NotificationMessage(sender, body), rulesVersion));
		}
		cursor.close();
		return npes;
	}

	public void exceptionResolved(NotificationProductionException npe) {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TNotificationProductionException.Column.status.name(), Status.RECOVERED.getCode());
		this.getWritableDatabase().update(TNotificationProductionException.TABLE_NAME, values,
				TNotificationProductionException.Column.id + " = ?", new String[] { String.valueOf(npe.getId()) });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public void exceptionsReported() {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TNotificationProductionException.Column.status.name(), Status.REPORTED.getCode());
		this.getWritableDatabase().update(TNotificationProductionException.TABLE_NAME, values,
				TNotificationProductionException.Column.status + " = ?",
				new String[] { String.valueOf(Status.OPEN.getCode()) });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public boolean isInitialized() {
		Cursor cursor = this.getReadableDatabase().query(
				TPreferences.TABLE_NAME, TPreferences.columns(),
				TPreferences.Column.key.name() + " = ?",
				new String[] { KEY_APP_INITIALISED }, null, null, null);
		cursor.moveToNext();
		String value = cursor.getString(cursor
				.getColumnIndex(TPreferences.Column.value.name()));
		cursor.close();
		return Boolean.valueOf(value);
	}

	public void initialize() {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TPreferences.Column.value.name(), "true");
		this.getWritableDatabase().update(TPreferences.TABLE_NAME, values,
				TPreferences.Column.key + " = ?",
				new String[] { KEY_APP_INITIALISED });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}
	
	public boolean areRulesInitialyInstalled() {
		Cursor cursor = this.getReadableDatabase().query(
				TPreferences.TABLE_NAME, TPreferences.columns(),
				TPreferences.Column.key.name() + " = ?",
				new String[] { KEY_RULES_INITIALY_INSTALLED }, null, null, null);
		cursor.moveToNext();
		String value = cursor.getString(cursor
				.getColumnIndex(TPreferences.Column.value.name()));
		cursor.close();
		return Boolean.valueOf(value);
	}

	public void rulesAreInitialyInstalled() {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TPreferences.Column.value.name(), "true");
		this.getWritableDatabase().update(TPreferences.TABLE_NAME, values,
				TPreferences.Column.key + " = ?",
				new String[] { KEY_RULES_INITIALY_INSTALLED });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public boolean hasUserAcknowledgedLackOfRules() {
		Cursor cursor = this.getReadableDatabase().query(
				TPreferences.TABLE_NAME, TPreferences.columns(),
				TPreferences.Column.key.name() + " = ?",
				new String[] { KEY_USER_ACKNOWLEDGED_LACK_OF_RULES }, null, null, null);
		cursor.moveToNext();
		String value = cursor.getString(cursor
				.getColumnIndex(TPreferences.Column.value.name()));
		cursor.close();
		return Boolean.valueOf(value);
	}

	public void userAcknowledgedLackOfRules() {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TPreferences.Column.value.name(), "true");
		this.getWritableDatabase().update(TPreferences.TABLE_NAME, values,
				TPreferences.Column.key + " = ?",
				new String[] { KEY_USER_ACKNOWLEDGED_LACK_OF_RULES });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

	public void updateContactData(String notificationNumber, long contactId) {
		this.getWritableDatabase().beginTransaction();
		ContentValues values = new ContentValues();
		values.put(TNotification.Column.contact_id.name(), contactId);
		this.getWritableDatabase().update(TNotification.TABLE_NAME, values, TNotification.Column.number + " = ?", new String[] { notificationNumber });
		this.getWritableDatabase().setTransactionSuccessful();
		this.getWritableDatabase().endTransaction();
	}

}
