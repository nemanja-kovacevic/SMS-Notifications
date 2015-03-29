package net.nemanjakovacevic.smsnotifications.receivers;

import java.util.Iterator;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.dao.NotificationProductionExceptionDAO;
import net.nemanjakovacevic.smsnotifications.dao.NotificationsDAO;
import net.nemanjakovacevic.smsnotifications.dao.PreferenceDAO;
import net.nemanjakovacevic.smsnotifications.dao.RuleSetDAO;
import net.nemanjakovacevic.smsnotifications.dao.SpamDAO;
import net.nemanjakovacevic.smsnotifications.domain.NotificationMessage;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.exceptions.CountryCodeNotSupportedException;
import net.nemanjakovacevic.smsnotifications.exceptions.InternetConnenctionException;
import net.nemanjakovacevic.smsnotifications.exceptions.NotNotificationException;
import net.nemanjakovacevic.smsnotifications.exceptions.NotificationProductionException;
import net.nemanjakovacevic.smsnotifications.producers.NotificationProducingEngine;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import net.nemanjakovacevic.smsnotifications.util.Util;
import net.nemanjakovacevic.smsnotifications.util.Util.MessageType;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

/**
 * BroadcastReceiver with purpose of receiving sms broadcasts and evaluating them to 
 * determine are they an operator notification, spam or ordinary message.
 * 
 * @author nemanjakovacevic
 * 
 */
public class SmsInterceptor extends BroadcastReceiver implements Keys {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			
			if (intent.getExtras() == null) {
				return;
			}
			
			NotificationMessage message = extractMessage(intent);
			
			boolean recognizedAndProcessed = interceptSpam(message, context);

			if (recognizedAndProcessed) {
				return;
			}
			
			if(!PreferenceDAO.areRulesInitialyInstalled(context)){
				if(PreferenceDAO.hasUserAcknowledgedLackOfRules(context)){
					return;
				}else{
					tryToInstallDefaultRuleSet(context, intent);
				}
			}

