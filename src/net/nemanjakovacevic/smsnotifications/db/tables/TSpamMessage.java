package net.nemanjakovacevic.smsnotifications.db.tables;


public class TSpamMessage {

public static final String TABLE_NAME = "spam_message";
	
	public static String createStatement(){
		return "CREATE TABLE " + TABLE_NAME + " (" +
        Column.id + " INTEGER PRIMARY KEY, " +
        Column.sender + " TEXT, " +
        Column.body + " TEXT," + 
        Column.timestamp + " INTEGER" +
        ");";
	}
	
	
	public enum Column {
		id,
		sender,
		body,
		timestamp;
	}


	public static String[] columns() {
		return new String[] {Column.id.name(), Column.sender.name(), Column.body.name(), Column.timestamp.name()};
	}
	
}
