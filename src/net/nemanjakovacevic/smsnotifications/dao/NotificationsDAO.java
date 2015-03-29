package net.nemanjakovacevic.smsnotifications.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.nemanjakovacevic.smsnotifications.db.DatabaseOpenHelper;
import net.nemanjakovacevic.smsnotifications.domain.Contact;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.producers.NotificationProducingEngine;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class NotificationsDAO {

	private static List<OperatorNotification> newMissedCallNotificationsCache;

	private static List<OperatorNotification> newAvailabilityNotificationsCache;

	private static boolean initialized = false;

	public static List<OperatorNotification> getNewMissedCallNotifications(Context context) {
		initializeNotificationsCache(context);
		return newMissedCallNotificationsCache;
	}

	public static List<OperatorNotification> getNewAvailabilityNotifications(Context context) {
		initializeNotificationsCache(context);
		return newAvailabilityNotificationsCache;
	}

	private static void initializeNotificationsCache(Context context) {
		if (!initialized) {
			DatabaseOpenHelper db = new DatabaseOpenHelper(context);
			List<OperatorNotification> unseenNotifications = db.loadUnseenNotifications();
			db.close();
			newMissedCallNotificationsCache = new ArrayList<OperatorNotification>();
			newAvailabilityNotificationsCache = new ArrayList<OperatorNotification>();
			for (OperatorNotification on : unseenNotifications) {

				if (on.getContact() != null) {
					on.setContact(retreiveContactInfo(on.getContact().getId(), context));
				}

				switch (on.getType()) {
				case MISSED_CALL:
					newMissedCallNotificationsCache.add(on);
					break;
				case AVAILABILITY:
					newAvailabilityNotificationsCache.add(on);
					break;
				}
			}
			initialized = true;
		}
	}

	public static List<OperatorNotification> getNewNotifications(Context context) {
		List<OperatorNotification> unseenNotification = new ArrayList<OperatorNotification>();
		unseenNotification.addAll(getNewMissedCallNotifications(context));
		unseenNotification.addAll(getNewAvailabilityNotifications(context));
		Collections.sort(unseenNotification);
		return unseenNotification;
	}

	public static List<OperatorNotification> getAllNotifications(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		List<OperatorNotification> allNotifications = db.loadAllNotifications();
		db.close();
		populateLoadedNotificationsWithContactsInfo(allNotifications, context);
		return allNotifications;
	}

	public static void clear(Context context) {
		if (newMissedCallNotificationsCache != null) {
			newMissedCallNotificationsCache.clear();
		}
		if (newAvailabilityNotificationsCache != null) {
			newAvailabilityNotificationsCache.clear();
		}
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.markAllNotificationsSeen();
		db.close();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}

	public static void storeNewNotifications(List<OperatorNotification> notifications, Context context) {
		populateNewNotificationsWithContactsInfo(notifications, context);
		updateNotificationsCache(notifications, context);
		persistNotifications(notifications, context);
	}

	/**
	 * Method for populating contact data into produced notifications.
	 */
	private static void populateNewNotificationsWithContactsInfo(List<OperatorNotification> notifications,
			Context context) {
		for (OperatorNotification notification : notifications) {
			String number = notification.getNumber();
			String numberBase = getNumberBase(number, context);
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					generateReplacePart() + " like '%" + numberBase + "'", null, null);
			if (cursor.getCount() != 0) {
				cursor.moveToNext();
				int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				notification.setContact(retreiveContactInfo(contactId, context));
			}
		}
	}
	
	private static String generateReplacePart(){
		String firstReplace = "replace("+ ContactsContract.CommonDataKinds.Phone.NUMBER + ", '-', '')";
		String secondReplace = "replace("+firstReplace+", '(', '')";
		String thirdReplace = "replace("+secondReplace+", ')', '')";
		String fourthReplace = "replace("+thirdReplace+", ' ', '')";
		return fourthReplace;
	}
	
	/**
	 * Method for extracting base oh phone number by trimming prefix of zero or national prefix e.g. +381
	 */
	private static String getNumberBase(String number, Context context) {
		Set<String> countryPrefixes = NotificationProducingEngine.getInstance(context).getCountryPrefixes();
		List<String> prefixes = new ArrayList<String>(countryPrefixes);
		prefixes.add("0");
		for(String prefix : prefixes){
			if(number.startsWith(prefix)){
				return number.substring(prefix.length());
			}
		}
		return null;
	}

	/**
	 * Method for populating contact data into produced notifications.
	 * 
	 * @param notifications
	 * @param context
	 */
	private static void populateLoadedNotificationsWithContactsInfo(List<OperatorNotification> notifications,
			Context context) {
		for (OperatorNotification notification : notifications) {
			if (notification.getContact() != null) {
				int contactId = notification.getContact().getId();
				notification.setContact(retreiveContactInfo(contactId, context));
			}
		}
	}

	/**
	 * Method for retrieving contact data based on contact id.
	 * 
	 * @return {@link Contact} object or null
	 */
	private static Contact retreiveContactInfo(int contactId, Context context) {
		Contact contact = null;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, BaseColumns._ID + " = ?",
				new String[] { String.valueOf(contactId) }, null);
		if (cursor.getCount() != 0) {
			cursor.moveToNext();
			contact = new Contact();
			contact.setId(contactId);
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			contact.setName(name);
			Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(context.getContentResolver(), uri);
			Bitmap photo = BitmapFactory.decodeStream(input);
			contact.setPhoto(photo);
		}
		cursor.close();
		return contact;
	}
	
	/**
	 * Method called when user creates contact out of notification unknown number.
	 * We should update the db with new contact data for every notification with this number.
	 * 
	 * @param contactUri
	 * @param notificationNumber
	 */
	public static void updateContactData(Uri contactUri, String notificationNumber, Context context) {
		try {
			long contactId = retreiveContactId(contactUri, context);
			DatabaseOpenHelper db = new DatabaseOpenHelper(context);
			db.updateContactData(notificationNumber, contactId);
			db.close();
		} catch (IllegalStateException ignore) {
			ignore.printStackTrace();
			// this should not happen because we call this method from successful onResult
			// after adding a contact. If it does happen anyway, app won't crash
		}
	}

	private static long retreiveContactId(Uri contactUri, Context context) {
		return ContentUris.parseId(contactUri);
	}

	private static void updateNotificationsCache(List<OperatorNotification> notifications, Context context) {
		for (OperatorNotification n : notifications) {
			if (n.getType() == OperatorNotification.Type.MISSED_CALL) {
				NotificationsDAO.getNewMissedCallNotifications(context).add(n);
			} else if (n.getType() == OperatorNotification.Type.AVAILABILITY) {
				NotificationsDAO.getNewAvailabilityNotifications(context).add(n);
			}
		}
	}

	private static void persistNotifications(List<OperatorNotification> notifications, Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		for (OperatorNotification notification : notifications) {
			db.storeNotification(notification);
		}
		db.close();
	}

	public static void deleteNotification(int id, Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.deleteNotification(id);
		db.close();
	}
	
	public static void deleteAllNotifications(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.deleteAllNotifications();
		db.close();
	}

	public static void trimTable(int max, Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		int count = db.getNotificationCount();
		if (count > max) {
			db.trimNotifications(max);
		}
		db.close();
	}

}