			interceptOperatorsNotification(message, context);
		}
	}

	

	private NotificationMessage extractMessage(Intent intent) {
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");
		
		String sender = null;
		String body = "";
		
		for(Object pdu : pdus){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
			if(sender == null){
				sender = smsMessage.getOriginatingAddress();
			}
			body += smsMessage.getMessageBody();
		}
		
		NotificationMessage notificationMessage = new NotificationMessage(sender, body);
		return notificationMessage;
	}

	/**
	 * Method for intercepting spam messages. If recognize spam it will store it and abort further sms broadcasting.
	 * 
	 * @param smsMessage
	 * @param context
	 * @return true if this sms is recognized and processed as spam and further processing should be aborted. false
	 *         otherwise.
	 */
	private boolean interceptSpam(NotificationMessage notificationMessage, Context context) {
		String rawSpamSenders = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_SPAM_SENDERS, "");
		List<String> spamSenders = Util.getSpamSenders(rawSpamSenders);
		if(spamSenders == null){
			return false;
		}
		for(String spamSender : spamSenders){
			if(spamSender.equalsIgnoreCase(notificationMessage.getSender())){
				asyncStoreNewSpam(notificationMessage, context);
				abortFurtherSmsBroadcast();
				return true;
			}
		}
		return false;
	}
	
	private void asyncStoreNewSpam(final NotificationMessage notificationMessage, final Context context) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				SpamDAO.storeNewSmsSpam(notificationMessage, context);
				return null;
			}

		}.execute();
	}

	private void abortFurtherSmsBroadcast() {
		abortBroadcast();
	}
	
	private void tryToInstallDefaultRuleSet(Context context, Intent intent) {
		try {
			RuleSetDAO.installDefaultRuleSet(context);
		} catch (InternetConnenctionException exception) {
			Intent notificationIntent = new Intent(INTENT_HANDLE_EXCEPTION);
			notificationIntent.putExtra(MESSAGE, context.getString(R.string.default_rules_install_internet_connection_problem));
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			Notification systemNotification = new Notification(R.drawable.error, context.getString(R.string.attention_requiered), System.currentTimeMillis());
			systemNotification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.attention_requiered), contentIntent);
			systemNotification.defaults |= Notification.DEFAULT_SOUND;
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(NOTIFICATION_ID_ERROR, systemNotification);
		}catch(CountryCodeNotSupportedException ex){
			Intent notificationIntent = new Intent(INTENT_HANDLE_EXCEPTION);
			notificationIntent.putExtra(MESSAGE, context.getString(R.string.country_code_not_supported_problem, ex.getCountryCode()));
			intent.putExtra(MESSAGE_TYPE, MessageType.INTERNET_CONECTION_PROBLEM.getCode());
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			Notification systemNotification = new Notification(R.drawable.error, context.getString(R.string.attention_requiered), System.currentTimeMillis());
			systemNotification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.attention_requiered), contentIntent);
			systemNotification.defaults |= Notification.DEFAULT_SOUND;
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(NOTIFICATION_ID_ERROR, systemNotification);
		}
	}
	
	/**
	 * Method for intercepting operator's notifications. If recognize operator's notification it will store it,
	 * broadcast all new operator's notifications as android notification and abort further sms broadcasting.
	 * 
	 * @param notificationMessage
	 * @param context
	 * @return true if this sms is recognized and processed as operator's notification and further processing should be
	 *         aborted. false otherwise.
	 */
	private boolean interceptOperatorsNotification(NotificationMessage notificationMessage, Context context) {
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(context);
		try {
			List<OperatorNotification> notifications = engine.produceNotifications(notificationMessage);
			abortFurtherSmsBroadcast();
			asyncStoreAndBroadcastNotifications(notifications, context);
			return true;
		} catch (NotNotificationException ignore) {
			
		} catch (NotificationProductionException e) {
			
			NotificationProductionExceptionDAO.storeNotificationProductionException(context, e);
			
			Intent intent = new Intent(INTENT_NOTIFICATION_PRODUCTION_EXCEPTION);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
			Notification systemNotification = new Notification(R.drawable.error, context.getString(R.string.attention_requiered), System.currentTimeMillis());
			systemNotification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.attention_requiered), contentIntent);
			systemNotification.defaults |= Notification.DEFAULT_SOUND;
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(NOTIFICATION_ID_ERROR, systemNotification);
		}
		return false;
	}

	private void asyncStoreAndBroadcastNotifications(final List<OperatorNotification> notifications,
			final Context context) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				NotificationsDAO.storeNewNotifications(notifications, context);
				broadcastNotifications(context);
				return null;
			}
		}.execute();
	}

	public static void broadcastNotifications(Context context) {
		for (OperatorNotification.Type type : OperatorNotification.Type.values()) {

			List<OperatorNotification> notifications = null;
			int icon = 0;
			StringBuilder contentTitle = new StringBuilder();

			switch (type) {
			case MISSED_CALL:
				notifications = NotificationsDAO.getNewMissedCallNotifications(context);
				if (notifications.isEmpty()) {
					continue;
				}
				icon = R.drawable.missed_call;
				if (notifications.size() == 1) {
					contentTitle.append(context.getString(R.string.type_missed_call_ticker_text));
				} else {
					contentTitle.append(context.getString(R.string.type_multiple_missed_call_ticker_text));
				}
				break;
			case AVAILABILITY:
				notifications = NotificationsDAO.getNewAvailabilityNotifications(context);
				if (notifications.isEmpty()) {
					continue;
				}
				icon = R.drawable.availability;
				if (notifications.size() == 1) {
					contentTitle.append(context.getString(R.string.type_availability_ticker_text));
				} else {
					contentTitle.append(context.getString(R.string.type_multiple_availability_ticker_text));
				}
				break;
			default:
				continue;
			}

			StringBuilder text = new StringBuilder();

			if (notifications.size() == 1) {
				OperatorNotification notification = notifications.get(0);
				text.append(notification);
			} else {
				Iterator<OperatorNotification> iterator = notifications.iterator();
				while (iterator.hasNext()) {
					OperatorNotification notification = iterator.next();
					text.append(notification);
					if (iterator.hasNext()) {
						text.append(", ");
					}
				}
			}

			Intent intent = new Intent(INTENT_SHOW_NOTIFICATIONS);
			intent.putExtra(MODE,
					NEW_NOTIFICATIONS);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
			Notification systemNotification = new Notification(icon, text, System.currentTimeMillis());
			systemNotification.defaults |= Notification.DEFAULT_SOUND;
			systemNotification.setLatestEventInfo(context, contentTitle, text, contentIntent);
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(type.code(), systemNotification);
		}
	}

}