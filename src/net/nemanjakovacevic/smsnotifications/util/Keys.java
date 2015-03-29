package net.nemanjakovacevic.smsnotifications.util;


public interface Keys {

	public static final String KEY_NOTIFICATIONS_STORE_LIMIT = "KEY_NOTIFICATIONS_STORE_LIMIT";
	public static final String KEY_SPAM_SENDERS = "KEY_SPAM_SENDERS";
	public static final String KEY_SPAM_STORE_LIMIT = "KEY_SPAM_STORE_LIMIT";
	
	public static final String INTENT_SHOW_NOTIFICATIONS = "net.nemanjakovacevic.smsnotifications.intents.SHOW_NOTIFICATIONS";
	public static final String INTENT_NOTIFICATION_PRODUCTION_EXCEPTION = "net.nemanjakovacevic.smsnotifications.intents.NOTIFICATION_PRODUCTION_EXCEPTION";
	public static final String INTENT_HANDLE_EXCEPTION = "net.nemanjakovacevic.smsnotifications.intents.INTENT_HANDLE_EXCEPTION";
	
	public static final String INTENT_KEY_SPAM_MESSAGE_ID = "INTENT_KEY_SPAM_MESSAGE_ID";
	public static final String INTENT_KEY_SPAM_MESSAGE_SENDER = "INTENT_KEY_SPAM_MESSAGE_SENDER";
	public static final String INTENT_KEY_SPAM_MESSAGE_DATE_TIME = "INTENT_KEY_SPAM_MESSAGE_DATE_TIME";
	public static final String INTENT_KEY_SPAM_MESSAGE_BODY = "INTENT_KEY_SPAM_MESSAGE_BODY";
	
	public static final int REQUEST_SHOW_SPAM_MESSAGE = 1;
	
	public static final int RESULT_DELETE_SPAM_MESSAGE = 1;
	
	public static final String URL_RULE_SET_INDEX = "stripped";
	public static final String URL_RULE_SET_BASE = "stripped";
	
	public static final String EMAIL_ERROR_REPORT = "stripped";
	public static final String EMAIL_AUTHOR = "stripped";
	
	public static final int NOTIFICATION_ID_ERROR = 3;
	
	public static final String MESSAGE = "MESSAGE";
	public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
	
	public static final String KEY_RULES_INITIALY_INSTALLED = "KEY_RULES_INITIALY_INSTALLED";
	public static final String KEY_USER_ACKNOWLEDGED_LACK_OF_RULES = "KEY_USER_ACKNOWLEDGED_LACK_OF_RULES";
	public static final String KEY_APP_INITIALISED = "KEY_APP_INITIALISED";
	
	public static final String MODE = "MODE";
	public static final int NEW_NOTIFICATIONS = 1;
	public static final int ALL_NOTIFICATIONS = 2;
	
	public static final String FLURRY_API_KEY = "stripped";
	
	public static final String ANALYTICS_INTRO_EVENT = "analytics_introduction_update_live";
	public static final String ANALYTICS_SPAM_SENDER_COUNT_PARAM = "spam_senders_count_live";
	
	public static final String ANALYTICS_SHOW_MORE_APPS_EVENT = "show_more_apps";
	public static final String ANALYTICS_SHOW_HELP_URL = "show_help_url";
	public static final String ANALYTICS_HELP_URL_PARAM = "help_url_param";
	
	public static final String ANALYTICS_FACEBOOK_PAGE_EVENT = "facebook_page";
	public static final String ANALYTICS_TWITTER_PAGE_EVENT = "twitter_page";
	public static final String ANALYTICS_SHARE_EVENT = "share";

}
