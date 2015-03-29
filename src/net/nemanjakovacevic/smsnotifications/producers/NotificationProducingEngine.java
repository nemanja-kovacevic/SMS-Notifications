package net.nemanjakovacevic.smsnotifications.producers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.nemanjakovacevic.smsnotifications.dao.RuleSetDAO;
import net.nemanjakovacevic.smsnotifications.domain.NotificationMessage;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.domain.Rule;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification.Type;
import net.nemanjakovacevic.smsnotifications.domain.Rule.RecognitionPattern;
import net.nemanjakovacevic.smsnotifications.exceptions.NotNotificationException;
import net.nemanjakovacevic.smsnotifications.exceptions.NotificationProductionException;
import android.content.Context;


/**
 * Engine for producing operator's notifications from sms message based on first matching rules from list of active
 * rules. To be used as a singleton.
 * 
 */
public class NotificationProducingEngine {

	/**
	 * List of active rules.
	 */
	Set<Rule> activeRules;

	private NotificationProducingEngine() {
		activeRules = new HashSet<Rule>();
	}

	/**
	 * Method for producing operator's notification out of sms message.
	 * 
	 * @param smsMessage
	 * @return List of produced notifications
	 * @throws NotNotificationException
	 *             if given sms is not an operator's notification message
	 * @throws NotificationProductionException
	 *             if error occurred in production process
	 */
	public List<OperatorNotification> produceNotifications(NotificationMessage notificationMessage) throws NotNotificationException,
			NotificationProductionException {
		Rule matchingRules = findMatchingRules(notificationMessage);
		List<OperatorNotification> notifications = produceNotifications(matchingRules, notificationMessage);
		return notifications;
	}

	/**
	 * Method for producing operator's notifications once matching rule have been found.
	 * 
	 * @param matchingRules
	 * @param smsMessage
	 * @return
	 * @throws NotificationProductionException
	 *             if error occurred in production process
	 */
	protected List<OperatorNotification> produceNotifications(Rule matchingRules, NotificationMessage notificationMessage)
			throws NotificationProductionException {
		Type type = Type.getTypeFromCode(matchingRules.getNotificationType());
		List<OperatorNotification> notifications;
		switch (type) {
		case MISSED_CALL:
			notifications = produceMissedCallNotifications(matchingRules, notificationMessage);
			break;
		case AVAILABILITY:
			notifications = produceAvailabilityNotifications(matchingRules, notificationMessage);
			break;
		default:
			throw new NotificationProductionException("Type of notification defined in not recognized", matchingRules.getCountryCode(), notificationMessage);
		}
		return notifications;
	}

