package net.nemanjakovacevic.smsnotifications.producers;

import junit.framework.Assert;
import net.nemanjakovacevic.smsnotifications.exceptions.NotNotificationException;
import net.nemanjakovacevic.smsnotifications.producers.NotificationProducingEngine;

public class FindMatchingRuleTest extends TestSuperClass29dec2012 {

	public void testFindMatchingRulesAll() throws Throwable {
		
		NotificationProducingEngine engine = NotificationProducingEngine.getInstance(getContext());
		Assert.assertNotNull(engine.findMatchingRules(vipOneNumberOneCallNotificationSms));
		Assert.assertNotNull(engine.findMatchingRules(vipOneNumberMultipleCallsNotificationSms));
		Assert.assertNotNull(engine.findMatchingRules(vipMultipleNumbersMultipleCallsNotificationSms));
		Assert.assertNotNull(engine.findMatchingRules(vipAvailableAgainNotificationSms));
		Assert.assertNotNull(engine.findMatchingRules(vipNotBusyAnymoreNotificationSms));

		// VARIANT B
		
		Assert.assertNotNull(engine.findMatchingRules(telenorOneNumberOneCallNotificationSmsVariantB));
		Assert.assertNotNull(engine.findMatchingRules(telenorOneNumberMultipleCallsNotificationSmsVariantB));
		Assert.assertNotNull(engine.findMatchingRules(telenorMultipleNumbersMultipleCallsNotificationSmsVariantB));
		Assert.assertNotNull(engine.findMatchingRules(telenorAvailableAgainNotificationSmsVariantB));
		
		
		Assert.assertNotNull(engine.findMatchingRules(mtsOneNumberOneCallNotificationSmsNotAvailable));
		Assert.assertNotNull(engine.findMatchingRules(mtsOneNumberMultipleCallsNotificationSmsNotAvailable));
		Assert.assertNotNull(engine.findMatchingRules(mtsMultipleNumbersMultipleCallsPart1NotificationSmsNotAvailable));
		Assert.assertNotNull(engine.findMatchingRules(mtsMultipleNumbersMultipleCallsPart2NotificationSmsNotAvailable));
		Assert.assertNotNull(engine.findMatchingRules(mtsAvailableAgainNotificationSms));
		Assert.assertNotNull(engine.findMatchingRules(mtsNotBusyAnymoreMotificationSms));
		try {
			engine.findMatchingRules(randomSms1);
			fail("Exception not produced");
		} catch (NotNotificationException e) {
		}
		try {
			engine.findMatchingRules(randomSms2);
			fail("Exception not produced");
		} catch (NotNotificationException e) {
		}
		try {
			engine.findMatchingRules(randomSms3);
			fail("Exception not produced");
		} catch (NotNotificationException e) {
		}
		try {
			engine.findMatchingRules(randomSms4);
			fail("Exception not produced");
		} catch (NotNotificationException e) {
		}
	}

}
