package net.nemanjakovacevic.smsnotifications.exceptions;

import net.nemanjakovacevic.smsnotifications.domain.NotificationMessage;

/**
 * Exception occurring in notification production. Carries information about
 * rules country code, stack trace, notification message recognized as notification but not processed.
 * 
 * @author nemanjak
 *
 */
public class NotificationProductionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String countryCode;
	private int rulesVersion;
	private NotificationMessage notificationMessage;
	private Status status;
	
	public NotificationProductionException(String message, String countryCode, NotificationMessage notificationMessage, Status status) {
		super(message);
		this.countryCode = countryCode;
		this.notificationMessage = notificationMessage;
		this.status = status;
	}
	
	public NotificationProductionException(String message, String countryCode, NotificationMessage notificationMessage) {
		this(message, countryCode, notificationMessage, Status.OPEN);
	}
	
	public NotificationProductionException(int id, String message, String countryCode, NotificationMessage notificationMessage, int rulesVersion) {
		this(message, countryCode, notificationMessage, Status.OPEN);
		this.id = id;
		this.rulesVersion = rulesVersion;
	}

	public int getId() {
		return id;
	};
	
	public String getRulesCountryCode() {
		return countryCode;
	}
	
	public NotificationMessage getNotificationMessage() {
		return notificationMessage;
	}

	public int getRulesVersion() {
		return rulesVersion;
	}
	
	public void setRulesVersion(int rulesVersion) {
		this.rulesVersion = rulesVersion;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public static enum Status {
		
		OPEN (1),
		REPORTED (2),
		RECOVERED (3);
		
		private int code;
		
		private Status(int code){
			this.code = code;
		}
		
		public int getCode(){
			return code;
		}
		
		public static Status getStatusFromCode(int code) {
			switch (code) {
			case 1:
				return OPEN;
			case 2:
				return REPORTED;
			case 3:
				return RECOVERED;
			default:
				return null;
			}
		}
	}
	
}
