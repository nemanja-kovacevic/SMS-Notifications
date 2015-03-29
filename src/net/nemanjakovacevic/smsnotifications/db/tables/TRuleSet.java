package net.nemanjakovacevic.smsnotifications.db.tables;


public class TRuleSet {

public static final String TABLE_NAME = "rule_set";
	
	public static String createStatement(){
		return "CREATE TABLE " + TABLE_NAME + " (" +
        Column.country_code + " TEXT PRIMARY KEY, " +
        Column.country_name + " TEXT, " +
        Column.version + " INTEGER," + 
        Column.file_name + " TEXT" +
        ");";
	}
	
	
	public enum Column {
		country_code,
		country_name,
		version,
		file_name;
	}


	public static String[] columns() {
		return new String[] {Column.country_code.name(), Column.country_name.name(), Column.version.name(), Column.file_name.name()};
	}
	
}
