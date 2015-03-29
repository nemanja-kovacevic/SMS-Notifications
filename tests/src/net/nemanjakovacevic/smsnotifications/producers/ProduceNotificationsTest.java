package net.nemanjakovacevic.smsnotifications.producers;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.producers.NotificationProducingEngine;

public class ProduceNotificationsTest extends TestSuperClass29dec2012 {

	
	public void testProduceMissedCallNotificationsMts() throws Throwable {
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(getContext());
		List<OperatorNotification> notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsOneNumberOneCallNotificationSmsNotAvailable), mtsOneNumberOneCallNotificationSmsNotAvailable);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		OperatorNotification notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0604749959", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(51, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsOneNumberOneCallNotificationSmsBusy), mtsOneNumberOneCallNotificationSmsBusy);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar = new GregorianCalendar();
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(57, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsOneNumberMultipleCallsNotificationSmsNotAvailable), mtsOneNumberMultipleCallsNotificationSmsNotAvailable);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0604749959", notification.getNumber());
		Assert.assertEquals(2, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(3, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsOneNumberMultipleCallsNotificationSmsBusy), mtsOneNumberMultipleCallsNotificationSmsBusy);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
		Assert.assertEquals(2, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(6, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsMultipleNumbersMultipleCallsPart1NotificationSmsNotAvailable), mtsMultipleNumbersMultipleCallsPart1NotificationSmsNotAvailable);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(4, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0604749959", notification.getNumber());
		Assert.assertEquals(2, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(47, calendar.get(Calendar.MINUTE));
		notification = notifications.get(1);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0642016105", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(47, calendar.get(Calendar.MINUTE));
		notification = notifications.get(2);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0112623606", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(46, calendar.get(Calendar.MINUTE));
		notification = notifications.get(3);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0621654066", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(46, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsMultipleNumbersMultipleCallsPart2NotificationSmsNotAvailable), mtsMultipleNumbersMultipleCallsPart2NotificationSmsNotAvailable);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0637083620", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(45, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsMultipleNumbersMultipleCallsPart1NotificationSmsBusy), mtsMultipleNumbersMultipleCallsPart1NotificationSmsBusy);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(3, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381637083620", notification.getNumber());
		Assert.assertEquals(2, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(58, calendar.get(Calendar.MINUTE));
		notification = notifications.get(1);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("0112623606", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(58, calendar.get(Calendar.MINUTE));
		notification = notifications.get(2);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(57, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(mtsMultipleNumbersMultipleCallsPart2NotificationSmsBusy), mtsMultipleNumbersMultipleCallsPart2NotificationSmsBusy);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(2, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381642016105", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(57, calendar.get(Calendar.MINUTE));
		notification = notifications.get(1);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381621654066", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(57, calendar.get(Calendar.MINUTE));
	}
	
	public void testProduceMissedCallNotificationsTelenor() throws Throwable {
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(getContext());
				
		// VARIANT A
		
		List<OperatorNotification> notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(telenorOneNumberOneCallNotificationSmsVariantB), telenorOneNumberOneCallNotificationSmsVariantB);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		OperatorNotification notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(29, calendar.get(Calendar.MINUTE));
				
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(telenorOneNumberMultipleCallsNotificationSmsVariantB), telenorOneNumberMultipleCallsNotificationSmsVariantB);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
		Assert.assertEquals(2, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(33, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(telenorMultipleNumbersMultipleCallsNotificationSmsVariantB), telenorMultipleNumbersMultipleCallsNotificationSmsVariantB);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(3, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381644755281", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(06, calendar.get(Calendar.MINUTE));
		notification = notifications.get(1);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(06, calendar.get(Calendar.MINUTE));
		notification = notifications.get(2);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381642016105", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(07, calendar.get(Calendar.MINUTE));		
	}
	
	public void testProduceMissedCallNotificationsVip() throws Throwable {
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(getContext());
		
		List<OperatorNotification> notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(vipOneNumberOneCallNotificationSms), vipOneNumberOneCallNotificationSms);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		OperatorNotification notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381644755281", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(14, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(vipOneNumberMultipleCallsNotificationSms), vipOneNumberMultipleCallsNotificationSms);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381644755281", notification.getNumber());
		Assert.assertEquals(2, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(18, calendar.get(Calendar.MINUTE));
		
		notifications = engine.produceMissedCallNotifications(engine.findMatchingRules(vipMultipleNumbersMultipleCallsNotificationSms), vipMultipleNumbersMultipleCallsNotificationSms);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(5, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381637083620", notification.getNumber());
		Assert.assertEquals(2, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(33, calendar.get(Calendar.MINUTE));
		notification = notifications.get(1);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381642016105", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(28, calendar.get(Calendar.MINUTE));
		notification = notifications.get(2);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381621654066", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(27, calendar.get(Calendar.MINUTE));
		notification = notifications.get(3);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381112623606", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(26, calendar.get(Calendar.MINUTE));
		notification = notifications.get(4);
		Assert.assertEquals(OperatorNotification.Type.MISSED_CALL, notification.getType());
		Assert.assertEquals("+381644755281", notification.getNumber());
		Assert.assertEquals(1, notification.getNumberOfCalls());
		calendar.setTimeInMillis(notification.getTimestamp().getTime());
		Assert.assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));
		Assert.assertEquals(11, calendar.get(Calendar.MONTH));
		Assert.assertEquals(22, calendar.get(Calendar.HOUR_OF_DAY));
		Assert.assertEquals(24, calendar.get(Calendar.MINUTE));
	}
	
	public void testProduceAvailabilityNotificationsMts() throws Throwable {
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(getContext());
		List<OperatorNotification> notifications = engine.produceAvailabilityNotifications(engine.findMatchingRules(mtsAvailableAgainNotificationSms), mtsAvailableAgainNotificationSms);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		OperatorNotification notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.AVAILABILITY, notification.getType());
		Assert.assertEquals("0644755281", notification.getNumber());
		
		notifications = engine.produceAvailabilityNotifications(engine.findMatchingRules(mtsNotBusyAnymoreMotificationSms), mtsNotBusyAnymoreMotificationSms);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.AVAILABILITY, notification.getType());
		Assert.assertEquals("+381644755281", notification.getNumber());
	}
	
	public void testProduceAvailabilityNotificationsTelenor() throws Throwable {
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(getContext());
		List<OperatorNotification> notifications = engine.produceAvailabilityNotifications(engine.findMatchingRules(telenorAvailableAgainNotificationSmsVariantB), telenorAvailableAgainNotificationSmsVariantB);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		OperatorNotification notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.AVAILABILITY, notification.getType());
		Assert.assertEquals("+381637083620", notification.getNumber());
	}
	
	public void testProduceAvailabilityNotificationsVip() throws Throwable {
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(getContext());
		List<OperatorNotification> notifications = engine.produceAvailabilityNotifications(engine.findMatchingRules(vipAvailableAgainNotificationSms), vipAvailableAgainNotificationSms);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		OperatorNotification notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.AVAILABILITY, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
		
		notifications = engine.produceAvailabilityNotifications(engine.findMatchingRules(vipNotBusyAnymoreNotificationSms), vipNotBusyAnymoreNotificationSms);
		Assert.assertNotNull(notifications);
		Assert.assertEquals(1, notifications.size());
		notification = notifications.get(0);
		Assert.assertEquals(OperatorNotification.Type.AVAILABILITY, notification.getType());
		Assert.assertEquals("+381604749959", notification.getNumber());
	}
	
}
