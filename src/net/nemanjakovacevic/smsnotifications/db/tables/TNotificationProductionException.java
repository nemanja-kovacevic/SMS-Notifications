package net.nemanjakovacevic.smsnotifications.db.tables;


public class TNotificationProductionException {

public static final String TABLE_NAME = "notification_production_exception";
	
	public static String createStatement(){
		return "CREATE TABLE " + TABLE_NAME + " (" +
		Column.id + " INTEGER PRIMARY KEY, " +
        Column.country_code + " TEXT, " +
        Column.rules_version + " INTEGER," + 
        Column.message + " TEXT, " +
        Column.sender + " TEXT, " +
        Column.body + " TEXT, " +
        Column.status + " INTEGER" +
        ");";
	}
	
	
	public enum Column {
		id,
		country_code,
		rules_version,
		message,
		sender,
		body,
		status;
	}


	public static String[] columns() {
		return new String[] {Column.id.name(), Column.country_code.name(), Column.rules_version.name(), Column.message.name(), Column.sender.name(), Column.body.name(), Column.status.name()};
	}
	
}