	/**
	 * Method for producing missed call notifications out of sms message based on matching rules.
	 * 
	 * @param rules
	 * @param smsMessage
	 * @return
	 * @throws NotificationProductionException
	 *             if error occurred in production process
	 */
	protected List<OperatorNotification> produceMissedCallNotifications(Rule rules, NotificationMessage notificationMessage) throws NotificationProductionException {
		List<OperatorNotification> notifications = new ArrayList<OperatorNotification>();
		String body = notificationMessage.getBody();

		Pattern phoneNumberPattern = Pattern.compile(rules.getNumberPattern());
		Pattern numberOfCallsPattern = Pattern.compile(rules.getNumberOfCallsPattern());

		Pattern dateTimePattern = null;
		if (rules.getDateTimeRecognitionPattern() != null) {
			dateTimePattern = Pattern.compile(rules.getDateTimeRecognitionPattern());
		}
		Pattern datePattern = null;
		if (rules.getDateRecognitionPattern() != null) {
			datePattern = Pattern.compile(rules.getDateRecognitionPattern());
		}
		Pattern timePattern = null;
		if (rules.getTimeRecognitionPattern() != null) {
			timePattern = Pattern.compile(rules.getTimeRecognitionPattern());
		}

		String[] words = body.split("\\s");

		for (int i = 0; i < words.length; i++) {
			Matcher phoneNumberMatcher = phoneNumberPattern.matcher(words[i]);
			if (phoneNumberMatcher.find()) {
				String match = phoneNumberMatcher.group();

				String number = match;
				if (!Character.isDigit(match.charAt(match.length() - 1))) {
					number = match.substring(0, match.length() - 1);
				}

				String possibleNumberOfCalls = words[i + rules.getNumberOfCallsPosition()];
				Matcher numberOfCallsMatcher = numberOfCallsPattern.matcher(possibleNumberOfCalls);
				int numberOfCalls;
				if (numberOfCallsMatcher.find()) {
					numberOfCalls = Integer.parseInt(trim(possibleNumberOfCalls));
				} else {
					numberOfCalls = 1;
				}

				Date timestamp = new Date();
				int currentYear = timestamp.getYear();

				String dateTime = null;

				if (rules.getDateTimeRecognitionPattern() != null && dateTimePattern != null) {
					for (int j = 0; j < rules.getDateTimePositions().length; j++) {
						String possibleDateTime = words[i + rules.getDateTimePositions()[j]];
						Matcher dateTimeMatcher = dateTimePattern.matcher(possibleDateTime);
						if (dateTimeMatcher.find()) {
							dateTime = dateTimeMatcher.group();
							break;
						}
					}
				}

				String date = null;

				if (rules.getDateRecognitionPattern() != null && datePattern != null) {
					for (int j = 0; j < rules.getDatePositions().length; j++) {
						String possibleDate = words[i + rules.getDatePositions()[j]];
						Matcher dateMatcher = datePattern.matcher(possibleDate);
						if (dateMatcher.find()) {
							date = dateMatcher.group();
							break;
						}
					}
				}

				String time = null;

				if (rules.getTimeRecognitionPattern() != null && timePattern != null) {
					for (int j = 0; j < rules.getTimePositions().length; j++) {
						String possibleTime = words[i + rules.getTimePositions()[j]];
						Matcher timeMatcher = timePattern.matcher(possibleTime);
						if (timeMatcher.find()) {
							time = timeMatcher.group();
							break;
						}
					}
				}

				if(dateTime == null && date == null && time == null){
					throw new NotificationProductionException("None of dateTime, date, time components was retreived", rules.getCountryCode(), notificationMessage);
				}
				
				if (dateTime != null) {
					boolean dateTimeParsed = false;
					for (String parsingPattern : rules.getDateTimeParsingPatterns()) {
						SimpleDateFormat formatter = new SimpleDateFormat(parsingPattern);
						try {
							Date d = formatter.parse(dateTime);
							if (d.getYear() >= currentYear - 1) {
								timestamp.setYear(d.getYear());
							}
							timestamp.setMonth(d.getMonth());
							timestamp.setDate(d.getDate());
							timestamp.setHours(d.getHours());
							timestamp.setMinutes(d.getMinutes());
							dateTimeParsed = true;
						} catch (ParseException ignore) {
						}
					}
					if (!dateTimeParsed) {
						throw new NotificationProductionException("Date time not parsed but expected to", rules.getCountryCode(), notificationMessage);
					}
				}

				if (date != null) {
					boolean dateParsed = false;
					for (String parsingPattern : rules.getDateParsingPatterns()) {
						SimpleDateFormat formatter = new SimpleDateFormat(parsingPattern);
						try {
							Date d = formatter.parse(date);
							if (d.getYear() >= currentYear - 1) {
								timestamp.setYear(d.getYear());
							}
							timestamp.setMonth(d.getMonth());
							timestamp.setDate(d.getDate());
							dateParsed = true;
						} catch (ParseException ignore) {
						}
					}
					if (!dateParsed) {
						throw new NotificationProductionException("Date not parsed but expected to", rules.getCountryCode(), notificationMessage);
					}
				}

				if (time != null) {
					boolean timeParsed = false;
					for (String parsingPattern : rules.getTimeParsingPatterns()) {
						SimpleDateFormat formatter = new SimpleDateFormat(parsingPattern);
						try {
							Date d = formatter.parse(time);
							timestamp.setHours(d.getHours());
							timestamp.setMinutes(d.getMinutes());
							timeParsed = true;
						} catch (ParseException ignore) {
						}
					}
					if (!timeParsed) {
						throw new NotificationProductionException("Time not parsed but expected to", rules.getCountryCode(), notificationMessage);
					}
				}

				OperatorNotification notification = new OperatorNotification();
				notification.setType(Type.MISSED_CALL);
				notification.setNumber(number);
				notification.setNumberOfCalls(numberOfCalls);
				notification.setTimestamp(timestamp);
				notifications.add(notification);
			}
		}
		
		applyMtsNumberOfCallsQuickFix(rules, words, notifications);
		
		return notifications;
	}
	
	
	private void applyMtsNumberOfCallsQuickFix(Rule rules, String[] words, List<OperatorNotification> notifications) {
		if(isMtsProblematicCase(rules, notifications)){
			Pattern numberOfCallsPattern = Pattern.compile(rules.getNumberOfCallsPattern());
			String possibleMatch1 = words[2];
			String possibleMatch2 = words[3];
			String possibleMatch3 = words[4];
			String possibleMatch4 = words[5];
			String possibleMatch5 = words[6];
			boolean[] numbersFound = {false, false, false, false, false};
			Matcher numberOfCallsMatcher = numberOfCallsPattern.matcher(possibleMatch1);
			if(numberOfCallsMatcher.find() && possibleMatch1.charAt(possibleMatch1.length()-1) == ':'){
				numbersFound[0] = true;
			}
			numberOfCallsMatcher = numberOfCallsPattern.matcher(possibleMatch2);
			if(numberOfCallsMatcher.find() && possibleMatch2.charAt(possibleMatch2.length()-1) == ':'){
				numbersFound[1] = true;
			}
			numberOfCallsMatcher = numberOfCallsPattern.matcher(possibleMatch3);
			if(numberOfCallsMatcher.find() && possibleMatch3.charAt(possibleMatch3.length()-1) == ':'){
				numbersFound[2] = true;
			}
			numberOfCallsMatcher = numberOfCallsPattern.matcher(possibleMatch4);
			if(numberOfCallsMatcher.find() && possibleMatch4.charAt(possibleMatch4.length()-1) == ':'){
				numbersFound[3] = true;
			}
			numberOfCallsMatcher = numberOfCallsPattern.matcher(possibleMatch5);
			if(numberOfCallsMatcher.find() && possibleMatch5.charAt(possibleMatch5.length()-1) == ':'){
				numbersFound[4] = true;
			}
			
			if(getNumbersFountCount(numbersFound) == 1){
				notifications.get(0).setNumberOfCalls(1);
			}
		}
	}

