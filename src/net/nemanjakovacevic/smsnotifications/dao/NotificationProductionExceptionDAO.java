package net.nemanjakovacevic.smsnotifications.dao;

import java.util.List;

import net.nemanjakovacevic.smsnotifications.db.DatabaseOpenHelper;
import net.nemanjakovacevic.smsnotifications.exceptions.NotificationProductionException;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.content.Context;

public class NotificationProductionExceptionDAO implements Keys {

	public static void storeNotificationProductionException(Context context, NotificationProductionException npe) {
		int version = RuleSetDAO.getLocalVersionOfRulesForCountryCode(context, npe.getRulesCountryCode());
		npe.setRulesVersion(version);
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.storeNotificationProductionException(npe);
		db.close();
	}

	public static List<NotificationProductionException> loadActiveExceptions(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		List<NotificationProductionException> npes = db.loadActiveExceptions();
		db.close();
		return npes;
	}

	public static void resolved(Context context, NotificationProductionException npe) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.exceptionResolved(npe);
		db.close();
	}

	public static void reported(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.exceptionsReported();
		db.close();
	}

}
