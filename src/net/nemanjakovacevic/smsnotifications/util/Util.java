package net.nemanjakovacevic.smsnotifications.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.activities.ExceptionHandlerActivity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class Util implements Keys {

	public static Date trim(Date aDate) {
		return new Date(aDate.getYear(), aDate.getMonth(), aDate.getDate());
	}

	public static void debugPrintPduArray(byte[] pdu) {
		System.out.print("{");
		for (int i = 0; i < pdu.length; i++) {
			System.out.print(pdu[i]);
			if (i < pdu.length - 1) {
				System.out.print(", ");
			}
		}
		System.out.print("}");
		System.out.println();
	}

	public static void notifyUser(Context context, MessageType messageType, int messageId, Object... messageArgs) {
		Intent intent = new Intent(context, ExceptionHandlerActivity.class);
		intent.putExtra(MESSAGE, context.getString(messageId, messageArgs));
		intent.putExtra(MESSAGE_TYPE, messageType.getCode());
		context.startActivity(intent);
	}
	
	public static enum MessageType {
		
		INTERNET_CONECTION_PROBLEM(1);

		private int code;
		
		private MessageType(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
		
	}

	public static String getCountryNameByOperator(Context context) {
		String countryCodeByOperator = getCountryCodeByOperator(context);
		return context.getString(getCountryResId(countryCodeByOperator));
	}

	private static int getCountryResId(String countryCodeByOperator) {
		if (countryCodeByOperator.equals("us")) {
			return R.string.us;
		} else if (countryCodeByOperator.equals("rs")) {
			return R.string.rs;
		} else {
			return R.string.unknown;
		}
	}

	public static String getCountryCodeByOperator(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String countryCodeByOperator = tm.getSimCountryIso().toLowerCase();
		return countryCodeByOperator;
	}
	
	public static List<String> getSpamSenders(String rawSpamSenders) {
		if(rawSpamSenders.equals("")){
			return new ArrayList<String>();
		}
		return new ArrayList<String>(Arrays.asList(rawSpamSenders.split(";")));
	}

	public static int getSpamSendersCount(Context context) {
		int blockedNumberCount = -1;
		try {
			String rawSpamSenders = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_SPAM_SENDERS, "");
			List<String> spamSenders = Util.getSpamSenders(rawSpamSenders);
			if(spamSenders == null){
				blockedNumberCount = 0;
			} else {
				blockedNumberCount = spamSenders.size();
			}
		} catch (Exception ignore) {
		}
		return blockedNumberCount;
	}

}
