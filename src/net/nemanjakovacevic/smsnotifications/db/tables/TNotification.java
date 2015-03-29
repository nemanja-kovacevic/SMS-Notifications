package net.nemanjakovacevic.smsnotifications.db.tables;

public class TNotification {

	public static final String TABLE_NAME = "notification";
	
	public static String createStatement(){
		return "CREATE TABLE " + TABLE_NAME + " (" +
        Column.id + " INTEGER PRIMARY KEY, " +
        Column.type + " INTEGER, " +
        Column.number + " TEXT," + 
        Column.contact_id + " INTEGER," +
        Column.number_of_calls + " INTEGER," +
        Column.timestamp + " INTEGER," +
        Column.seen + " INTEGER" +
        ");";
	}
	
	
	public enum Column {
		id,
		type,
		number,
		contact_id,
		number_of_calls,
		timestamp,
		seen;
	}


	public static String[] columns() {
		return new String[] {Column.id.name(), Column.type.name(), Column.number.name(), Column.contact_id.name(), Column.number_of_calls.name(), Column.timestamp.name(), Column.seen.name()};
	}
}

