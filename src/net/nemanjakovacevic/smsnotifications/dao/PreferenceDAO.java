package net.nemanjakovacevic.smsnotifications.dao;

import net.nemanjakovacevic.smsnotifications.db.DatabaseOpenHelper;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.content.Context;

public class PreferenceDAO implements Keys {

	public static boolean isInitialized(Context context){
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		boolean isInitialized = db.isInitialized();
		db.close();
		return isInitialized;
	}

	public static void initialize(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.initialize();
		db.close();
	}
	
	public static boolean areRulesInitialyInstalled(Context context){
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		boolean installed = db.areRulesInitialyInstalled();
		db.close();
		return installed;
	}

	public static void rulesAreInitialyInstalled(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.rulesAreInitialyInstalled();
		db.close();
	}

	public static boolean hasUserAcknowledgedLackOfRules(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		boolean acknoledged = db.hasUserAcknowledgedLackOfRules();
		db.close();
		return acknoledged;
	}
	
	public static void userAcknowledgedLackOfRules(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.userAcknowledgedLackOfRules();
		db.close();
	}

}