	private int getNumbersFountCount(boolean[] numbersFound) {
		int count = 0;
		for(boolean b : numbersFound){
			if(b == true){
				count++;
			}
		}
		return count;
	}

	private boolean isMtsProblematicCase(Rule rules, List<OperatorNotification> notifications) {
		if(!rules.getServiceProvider().equals("mts")){
			return false;
		}
		if(notifications.size() == 1){
			return false;
		}
		if(notifications.get(0).getType() != Type.MISSED_CALL){
			return false;
		}
		return true;
	}

	private String trim(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * Method for producing missed call notifications out of sms message based on matching rules.
	 * 
	 * @param rules
	 * @param smsMessage
	 * @return List of one availability notification
	 * @throws NotificationProductionException
	 *             if error occurred in production process
	 */
	public List<OperatorNotification> produceAvailabilityNotifications(Rule rules, NotificationMessage notificationMessage)
			throws NotificationProductionException {
		List<OperatorNotification> notifications = new ArrayList<OperatorNotification>();
		String body = notificationMessage.getBody();
		Matcher matcher = Pattern.compile(rules.getNumberPattern()).matcher(body);
		if (matcher.find()) {
			String match = matcher.group();
			String number = match.substring(0, match.length() - 1);
			OperatorNotification notification = new OperatorNotification();
			notification.setType(Type.AVAILABILITY);
			notification.setNumber(number);
			notification.setTimestamp(new Date());
			notifications.add(notification);
			return notifications;
		} else {
			throw new NotificationProductionException(
					"Phone number not found in production of availability norification", rules.getCountryCode(), notificationMessage);
		}
	}

	/**
	 * Method for retrieving applicable rules for this notification based on recognition patterns in active rules.
	 * 
	 * @param smsMessage
	 *            possible operator's notification
	 * @return matching set of rules for this notification
	 * @throws NotNotificationException if sms message is not an operator's notification
	 */
	public Rule findMatchingRules(NotificationMessage notificationMessage) throws NotNotificationException {
		for (Rule rules : activeRules) {
			if (notificationMessage.getSender().equals(rules.getServiceNumber())) {
				for (RecognitionPattern rp : rules.getRecognitionPatterns()) {
					if (rp.getType() == RecognitionPattern.Type.ANYWHERE_IN_MESSAGE_BODY) {
						Matcher matcher = Pattern.compile(rp.getPattern()).matcher(notificationMessage.getBody());
						if (matcher.find()) {
							return rules;
						}
					} else if (rp.getType() == RecognitionPattern.Type.STARTS_WITH) {
						Matcher matcher = Pattern.compile(rp.getPattern()).matcher(notificationMessage.getBody());
						if (matcher.find()) {
							if (matcher.start() == 0) {
								return rules;
							}
						}
					}
				}
			}
		}
		throw new NotNotificationException();
	}

	private static NotificationProducingEngine instance;

	public static NotificationProducingEngine getInstance(Context context) {
		if (instance == null) {
			instance = createEngineAndLoadRules(context);
		}
		return instance;
	}

	private static NotificationProducingEngine createEngineAndLoadRules(Context context) {
		NotificationProducingEngine engine = new NotificationProducingEngine();
		engine.add(RuleSetDAO.loadRules(context));
		return engine;
	}
	
	public void add(Set<Rule> rules) {
		activeRules.addAll(rules);
	}
	
	public void remove(Set<Rule> rules) {
		activeRules.removeAll(rules);
	}

	public Set<String> getCountryPrefixes() {
		Set<String> countryPrefixes = new HashSet<String>();
		for(Rule rule : activeRules){
			countryPrefixes.add(rule.getCountryPrefix());
		}
		return countryPrefixes;
	}

}
