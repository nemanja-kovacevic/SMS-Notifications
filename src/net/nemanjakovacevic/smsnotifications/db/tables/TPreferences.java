package net.nemanjakovacevic.smsnotifications.db.tables;


public class TPreferences {

public static final String TABLE_NAME = "preferences";
	
	public static String createStatement(){
		return "CREATE TABLE " + TABLE_NAME + " (" +
        Column.key + " TEXT PRIMARY KEY, " +
        Column.value + " TEXT " +
        ");";
	}
	
	
	public enum Column {
		key,
		value;
	}


	public static String[] columns() {
		return new String[] {Column.key.name(), Column.value.name()};
	}
	
}
